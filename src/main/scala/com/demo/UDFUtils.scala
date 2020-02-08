package com.demo


import com.demo.utils.SingleSpark
import org.apache.spark.rdd.RDD

import scala.util.Random


/**
  * @author dalizu on 2020/2/8.
  * @desc
  * @version v1.0 
  */
class UDFUtils extends Serializable{

  def select(): Unit = {

    val spark=SingleSpark.getInstance();
    val sc=spark.sparkContext

   /* val userAccessLog = Array (
      "user1,product1",  // day  userid
      "user2,product3",
      "user3,product1",
      "user3,product1",
    )

    import spark.implicits._
    //Array  ===> RDD
    val infoRDD:RDD[String]=spark.sparkContext.parallelize(userAccessLog)*/

    //统计爱好的个数
    import  spark.implicits._
    val infoRDD:RDD[String]=spark.sparkContext.textFile("E:\\Ideljava\\githubWork\\web-spark\\src\\main\\scala\\com\\demo\\demo02")

    println("----------------->>>"+infoRDD.getNumPartitions)
    //repartition  加大数据所在的分区个数,使得shuffle效果明显
    val rdf=infoRDD.repartition(10)
    val df=rdf.map(_.split(",")).map(x=>{
      UserInfo(x(0),x(1))
    }).toDF()
    df.createOrReplaceTempView("user")
    //计算每个用户总共购买了多少产品
    //数据倾斜一般发生在聚合计算的时候，(shuffle阶段才会有数据倾斜) 由于相同的key过多导致的，导致有一个task可能会计算的很慢，导致整个job的时间很长！
    //spark.sql("select uid, count(1) as cnt from user group by uid").show()



    //定义函数+注册函数
    spark.udf.register("add_random_prefix",(input:String,upperLimit:Int)=>randomPrefixUDF(input,upperLimit))

    spark.udf.register("remove_random_prefix",(input:String)=>removeRandomPrefixUDF(input))

    /*spark.sql("select add_random_prefix('hive',10)").show()*/


    //先把uid打散

    //第一次聚合

    //spark.sql("select rdm_uid, count(1) as cnt from   (select add_random_prefix(uid,30) as rdm_uid from user) a      group by rdm_uid").show()

    //移除随机数

    //第二次聚合
    spark.sql("select uid, sum(cnt) as total_cnt from" +
      "(select remove_random_prefix(rdm_uid) as uid, cnt from" +
      "(select rdm_uid, count(1) as cnt from" +
      "(select add_random_prefix(uid,30) as rdm_uid from user) a group by rdm_uid) b) c group by uid").show()


  }


  def randomPrefixUDF(field: String,upperLimit:Int): String = {
    val random = new Random()
    val prefix = random.nextInt(upperLimit)
    prefix + "_" + field
  }

  def removeRandomPrefixUDF(field: String): String = {
    val split = field.split("_")
    split(1)
  }


}
case class UserInfo(uid:String,product:String)