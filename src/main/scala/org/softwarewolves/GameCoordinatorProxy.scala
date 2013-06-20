package org.softwarewolves

import akka.actor.{ Actor, ActorRef }
import akka.actor.FSM
import org.jivesoftware.smack.packet.Message

class GameCoordinatorProxy(connection: ActorRef) extends Actor {

  var room: Option[ActorRef] = None
  var player: Option[ActorRef] = None

  def receive = {
    case "I want to play" => {
      if (room.isDefined) {
        sender ! room
      } else {
        val msg = new Message()
        msg.setTo("sww")
        msg.addBody("EN", "I want to play")
        connection ! WantToPlay(msg)
        player = Some(sender)
      }
    }
    case r: ActorRef => {
      room = Some(r)
      player.get ! r
    }
  }
}