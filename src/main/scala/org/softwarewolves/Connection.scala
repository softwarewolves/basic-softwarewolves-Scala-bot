package org.softwarewolves

import akka.actor.FSM
import akka.actor.Actor

sealed trait ConnectionState
case class Initial() extends ConnectionState
case class Connected() extends ConnectionState
case class LoggedIn() extends ConnectionState

sealed trait ConnectionCommand
case class Connect(srv: String) extends ConnectionCommand
case class Login(username: String, password: String) extends ConnectionCommand

class Connection  extends Actor with FSM[ConnectionState, XMPPStream] {
  startWith(Initial(), new XMPPStream)
  when(Initial()){
    case Event(Connect(srv), d) => 
      goto(Connected()) using d.setSrv(srv)
  }
  when(Connected()){
    case Event(Login(userName, password), d) =>{
      goto(LoggedIn()) using d.setCredentials(userName, password)
    }
  }
  when(LoggedIn()){
    case Event(_, _) => throw new RuntimeException("not yet implemented")
  }
  initialize
}