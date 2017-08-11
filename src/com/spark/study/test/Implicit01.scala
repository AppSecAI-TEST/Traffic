package com.spark.study.test

/**
  * Created by yg on 2017/7/26.
  * ��ʽת�� ����Ľӿ� ֻ����ѧ������
  * ���Ͳ��Գ��� ��ʽת��
  * SpecialPerson ��ʹ�ýӿ�
  */
class SpecialPerson(val name : String)
class Student(val name : String)
class Older(val name :String)
class Teacher(val name:String)
object Implicit01{
  implicit def object2SpecialPerson(obj:Object):SpecialPerson={//object2SpecialPerson( ʲô����ת����ʲô�� Ĭ�ϵ�д��
    if(obj.getClass==classOf[Student]){ //l�����
      val stu =obj.asInstanceOf[Student]//��ת��
      new SpecialPerson(stu.name)
    }
    else if(obj.getClass==classOf[Older]){
        val Older =obj.asInstanceOf[Older]
      new SpecialPerson(Older.name)
    }
    else{
      Nil
    }
  }
  var ticketNumber =0;
  def buySpecialTicket(p:SpecialPerson)={
    ticketNumber+=1
    "T_"+ticketNumber
  }

  def main(args: Array[String]): Unit = {
    val laoyang = new Student("laoyang")
    println(buySpecialTicket(laoyang))

  }
}
