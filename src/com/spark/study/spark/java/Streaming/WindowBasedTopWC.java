package com.spark.study.spark.java.Streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.List;

/**
 * Created by yg on 2017/8/4.
 * ÿ��10���֮ǰ1���ӵ��ռ�����־����
 */
public class WindowBasedTopWC {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("wordcount").setMaster("local[2]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf,Durations.seconds(5));

        // ������־��, yasaka hello, lily world,������־����Ҫ��ѧϰ��ôʹ��Spark Streaming��
        JavaReceiverInputDStream<String> searchLog = jssc.socketTextStream("spark001", 9999);
        // ��������־ת����ֻ��һ�������ʼ���
        JavaDStream<String> searchWordDStream = searchLog.map(new Function<String,String>(){

            private static final long serialVersionUID = 1L;

            @Override
            public String call(String searchLog) throws Exception {
                return searchLog.split(" ")[1];
            }

        });

        // ��������ӳ��Ϊ(searchWord, 1)��Tuple��ʽ
        JavaPairDStream<String, Integer> searchWordPairDStream = searchWordDStream.mapToPair(new PairFunction<String,String,Integer>(){

            private static final long serialVersionUID = 1L;

            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String,Integer>(word,1);
            }

        }) ;

        JavaPairDStream<String, Integer> searchWordCountsDStream =
                searchWordPairDStream.reduceByKeyAndWindow(new Function2<Integer,Integer,Integer>(){

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Integer call(Integer v1, Integer v2) throws Exception {
                        return v1+v2;
                    }

                }, Durations.seconds(60), Durations.seconds(10));

        // ��������Ѿ�ÿ��10���֮ǰ60���ռ����ĵ���ͳ�Ƽ���,12��RDD ���5���и�RDD 1���� Ҳ��12��RDD,ִ��transform������Ϊһ������60�����ݻ���һ��RDD
        // Ȼ�����һ��RDD����ÿ�������ʳ���Ƶ�ʽ�������Ȼ���ȡ����ǰ3�ȵ�������,���ﲻ��transform��transformToPair���ؾ��Ǽ�ֵ��
        JavaPairDStream<String,Integer> finalDStream = searchWordCountsDStream.transformToPair(
                new Function<JavaPairRDD<String,Integer>,JavaPairRDD<String, Integer>>(){

                    private static final long serialVersionUID = 1L;

                    @Override
                    public JavaPairRDD<String, Integer> call(
                            JavaPairRDD<String, Integer> searchWordCountsRDD) throws Exception {
                        // ��תȻ���������
                        JavaPairRDD<Integer,String> countSearchWordsRDD = searchWordCountsRDD
                                .mapToPair(new PairFunction<Tuple2<String,Integer>,Integer,String>(){

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Tuple2<Integer, String> call(
                                            Tuple2<String, Integer> tuple) throws Exception {
                                        return new Tuple2<Integer,String>(tuple._2,tuple._1);
                                    }

                                });

                        JavaPairRDD<Integer,String> sortedCountSearchWordsRDD = countSearchWordsRDD.
                                sortByKey(false); //����
                            //�ڷ�ת����
                        JavaPairRDD<String,Integer> sortedSearchWordsRDD = sortedCountSearchWordsRDD
                                .mapToPair(new PairFunction<Tuple2<Integer,String>,String,Integer>(){

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public Tuple2<String,Integer> call(
                                            Tuple2<Integer,String> tuple) throws Exception {
                                        return new Tuple2<String,Integer>(tuple._2,tuple._1);
                                    }

                                });

                        List<Tuple2<String,Integer>> topSearchWordCounts = sortedSearchWordsRDD.take(3);
                        for(Tuple2<String,Integer> wordcount : topSearchWordCounts){
                            System.out.println(wordcount._1 + " " + wordcount._2);
                        }
                        return searchWordCountsRDD;
                    }

                }	);

        // ����޹ؽ�Ҫ,ֻ��Ϊ�˴���job��ִ��,���Ա�����action����
        finalDStream.print();

        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }
}

