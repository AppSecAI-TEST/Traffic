package com.spark.study.test
import scala.actors.Actor

/**
  * Created by yg on 2017/7/26.
  * g�� Actor ����case class ����Ϣ
  * case �ؼ��� ר����ģʽƥ���
  * ����һ����Ⱥ  master ������slave  ͨѶ ����������
  *spark �������� �������ȥ ����ƥ�����
  *
  * */


  case class Register(username: String, password: String)

  case class Login(username: String, password: String)

  class UserManagerActor extends Actor {
    def act {
      while (true) {
        receive{
        case Login(username, password) => println("Login"+username+password)
        case Register(usernam, password) => println("Register"+usernam+password)
      }
    }
  }

}
  object UserManagerActor{
    def main(args: Array[String]): Unit = {
      val userActor = new UserManagerActor
      userActor.start()
      userActor ! Register("laoyang","123")
      userActor ! Login("laoyang2","345")
    }
  }


