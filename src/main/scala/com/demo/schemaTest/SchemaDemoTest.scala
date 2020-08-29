package com.demo.schemaTest

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{StringType, StructField, StructType}

import scala.collection.mutable.ListBuffer

/**
 * @author dalizu on 2020/8/29.
 * @desc
 * @version v1.0 
 */
object SchemaDemoTest {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("JoinApp02").getOrCreate()

    val schema1=StructType{
      Array(
        StructField("c1",StringType),
        StructField("c2",StringType)
      )
    }
    val schema2=StructType{
      Array(
        StructField("c3",StringType),
        StructField("c4",StringType)
      )
    }

    val data1Rdd=spark.sparkContext.parallelize(Array(1,2))

    //按照schema转换
    val data1ResRdd=data1Rdd.map(x=>{
      val buffer=new ListBuffer[Any]
      buffer.append("A")
      buffer.append("B")
      new GenericRowWithSchema(buffer.toArray,schema1)
    })


    //rdd数量
    val label=new ListBuffer[Integer]
    label.append(1)
    label.append(2)

    //按照schema转换
    val labelInfo=label.toArray.map(x=>{
      val buffer=new ListBuffer[Any]
      buffer.append("C")
      buffer.append("D")
      new GenericRowWithSchema(buffer.toArray,schema2)
    })

    val resultRdd:RDD[Row]=data1ResRdd.flatMap(row=>{
      labelInfo.map(label=>{
        //rdd合并的效果
        val meRow=Row.merge(row,label) //没有schema
        var schemas1=row.schema
        val schemas2=label.schema
        /*println("row schema:"+schemas1)
        println("label schema:"+schemas2)*/
        for(i <-0 to schemas2.length-1){
          schemas1=schemas1.add(schemas2.apply(i))
        }

        val buffer=Row.unapplySeq(meRow).get
        new GenericRowWithSchema(buffer.toArray,schemas1)
      })
    })
    println("first schema:"+resultRdd.first().schema)
    val df01=spark.createDataFrame(resultRdd,resultRdd.first().schema)
    df01.show()
  }

}
