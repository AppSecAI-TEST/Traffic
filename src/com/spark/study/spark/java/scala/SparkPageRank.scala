package com.spark.study.spark.java.scala

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by yg on 2017/7/27.
  * Ȩ�����
  */
object SparkPageRank {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("PageRank").setMaster("local[100]")
    val iters = 20; //����20�� ���һ��������ֵҲ��
    val ctx = new SparkContext(sparkConf)
    val lines = ctx.textFile("D:\\bigdata\\spark\\pageRank.txt")
    //���ݱߵĹ�ϵ���������ڽӱ�(1,(2,3,4))(2,(1,3))..
    //distinc()�ظ��ı�ȥ��
    //groupByKey() ����key����
    //cache() ��Ҫ��ͣ�ĵ������԰������ݻ���
    val links = lines.map {
      s =>
      val parts = s.split("\\s+")
      (parts(0), parts(1))//�ո��ǰ����0 �ո�ĺ�����1
    }.distinct().groupByKey().cache()
    links.foreach(println)
    //mapValues (1,1.0)(2,1.0) ��ʼֵÿ���˵�Ȩ����1.0
    var ranks = links.mapValues(v => 1.0)
    ranks.foreach(println);
    for (i <- 1 to iters) {
      //(1,((2.3.4.5),1.0)) һ���˵ĺ����б� ���ϳ�ʼֵ
      /**
        * join :2��RDD���Ժϳ�һ��
        * .values=((2.3.4.5),1.0))
        * case (urls, rank)  ������������ĸ�ʽ(1,2,3),1.0 �Ͳ��ڷ�����ִ��
        *  urls.size= (2.3.4.5)
        *  urls.map(url => (url, rank / size)) ���� (2.3.4.5)���Ԫ��
        *  rank / size = 1.0 /4
        */
      val contribs = links.join(ranks).values.flatMap { case (urls, rank) =>
        val size = urls.size
        urls.map(url => (url, rank / size))

      }
      //�����ж� 2.3.4.5 �Ĺ��׶�Ҫ������� ���ֵ������ ��url, rank / size)
      ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
    }
    val output = ranks.collect()
    output.foreach(tup => println(tup._1+"====="+tup._2))
    ctx.stop()
  }
}
