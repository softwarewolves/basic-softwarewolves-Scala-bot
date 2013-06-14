package org.softwarewolves

import akka.actor.{Actor, ActorRef, FSM}
import akka.actor.actorRef2Scala

  
object Villager {
  trait State
  case class Initial() extends State
  case class InRoom() extends State
}

class Villager(username: String, password: String, gc: ActorRef) extends Actor 
	with FSM[Villager.State, String]{
  
  import Villager._
  
  gc ! "I want to play"
  startWith(Initial(), "waking up")
  when(Initial()){
    case Event(_, _) => 
      goto(InRoom()) using "hoping to be invited"
  }
  initialize
}