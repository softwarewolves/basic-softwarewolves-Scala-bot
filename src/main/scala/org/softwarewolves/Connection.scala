package org.softwarewolves

import akka.actor.FSM
import akka.actor.Actor
import org.jivesoftware.smack.packet.Message
import akka.actor.ActorRef
import akka.actor.Props
import org.jivesoftware.smackx.muc.MultiUserChat

sealed trait ConnectionState
case class Initial() extends ConnectionState
case class Connected() extends ConnectionState
case class LoggedIn() extends ConnectionState
case class InRoom(room: String) extends ConnectionState

sealed trait ConnectionCommand
case class Connect(srv: String) extends ConnectionCommand
case class Login(username: String, password: String, nickname: String) extends ConnectionCommand
case class SendMessage(msg: Message) extends ConnectionCommand
case class WantToPlay(msg: Message) extends ConnectionCommand
case class Invite(room: MultiUserChat) extends ConnectionCommand

class Connection extends Actor with FSM[ConnectionState, XMPPStream] {
  
  var xmppSrv: Option[String] = None
  var gc: Option[ActorRef] = None
  
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
      d.sendMsg(msg)
      stay
    }
    case Event(SendMessage(msg), d) => {
      d.sendMsg(msg)
      stay
    }
    case Event(Invite(room), d) => {
      gc.get ! context.system.actorOf(Props(new Room(room, context.self)))
      stay
    }
  }
  initialize
}