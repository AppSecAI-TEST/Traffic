package com.spark.study.test

/**
  * Created by yg on 2017/7/25.
  */
object TestCollection {
  def main(args: Array[String]): Unit = {
     var t =List(1,2,3)
    //println(t(2)) //�±��0��ʼ
    println(t.map(a=>{println(a);a+2}))//List(3, 4, 5) //������̫��
    println(t.map(_+1))//_ÿ��Ԫ��
    var t2 =t.+:("test")
   // println(t2)//List(test, 1, 2, 3)
    t2 =t::6::Nil
    println(t2) //List(List(1, 2, 3), 6)
    t2.foreach(t=>print(t))
    println(t.distinct)//ȥ��
    println(t.slice(0,2))//�з�
    println(t./:(0)({//   /:
      (sum,num)=>print(sum+"=="+num);
        sum+num
    }));

    //Ԫ��
    var tuple01 = (1,2,3); //index ��1��ʼ
    println(tuple01._1)//._1) ȡֵ
  }
}
