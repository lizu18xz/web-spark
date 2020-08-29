package com.demo.kryo

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
 * @author dalizu on 2020/8/29.
 * @desc
 * @version v1.0
 *
 *          java:MEMORY_ONLY   34.3MB
 *          java:MEMORY_ONLY_SER    25.3MB
 *
 *          kryo:MEMORY_ONLY   60.9MB
 *          kryo:MEMORY_ONLY_SER    21.8MB
 *
 */
object KryoDemo {


  def main(args: Array[String]): Unit = {

    val sparkConf=new SparkConf()

    sparkConf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")

    //必须注册，kryo的序列化,否则不生效
    sparkConf.registerKryoClasses(Array(classOf[Info]))


    val spark = SparkSession.builder().master("local").appName("JoinApp01").getOrCreate()

    val infos=new ArrayBuffer[Info]()

    val names=Array[String]("PK","ll","ss")
    val genders=Array[String]("male","nv")
    val addresss=Array[String]("beiin","sh")

    for(i<-1 to 1000000){
      val name=names(Random.nextInt(3))
      val age=11
      val gender="man"
      val address="beijing"

      infos+=Info(name,age,gender,address)
    }

    val rdd=spark.sparkContext.parallelize(infos)

    //比较两种情况占用内存的大小
    //rdd.persist(StorageLevel.MEMORY_ONLY)

    //序列化方式,默认java序列化
    rdd.persist(StorageLevel.MEMORY_ONLY_SER)

    println(rdd.count())

  }


  case class Info(name:String,age:Int,gender:String,address:String)

}
