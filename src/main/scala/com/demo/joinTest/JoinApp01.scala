package com.demo.joinTest

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object JoinApp01 {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("JoinApp01").getOrCreate()

    val peopleInfo: RDD[(String, (String, String))] = spark.sparkContext
      .parallelize(Array(("100", "pk"), ("101", "jepson"))).map(x => (x._1, x))

    val peopleDetail: RDD[(String, (String, String, String))] = spark.sparkContext
      .parallelize(Array(("100", "ustc", "beijing"), ("103", "xxx", "shanghai")))
      .map(x => (x._1, x))


    // kv  id name school
    peopleInfo.join(peopleDetail)
        .map(x => {
          x._1 + " : " + x._2._1._2 + " : " + x._2._2._2
        })
      .foreach(println)

    Thread.sleep(20000)

    spark.stop()
  }
}
