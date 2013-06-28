package org.softwarewolves

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.packet.Packet
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.InvitationListener
import akka.actor.ActorRef
import org.jivesoftware.smack.PacketInterceptor
import org.jivesoftware.smack.MessageListener

class XMPPStream(actor: ActorRef) {

  var config: Option[ConnectionConfiguration] = None
  var username: Option[String] = None
  var password: Option[String] = None
  var conn: Option[XMPPConnection] = None

  def setSrv(srv: String) = {
    config = Some(new ConnectionConfiguration(srv, 5222))
    this
  }

  def setPrincipal(userName: String, passWord: String, nickname: String) = {
    username = Some(userName)
    password = Some(passWord)
    conn = Some(new XMPPConnection(config.get))
    connectForReal(nickname)
    this
  }

  def isConnected() = {
    conn.get.isConnected
  }

  def connectForReal(nickname: String) = {
    conn.get.connect
    conn.get.login(username.get, password.get)

    MultiUserChat.addInvitationListener(conn.get, new InvitationListener() {
      override def invitationReceived(c: org.jivesoftware.smack.Connection, room: String,
        inviter: String,
        reason: String, password: String, msg: Message) = {
        Console.println("invitation received from " + inviter + " for " + room
          + ". He says: \"" + reason + "\"")
        val gameChatRoom = new MultiUserChat(conn.get, room)
        gameChatRoom.join(nickname)
        actor ! Invite(gameChatRoom)
      }
    })

    this
  }

  def sendMsg(msg: Message) = {
    conn.get.sendPacket(msg)
    this
  }
}
