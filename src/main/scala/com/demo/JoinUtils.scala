package com.demo

import com.demo.utils.SingleSpark
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/**
  * @author dalizu on 2019/12/8.
  * @version v1.0
  *
  * 测试数据倾斜小案例
  */
class JoinUtils {

  def joinWrite(path:String): Unit = {

    val sparkSession=SingleSpark.getInstance();
    val sc=sparkSession.sparkContext

    val textRdd=sc.textFile(path)

    val struct=StructType{
      Array(
        StructField("col01",StringType),
        StructField("col02",StringType)
      )
    }

    //表1
    val df=sparkSession.createDataFrame(textRdd.map(line=>{
      val lines=line.split(",")
      Row(lines(0),lines(1))
    }),struct)

    df.createOrReplaceTempView("join01")

    //表2
    val textRdd2=sc.textFile(path)
    val df2=sparkSession.createDataFrame(textRdd.map(line=>{
      val lines=line.split(",")
      Row(lines(0),lines(1))
    }),struct)

    df.createOrReplaceTempView("join02")

    val resultDf=sparkSession.sqlContext.sql("select a.card card1,b.card card2 from join01 a left join join02 b on a.card=b.card");

    //写入mysql
    resultDf.write
        .format("jdbc")
        .option("url","jdbc:mysql://10.45.150.81:3306/test")
        .option("dbtable","xxxx")
        .option("user","root")
        .option("password","root123")
        .option("numPartitions","1")  //加上此参数,只会产生一个分区,产生一个数据库连接
      .save()

  }


}
