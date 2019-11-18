package com.demo

import com.demo.utils.SingleSpark
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._

/**
  * @author dalizu on 2019/11/12.
  * @version v1.0
  */

class ParquetUtils extends Serializable{


  def write(path:String,outPut:String): Unit = {


    val sparkSession=SingleSpark.getInstance();
    val sc=sparkSession.sparkContext

    val textRdd=sc.textFile(path)

    val struct=StructType{
      Array(
        StructField("col01",StringType),
        StructField("col02",StringType)
      )
    }

    val df=sparkSession.createDataFrame(textRdd.map(line=>{
      val lines=line.split(",")
      Row(lines(0),lines(1))
    }),struct)


    df.select("*").show()

    df.write.parquet(outPut)

    val outDF = sparkSession.read.parquet(outPut)

    // Parquet files can also be used to create a temporary view and then used in SQL statements
    outDF.createOrReplaceTempView("parquetFile")
    val col01DF = sparkSession.sql("SELECT col01 FROM parquetFile")

    col01DF.show()

  }

}
