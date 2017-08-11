package com.spark.study.spark.java.Streaming;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by yg on 2017/8/2.
 *
 */

//�����
public class KafkaDirectWC {
    public static void main (String[] args){
        SparkConf conf = new SparkConf().setAppName("c").setMaster("local[1]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        // ���������ǲ���Ҫzookeeper�ڵ�İ�,�������������broker.list
        Map<String,String> kafkaParams = new HashMap<String,String>();
        // Ȼ�󴴽�һ��set,���������Ҫ��ȡ��Topic,�������������˵��,���������ĺܺ�,���Բ��ж�ȡ���topic
        kafkaParams.put("metadata.broker.list","node1:9092,node2:9092,node3:9092");
        Set<String> topics = new HashSet<String>();
        topics.add("laoyang");
        JavaPairInputDStream<String,String> lines = KafkaUtils.createDirectStream(
                jssc,
                String.class, // key����
                String.class, // value����
                StringDecoder.class, // ������
                StringDecoder.class,
                kafkaParams,
                topics
        );
        JavaDStream<String> words =lines.flatMap(new FlatMapFunction<Tuple2<String, String>, String>() {
            @Override
            public Iterable<String> call(Tuple2<String, String> stringStringTuple2) throws Exception {
                return Arrays.asList(stringStringTuple2._2.split(" "));
            }
        });
        JavaPairDStream<String,Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String,Integer> call(String s) throws Exception {
                return new Tuple2<String,Integer>(s,1);
            }
        });
        JavaPairDStream<String,Integer> wc =pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer+integer2;
            }
        });
        wc.print();
        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }
}
