
package com.spark.study.spark.java.Streaming;

import com.google.common.base.Optional;
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

import java.util.Arrays;
import java.util.List;

/**
 * Created by yg on 2017/8/4.
 */
public class UpdateStateByKeyWC {
    public static void main (String[] args){
        SparkConf conf = new SparkConf().setAppName("c").setMaster("local[2]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));
        // ��һ��,���Ҫʹ��updateStateByKey����,�ͱ�������һ��checkpointĿ¼,����checkpoint����
        jssc.checkpoint("hdfs://node1:9000/wc_checkpoint");
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("node1",9999);
        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String s) throws Exception {
                return Arrays.asList(s.split(" "));
            }
        });
        JavaPairDStream<String,Integer>pairs =words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String,Integer>(s,1);
            }
        });
        // updateStateByKey,�Ϳ���ʵ��ֱ��ͨ��sparkά��һ��ÿ�����ʵ�ȫ�ֵ�ͳ�ƴ���

        JavaPairDStream<String,Integer> wc = pairs.updateStateByKey(
                // �����Optional,�൱��scala�е�������,����Option,�������������һ��״̬,����֮ǰ����,Ҳ����֮ǰ������

                new Function2<List<Integer>, Optional<Integer>, Optional<Integer>>() {
                    // ʵ����,����ÿ������,ÿ��batch�����ʱ��,��������������,��һ������,values�൱�����batch��,���key���µ�ֵ,
                    // �����ж��,����һ��hello,������2��1,(hello,1) (hello,1) ��ô�������(1,1)
                    // ��ô�ڶ���������ʾ�������key֮ǰ��״̬,��ʵ���͵Ĳ��������Լ�ָ����
                    @Override
                    public Optional<Integer> call(List<Integer> values, Optional<Integer> state) throws
                            Exception {
                        Integer newValue=0;
                        if(state.isPresent()){
                            newValue=state.get();
                        }
                        for(Integer value : values){
                            newValue += value;
                        }


                        return Optional.of(newValue);
                    }
                }
        );
        wc.print();
        jssc.start();
        jssc.awaitTermination();
        jssc.close();

    }
}