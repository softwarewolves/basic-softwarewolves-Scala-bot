package org.softwarewolves

import akka.testkit.{ TestKit, TestActorRef, TestFSMRef }
import akka.actor.{ ActorSystem, Actor, Props, FSM, ActorRef}
import org.scalatest.{ BeforeAndAfter, FunSpec }
import akka.testkit.{ TestProbe, ImplicitSender }
import akka.util.duration._
import akka.dispatch.Await
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Presence.Type._
import org.jivesoftware.smack.packet.Message

class TestXMPPStream(actor: ActorRef) extends XMPPStream(actor) {
  override def connectForReal(nickname: String): TestXMPPStream = {
	  this
  }
  override def sendMsg(msg: Message): XMPPStream = {
    this
  }
}

class TestConnection extends Connection {
  startWith(Initial(), new TestXMPPStream(self))
}

class BasicBotSpec extends TestKit(ActorSystem("Softwarewolves"))
  with ImplicitSender
  with FunSpec
  with BeforeAndAfter {
  
  val conn = TestFSMRef(new TestConnection)

  describe("an XMPPConnection") {

    it("connects to the configured server after receiving its URL") {
      conn ! Connect("awtest1.vm.bytemark.co.uk")
      assert(conn.stateName == Connected())
      assert(conn.stateData.config.get.getHost == "awtest1.vm.bytemark.co.uk")
    }
    it("goes into the LoggedIn state after receiving username and password") {
      conn ! Login("francis", "francis", "francis")
      assert(conn.stateName == LoggedIn())
      conn.stateData.conn.get.disconnect(new Presence(unavailable))
    }
    it("remembers the username") {
      assert(conn.stateData.username.get == "francis")
      assert(!conn.stateData.isConnected)
    }
  }

  describe("a GameCoordinatorProxy") {
    it("should return a room") {
      val gc = TestActorRef(new GameCoordinatorProxy(self))
      gc ! "I want to play"
      expectMsgClass(classOf[WantToPlay])
    }
  }

  describe("a Villager") {
    it("should tell the GameCoordinatorProxy that it wants to play") {
      val probe = TestProbe()
      val villager = TestActorRef(new Villager("francis", "francis", probe.ref))
      probe.expectMsg(500 millis, "I want to play")
    }
  }
}