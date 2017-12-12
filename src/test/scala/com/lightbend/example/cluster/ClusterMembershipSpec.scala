/*
 * Copyright © 2014-2016 Lightbend, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of Lightbend, Inc.
 */

package com.lightbend.example.cluster

import akka.actor.{ ActorSystem, Props }
import akka.cluster.ClusterEvent.{ MemberJoined, MemberRemoved, MemberUp }
import akka.cluster.{ MemberStatus, TestCluster }
import akka.testkit.TestProbe
import com.lightbend.example.cluster
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpec }

import scala.concurrent.Await
import scala.concurrent.duration._

object ClusterMembershipSpec {
  class TestClusterMembership extends ClusterMembershipAware
}

class ClusterMembershipSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  private val systemName = this.getClass.getSimpleName
  private implicit val actorSystem = ActorSystem(systemName)

  override protected def afterAll(): Unit =
    Await.ready(actorSystem.terminate(), atMost = 10.seconds)

  "cluster membership" should {
    "keep track of member states" in {
      val client = TestProbe()
      val clusterMembership = actorSystem.actorOf(Props(new cluster.ClusterMembershipSpec.TestClusterMembership()))

      val member1 = TestCluster.createMember(systemName, "10.0.0.1", 9004, 10L, Set("server"))

      clusterMembership ! MemberJoined(member1)

      client.send(clusterMembership, ClusterMembership.GetMembershipInfo)
      client.expectMsg(ClusterMembership.MembershipInfo(Set(member1)))

      val member1Up = member1.copyUp(1)
      clusterMembership ! MemberUp(member1Up)

      client.send(clusterMembership, ClusterMembership.GetMembershipInfo)
      client.expectMsg(ClusterMembership.MembershipInfo(Set(member1Up)))

      val member2 = TestCluster.createMember(systemName, "10.0.0.2", 9004, 10L, Set("server"))

      clusterMembership ! MemberJoined(member2)

      client.send(clusterMembership, ClusterMembership.GetMembershipInfo)
      client.expectMsg(ClusterMembership.MembershipInfo(Set(member1Up, member2)))

      val member1Removed = member1Up.copy(MemberStatus.Removed)
      clusterMembership ! MemberRemoved(member1Removed, previousStatus = MemberStatus.Up)

      client.send(clusterMembership, ClusterMembership.GetMembershipInfo)
      client.expectMsg(ClusterMembership.MembershipInfo(Set(member2)))
    }
  }
}
