package com.spark.study.spark.java.Streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yg on 2017/8/2.
 */
public class KafKaReceiverWC {
    //����topice ����д����
    // ./bin/kafka-topics.sh --zookeeper spark001:2181,spark002:2181,spark003:81 --topic wordcount --replication-factor 1 --partitions 1 --create
    // ./bin/kafka-console-producer.sh --topic wordcount --broker-list spark001:9092,spark002:9092,spark003:9092
    public static void main (String [] args){
        SparkConf conf = new SparkConf().setAppName("s").setMaster("local[2]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf,  Durations.seconds(5));
        // ����Ƚ���Ҫ,�Ƕ�Ӧ���topic�ü����߳�ȥ��ȡ����
        Map<String,Integer> topicThreadMap= new HashMap<String,Integer>();
        topicThreadMap.put("laoyang",1);
        // kafka���ִ�������,��pair����ʽ,������ֵ,����һ��ֵͨ������Null��
        JavaPairReceiverInputDStream<String,String> lines = KafkaUtils.createStream(
                jssc,
                "node1:2181,node2:2181,node3:2181",
                "WCConsumerGroup",
                topicThreadMap
        );

        JavaDStream<String> word =lines.flatMap(new FlatMapFunction<Tuple2<String, String>, String>() {
            @Override
            public Iterable<String> call(Tuple2<String, String> stringStringTuple2) throws Exception {
                return Arrays.asList(stringStringTuple2._2.split(" "));
            }
        });
        JavaPairDStream<String,Integer> pairs = word.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String,Integer>(s,1);
            }
        });
       JavaPairDStream<String,Integer> wc = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
           @Override
           public Integer call(Integer integer, Integer integer2) throws Exception {
               return integer+integer2;
           }
       });
       wc.print();
       jssc.awaitTermination();
       jssc.close();



    }
}
