/*
 * Copyright 2017 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.example.cluster

import akka.actor.{ Actor, Props }
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.{ Cluster, ClusterEvent, Member }

object ClusterMembership {
  val Name = "cluster-membership"

  def props: Props = Props(new ClusterMembership)

  sealed trait Message

  /**
   * Sent as a request to obtain membership info
   */
  case object GetMembershipInfo extends Message

  /**
   * Sent as a reply to [[GetMembershipInfo]]; contains the list of [[members]] of the cluster.
   */
  case class MembershipInfo(members: Set[Member]) extends Message
}

abstract class ClusterMembershipAware extends Actor {

  override def receive = handleMembershipEvents(Set.empty)

  protected def handleMembershipEvents(members: Set[Member]): Receive = {
    case event: ClusterEvent.MemberRemoved =>
      context.become(handleMembershipEvents(members.filterNot(_ == event.member)))

    case event: ClusterEvent.MemberEvent =>
      context.become(handleMembershipEvents(members.filterNot(_ == event.member) + event.member))

    case ClusterMembership.GetMembershipInfo =>
      sender() ! ClusterMembership.MembershipInfo(members)
  }
}

/**
 * Subscribes to the membership events, stores the updated list of the members in the Akka cluster.
 */
class ClusterMembership extends ClusterMembershipAware {
  private val cluster = Cluster(context.system)

  override def preStart(): Unit =
    cluster.subscribe(self, initialStateMode = ClusterEvent.InitialStateAsEvents, classOf[MemberEvent])

  override def postStop(): Unit =
    cluster.unsubscribe(self)
}
