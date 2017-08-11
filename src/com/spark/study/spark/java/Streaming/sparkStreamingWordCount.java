package com.spark.study.spark.java.Streaming;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;


import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

/**
 * Created by yg on 2017/8/1.
 */
public class sparkStreamingWordCount {
    public static void main(String[] args) throws InterruptedException {
        Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
        SparkConf conf = new SparkConf().setAppName("wordcount").setMaster("local[2]");
        // �����ö����������Spark Core�е�JavaSparkContext,������Spark SQL�е�SQLContext
        // �ö�����˽���SparkConf����,��Ҫ����һ��Batch Interval����,����˵,ÿ�ռ��೤ʱ�����ݻ���һ��batchȥ���д���
        // �������ǿ�Durations����������÷��ӡ����롢��,��������һ��
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10)); //����и��RDD

        // ���ȴ�������DStream,����һ������Դ�����socket��kafka���������ϵĽ���ʵʱ������
        // ����һ�������˿ڵ�socket������,������ͻ���ÿ��һ������һ��RDD,RDD��Ԫ������ΪString����һ��һ�е��ı�
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("node1", 9999);//���ܶ˿ڵ�����
        // ����Spark Core�ṩ������ֱ��Ӧ����DStream�ϼ���,���ӵײ��Ӧ���������ÿ��RDD����,RDDת�������RDD����Ϊ��DStream��RDD
        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>(){

            private static final long serialVersionUID = 1L;

            @Override
            public Iterable<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" "));
            }

        });

        JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>(){

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }

        });

        JavaPairDStream<String, Integer> wordcounts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>(){

            private static final long serialVersionUID = 1L;

            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }

        });

        // ���ÿ�μ�����,����ӡһ����һ���ӵĵ��ʼ������,������5����,�Ա������ǲ��Ժ͹۲�
        wordcounts.print();

        // �������start����,����spark streamingӦ�òŻ�����ִ��,Ȼ��������,���close�ͷ���Դ
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}
