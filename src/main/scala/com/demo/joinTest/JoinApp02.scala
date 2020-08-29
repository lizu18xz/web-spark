package com.demo.joinTest

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object JoinApp02 {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("JoinApp02").getOrCreate()

    // 广播变量是把小表的数据通过sc广播出去
    val peopleInfo: collection.Map[String, String] = spark.sparkContext
      .parallelize(Array(("100", "pk"), ("101", "jepson"))).collectAsMap()

    val peopleBroadcast: Broadcast[collection.Map[String, String]] = spark.sparkContext.broadcast(peopleInfo)

    val peopleDetail: RDD[(String, (String, String, String))] = spark.sparkContext
      .parallelize(Array(("100", "ustc", "beijing"), ("103", "xxx", "shanghai")))
      .map(x => (x._1, x))


    // TODO... 是Spark以及广播变量实现join
    // mapPartitions做的事情： 遍历大表的每一行数据  和 广播变量的数据对比 有就取出来，没有就拉倒
    peopleDetail.mapPartitions(x => {

      val broadcastPeople: collection.Map[String, String] = peopleBroadcast.value

      for((key,value) <- x if broadcastPeople.contains(key))
        yield (key, broadcastPeople.get(key).getOrElse(""), value._2)
    }).foreach(println)


    Thread.sleep(20000)

    spark.stop()
  }
}
