package org.softwarewolves

import akka.actor.{Actor, ActorRef, FSM}
import scala.concurrent.duration._
import akka.actor.actorRef2Scala
import akka.actor.ActorSystem
import akka.actor.Props

  
object Villager extends App {
  trait State
  case class Initial() extends State
  case class InRoom() extends State
  
  val name = "francis"
  val passwd = "francis"
  val nickname = "Frank"
  val xmppSrv = "awtest1.vm.bytemark.co.uk"
  val system = ActorSystem(name)
  val conn = system.actorOf(Props[Connection], "connection")
  conn ! Connect(xmppSrv)
  conn ! Login(name, passwd, nickname)
  val gc = system.actorOf(Props(new GameCoordinatorProxy(conn)))
  val myBot = system.actorOf(Props(new Villager(name, passwd, gc)))
  Console.println(name + " is going to play softwarewolves. Yay.")
}

class Villager(username: String, password: String, gc: ActorRef) extends Actor {
  
  import Villager._
  
  gc ! "I want to play"

  def receive = {
    
    case r: ActorRef => {
      Console.println("whoopee, the room!")
      r ! "howdy"
    }
  }
}