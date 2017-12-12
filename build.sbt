import sbt._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences.AlignSingleLineCaseStatements
import com.typesafe.sbt.packager.docker._
import de.heikoseeberger.sbtheader.FileType

lazy val akkaVersion = "2.5.4"

name := "myapp"
organization := "IBM"
organizationName := "IBM"
startYear := Some(2017)
maintainer := "andy.shi@ibm.com"

scalaVersion := "2.12.3"

enablePlugins(AutomateHeaderPlugin, JavaServerAppPackaging)

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor"          % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"        % akkaVersion,
  "com.typesafe.akka" %% "akka-http"           % "10.0.10",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.19.0-M2",
  "com.typesafe.play" %% "play-json"           % "2.6.5",
  "org.scalatest"     %% "scalatest"           % "3.0.1"     % "test",
  "com.typesafe.akka" %% "akka-testkit"        % akkaVersion % "test"
)

dockerEntrypoint ++= Seq(
  """-DakkaActorSystemName="$AKKA_ACTOR_SYSTEM_NAME"""",
  """-Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")"""",
  """-Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT"""",
  """$(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done)""",
  "-Dakka.io.dns.resolver=async-dns",
  "-Dakka.io.dns.async-dns.resolve-srv=true",
  "-Dakka.io.dns.async-dns.resolv-conf=on",
  """-DhttpHost="$HTTP_HOST"""",
  """-DhttpPort="$HTTP_PORT"""",
  """-DclusterMembershipAskTimeout="$CLUSTER_MEMBERSHIP_ASK_TIMEOUT""""
)
dockerCommands :=
  dockerCommands.value.flatMap {
    case ExecCmd("ENTRYPOINT", args @ _*) => Seq(Cmd("ENTRYPOINT", args.mkString(" ")))
    case v => Seq(v)
  }
dockerRepository := Some("szihai")
dockerUpdateLatest := true
dockerBaseImage := "local/openjdk-jre-8-bash"

ScalariformKeys.preferences :=
  ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)

licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
headerMappings := headerMappings.value + (FileType("conf") -> HeaderCommentStyle.HashLineComment)
