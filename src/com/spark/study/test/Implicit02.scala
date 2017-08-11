package com.spark.study.test

/**
  * Created by yg on 2017/7/26.
  * ��ʿ����
  */
class SingnPen{
  def write(content :String) =println(content)
}
object ImplicitContext{
  implicit  val singnPen = new SingnPen
}
//�Լ����ʺ�ʹ�ù��õı�
object Implicit02 {
  def  signForExam(name:String)(implicit singnPen: SingnPen): Unit ={//���ﻯ ������ ���ǹ��õ�
    singnPen.write(name +"VBVCVCV")
  }

  def main(args: Array[String]): Unit = {
    import ImplicitContext._ //���ǰ� singnPen ���嵽���
    //Ҳ��������
    //val singnPen = new SingnPen
    //signForExam("laoyang")(singnPen)
    signForExam("laoyang")
  }
}
