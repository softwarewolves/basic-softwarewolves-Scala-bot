package org.softwarewolves

import akka.actor.FSM
import akka.actor.Actor
import org.jivesoftware.smack.packet.Message
import akka.actor.ActorRef
import akka.actor.Props
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smack.packet.Packet
import org.jivesoftware.smack.PacketListener

sealed trait ConnectionState
case class Initial() extends ConnectionState
case class Connected() extends ConnectionState
case class LoggedIn() extends ConnectionState
case class InRoom() extends ConnectionState

sealed trait ConnectionCommand
case class Connect(srv: String) extends ConnectionCommand
case class Login(username: String, password: String, nickname: String) extends ConnectionCommand
case class SendMessage(msg: Message) extends ConnectionCommand
case class WantToPlay(msg: Message) extends ConnectionCommand
case class Invite(room: MultiUserChat) extends ConnectionCommand
case class FromRoom(packet: Packet) extends ConnectionCommand

class Connection extends Actor with FSM[ConnectionState, XMPPStream] {
  
  var xmppSrv: Option[String] = None
  var gc: Option[ActorRef] = None
  var room: Option[ActorRef] = None
  
  startWith(Initial(), new XMPPStream(context.self))
  when(Initial()){
    case Event(Connect(srv), d) => {
      xmppSrv = Some(srv)
      goto(Connected()) using d.setSrv(srv)
    }
  }
  when(Connected()){
    case Event(Login(userName, password, nickname), d) =>{
      goto(LoggedIn()) using d.setPrincipal(userName, password, nickname)
    }
  }
  when(LoggedIn()){
    case Event(WantToPlay(msg), d) => {
      gc = Some(sender)
      msg.setTo(msg.getTo() + "@" + xmppSrv.get)
      msg.setFrom(d.username.get + "@" + xmppSrv.get)
      d.sendMsg(msg)
      stay
    }
    case Event(Invite(r), d) => {
      room = Some(context.system.actorOf(Props(new Room(r, context.self))))
      r.addMessageListener(new PacketListener() {
          override def processPacket(message: Packet): Unit = {
            self ! FromRoom(message)
          }
        })
      gc.get ! room.get
      goto(InRoom()) using d
    }
  }
  when(InRoom()){
    case Event(SendMessage(msg), d) => {
      d.sendMsg(msg)
      stay
    }
    case Event(FromRoom(msg), d) => {
      Console.println("received a message from the room: " + msg.toXML())
      room.get ! msg
      stay
    } 
  }
  initialize
}