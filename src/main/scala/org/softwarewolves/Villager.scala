package org.softwarewolves

import akka.actor.{Actor, ActorRef, FSM}
import scala.concurrent.duration._
import akka.actor.actorRef2Scala
import akka.actor.ActorSystem
import akka.actor.Props
import org.jivesoftware.smack.packet.Message

  
object Villager extends App {
  trait State
  case class Initial() extends State
  case class InRoom() extends State
  
  val name = "francis"
  val passwd = "francis"
  val nickname = "Frank"
  val xmppSrv = "softwarewolves.org"
  val system = ActorSystem(name)
  val conn = system.actorOf(Props[Connection], "connection")
  conn ! Connect(xmppSrv)
  conn ! Login(name, passwd, nickname)
  val gc = system.actorOf(Props(new GameCoordinatorProxy(conn)))
  val myBot = system.actorOf(Props(new Villager(name, passwd, gc)))
  Console.println(nickname + " is going to play softwarewolves. Yay.")
}

class Villager(username: String, password: String, gc: ActorRef) extends Actor {
  
  import Villager._
  
  var room: Option[ActorRef] = None
  
  gc ! "I want to play"

  def receive = {
    
    case room: ActorRef => {
      Console.println("whoopee, the room!")
      this.room = Some(room)
      room ! "howdy"
    }
    case msg: Message => {
      Console.println(msg.getFrom().split("/")(1) + " says: " + msg.getBody())
    }
  }
}