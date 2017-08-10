package com.spark.study.spark.java.SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.util.List;

/**
 * Created by yg on 2017/8/10.
 */
public class RDD2DataFrameReflection{ //���� /��ҪSQL��Ҫ��RDDת��DataFrame �÷�����ʵ��Reflection
    public static void main (String [] args){
        SparkConf conf = new SparkConf().setAppName("d").setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);
        final JavaRDD<String> lines = sc.textFile("order.text");
        JavaRDD<ST> st = lines.map(new Function<String, ST>() {
            @Override
            public ST call(String s) throws Exception {
                String[] lineSplited = s.split(",");
                ST sut = new ST();
                sut.setId(Integer.valueOf(lineSplited[0].trim()));
                sut.setName(lineSplited[1]);
                return sut;
            }
        });
        // ʹ�÷��䷽ʽ��RDDת��ΪDataFrame
        DataFrame stDF = sqlContext.createDataFrame(st,ST.class);
        stDF.registerTempTable("order");
        // ����DataFrame��Ϳ���ע��Ϊһ����ʱ��,
        DataFrame teenagerDF = sqlContext.sql("select * from t_ord_saleorder");
        // �ٴ�ת��RDD,ӳ��ΪStudent
        JavaRDD<Row> teemagerRDD =teenagerDF.javaRDD();
        JavaRDD<ST> tenSRDD =teemagerRDD.map(new Function<Row, ST>() {
            @Override
            public ST call(Row row) throws Exception {
                System.out.println(row.get(0));
                System.out.println(row.get(1));
                System.out.println(row.get(2));
                String name = row.getString(2);
                int age = row.getInt(2);
                ST st = new ST();
                st.setName(name);
                st.setAge(age);
                return st;
            }
        });

        List<ST> stlist =tenSRDD.collect();
        for(ST st1 : stlist){
            System.out.println(st1);
        }


    }
}
