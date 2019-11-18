package com.demo.utils;

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
                    sparkSession=SparkSession.builder()
                            /*.master("local[5]")*/
                            .getOrCreate();
                }

            }

        }
        return sparkSession;
    }

}
