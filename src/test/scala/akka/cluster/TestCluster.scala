/*
 * Copyright © 2014-2016 Lightbend, Inc. All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of Lightbend, Inc.
 */

package akka.cluster

import akka.actor.Address

object TestCluster {
  def createMember(systemName: String, host: String, port: Int, uniqueAddressId: Long, roles: Set[String]): Member = {
    val address = Address("akka.tcp", systemName, host, port)
    Member(UniqueAddress(address, uniqueAddressId), roles)
  }
}
