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

class TestXMPPStream extends XMPPStream {
  override def connectForReal = {

  }
}

class TestConnection extends Connection {
  startWith(Initial(), new TestXMPPStream)
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
      conn ! Login("francis", "francis")
      assert(conn.stateName == LoggedIn())
      conn.stateData.conn.get.disconnect(new Presence(unavailable))
    }
    it("remembers the username") {
      assert(conn.stateData.username.get == "francis")
      assert(!conn.stateData.isConnected)
    }
  }

  describe("a GomeCoordinatorProxy") {
    it("should return a room") {
      val gc = TestActorRef(new GameCoordinatorProxy(conn))
      gc ! "I want to play"
      expectMsgClass(classOf[ActorRef])
    }
  }
  
  describe("a Connection"){
    it("should pass on messages"){
     
    }
  }

  describe("a Villager") {
    it("should tell the GameCoordinatorProxy that it wants to play") {
      val probe = TestProbe()
      val villager = TestFSMRef(new Villager("francis", "francis", probe.ref))
      assert(villager.stateName == Villager.Initial())
      probe.expectMsg(500 millis, "I want to play")
    }
  }
}