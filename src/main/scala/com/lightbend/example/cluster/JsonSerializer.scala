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

import akka.cluster.{ Member, MemberStatus }
import play.api.libs.json.{ Format, JsResult, JsString, JsValue, Json }

object JsonSerializer {
  implicit val memberStatusJsonSerializer: Format[MemberStatus] = new Format[MemberStatus] {
    override def reads(json: JsValue): JsResult[MemberStatus] =
      throw new UnsupportedOperationException("Reading MemberStatus from json is not supported")

    override def writes(o: MemberStatus): JsValue =
      JsString(
        o match {
          case MemberStatus.Joining  => "Joining"
          case MemberStatus.WeaklyUp => "WeaklyUp"
          case MemberStatus.Up       => "Up"
          case MemberStatus.Down     => "Down"
          case MemberStatus.Exiting  => "Exiting"
          case MemberStatus.Leaving  => "Leaving"
          case MemberStatus.Removed  => "Removed"
        })
  }

  implicit val memberJsonSerializer: Format[Member] = new Format[Member] {
    override def reads(json: JsValue): JsResult[Member] =
      throw new UnsupportedOperationException("Reading Member from json is not supported")

    override def writes(o: Member): JsValue =
      Json.obj(
        "address" -> o.uniqueAddress.address.toString,
        "status" -> o.status,
        "roles" -> o.roles)
  }

  implicit val membershipInfoJsonSerializer: Format[ClusterMembership.MembershipInfo] = Json.format
}
