package org.softwarewolves

import akka.actor.Actor
import akka.actor.ActorRef
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smack.PacketInterceptor
import org.jivesoftware.smack.packet.Packet

case class NickName(nick: String)

class Room(room: MultiUserChat, conn: ActorRef) extends Actor {
  
  room.addPresenceInterceptor(new PacketInterceptor(){
    override def interceptPacket(packet: Packet){
      Console.println("sending Presence to the room: " + packet.toXML)
    }    
  })
  
  def receive = {
    
    case NickName(nick) => {
		room.join(nick)
    }
    case msg: Message => room.sendMessage(msg)
    case msg: String => room.sendMessage(msg)
      
  }

}