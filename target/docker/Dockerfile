FROM local/openjdk-jre-8-bash
MAINTAINER andy.shi@ibm.com
WORKDIR /opt/docker
ADD opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]
USER daemon
ENTRYPOINT bin/myapp -DakkaActorSystemName="$AKKA_ACTOR_SYSTEM_NAME" -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) -Dakka.io.dns.resolver=async-dns -Dakka.io.dns.async-dns.resolve-srv=true -Dakka.io.dns.async-dns.resolv-conf=on -DhttpHost="$HTTP_HOST" -DhttpPort="$HTTP_PORT" -DclusterMembershipAskTimeout="$CLUSTER_MEMBERSHIP_ASK_TIMEOUT"
CMD []
