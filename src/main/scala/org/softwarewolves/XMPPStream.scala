package org.softwarewolves

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.packet.Packet

class XMPPStream{

	var config: Option[ConnectionConfiguration] = None
    var username: Option[String] = None
    var password: Option[String] = None
    var conn: Option[XMPPConnection] = None
    
    def setSrv(srv: String) = {
		config = Some(new ConnectionConfiguration(srv, 5222))
		this
	}
	
    def setCredentials(userName: String, passWord: String) = {
      username = Some(userName)
      password = Some(passWord)
      conn = Some(new XMPPConnection(config.get))
      conn.get.addPacketListener(new PacketListener(){
        def processPacket(packet: Packet) = {
          System.out.println("received packet " + packet.toXML)
        }
      }, new PacketFilter(){
        def accept(packet: Packet) = {
          true
        }
      })
      connectForReal
      this
    }
    
    def isConnected() = {
      conn.get.isConnected
    }
  
  	def connectForReal: Unit = {
	  conn.get.connect
	  conn.get.login(username.get, password.get)
	}
}