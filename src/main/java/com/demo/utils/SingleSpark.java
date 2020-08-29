package com.demo.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * @author dalizu on 2019/11/13.
 * @version v1.0
 * @desc 获取SparkSession
 */
public class SingleSpark {


    private static volatile SparkSession sparkSession=null;

    private SingleSpark(){}

    public static SparkSession getInstance(){
        if(sparkSession==null){

            synchronized (SingleSpark.class){

                if(sparkSession==null){

                    SparkConf sparkConf=new SparkConf();
                    sparkConf.set("spark.cores.max", "2");
                    sparkSession=SparkSession.builder()
                            .config(sparkConf)
                            .master("local[5]")
                            .getOrCreate();
                }

            }

        }
        return sparkSession;
    }

}
