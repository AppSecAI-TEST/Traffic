package com.spark.study.spark.java.scala

import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by yg on 2017/7/27.
  */
object MySparkPI {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("local[1]")
    val spark = new SparkContext(conf) //
    val slices =10;
    val n =1000*slices
        //parallelize() ���ص����ݼ��ϲ��л�
    //1��10000���е�10������ȥ ÿ���� 1000
    val count =spark.parallelize(1 to n,slices).map({
      i=>

        /**
          * ���ؿ����㷨 ����
          * i=> �����Ƿ����� ��û���õ�i ֻ��ͨ�����ַ�ʽ������n
          * random()  ��Χ�� 0 �� 1
          * random * 2  0 �� 2
          * random * 2 -1  �� -1 �� 1
          * x*x +y*y   ƽ���뾶
          * x*x +y*y <1  ��Բ�� ��1  ��Բ���� 0
          * .reduce(_+_) ���ڵ�2�����ۼ�
          */

        def random : Double =java.lang.Math.random()
        val x =random * 2 -1
        val y =random * 2 -1
        println(x + "============="+ y)
        if(x*x +y*y <1 ) 1 else 0

    }).reduce(_+_)
    println("PI"+4.0*count/n)
    spark.stop()
  }
}
