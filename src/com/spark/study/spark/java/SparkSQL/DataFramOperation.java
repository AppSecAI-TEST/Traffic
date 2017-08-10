package com.spark.study.spark.java.SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Created by yg on 2017/8/10.
 */
public class DataFramOperation {
    public static void main(String[] args){
        SparkConf conf = new SparkConf().setAppName("da").setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlcon = new SQLContext(sc);
        DataFrame df = sqlcon.read().json("hdfs://node01/laoyang.json");
        df.show();
        // ��ӡԪ����
        df.printSchema();
        // ��ѯ�в�����
        df.select("name").show();
        df.select(df.col("name"),df.col("age").plus(1)).show();
        df.filter(df.col("age").gt(111)).show();// ����
        df.groupBy(df.col("age")).count().show();// ����ĳһ�з���Ȼ��Count

    }
}
