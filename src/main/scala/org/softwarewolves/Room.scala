package org.softwarewolves

import akka.actor.Actor
import akka.actor.ActorRef
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smack.PacketInterceptor
import org.jivesoftware.smack.packet.Packet

case class NickName(nick: String)

class Room(room: MultiUserChat, conn: ActorRef) extends Actor {
  
  var villager: Option[ActorRef] = None
  
  def receive = {
    
    case msg: Message => villager.get ! msg
    case msg: String => {
      villager = Some(sender)
      room.sendMessage(msg)
    }
      
  }

}