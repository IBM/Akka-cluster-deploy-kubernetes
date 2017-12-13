
[![Build Status](https://travis-ci.org/IBM/watson-banking-chatbot.svg?branch=master)](https://travis-ci.org/IBM/watson-banking-chatbot)
![IBM Cloud Deployments](https://metrics-tracker.mybluemix.net/stats/527357940ca5e1027fbf945add3b15c4/badge.svg)
<!--Add a new Title and fill in the blanks -->
# [Deploy Akka Cluster on Kubernetes]
In this Code Pattern, we will deploy an Akka Cluster on Kubernetes. Along the way, we'll try to explain:
* What is Akka?   
* What are the difficulties in deploying Akka cluster on Kubernetes?   
* The solution to the problem.    

## Flow
<!--Remember to dump an image in this path-->
![](doc/source/images/architecture.png)

<!--Update this section-->
## Included components
Select components from [here](../sections/components.md), copy and paste the raw text for ease
* [Akka](link): description
* [Kubernetes](link): description
* [Docker](link): description
* [sbt](http://www.scala-sbt.org/): The interactive build tool for Scala

<!--Update this section-->
## Featured technologies
Select components from [here](../sections/technologies.md), copy and paste the raw text for ease
* [Scala](link): description

<!--Update this section when the video is created-->
# Watch the Video
[![](http://img.youtube.com/vi/Jxi7U7VOMYg/0.jpg)](https://www.youtube.com/watch?v=Jxi7U7VOMYg)

# Steps
Use the ``Deploy to IBM Cloud`` button **OR** create the services and run locally.

## Deploy to IBM Cloud 
<!--Update the repo and tracking id-->
[![Deploy to IBM Cloud](https://metrics-tracker.mybluemix.net/stats/527357940ca5e1027fbf945add3b15c4/button.svg)](https://bluemix.net/deploy?repository=https://github.com/IBM/watson-banking-chatbot.git)

1. Press the above ``Deploy to IBM Cloud`` button and then click on ``Deploy``.

<!--optional step-->
2. In Toolchains, click on Delivery Pipeline to watch while the app is deployed. Once deployed, the app can be viewed by clicking 'View app'.
![](doc/source/images/toolchain-pipeline.png)

<!--update with service names from manifest.yml-->
3. To see the app and services created and configured for this Code Pattern, use the IBM Cloud dashboard. The app is named `TODO` with a unique suffix. The following services are created and easily identified by the `TODO-` prefix:

## Try it out
> NOTE: These steps are only needed when running locally instead of using the ``Deploy to IBM Cloud`` button.

<!-- there are MANY updates necessary here, just screenshots where appropriate -->

1. [Clone the repo](#1-clone-the-repo)
2. [Prerequisite](#2-prerequisite)
3. [Build the Docker base image](#3-build-the-docker-base-image)
4. [Build the sample app](#4-build-the-sample-app)
5. [Deploy the cluster on Kubernetes](#5-deploy-the-cluster-on-kubernetes)
6. [Confirm the sample app is working](#6-confirm-the-sample-app-is-working)

### 1. Clone the repo

Clone the `Akka-cluster-deploy-kubernetes` locally. In a terminal, run:

```
git clone https://github.com/IBM/Akka-cluster-deploy-kubernetes
cd Akka-cluster-deploy-kubernetes
```

### 2. Prerequisite

* A working Kubernetes installation, i.e. an actual Kubernetes cluster or Minikube.
* Ensure the Docker environment variable is configured to point to the registry used by the Kubernetes cluster.
* Have sbt installed.   

### 3. Build the Docker base image

Build the Docker base image required by the example app:  

```bash
$ cat <<EOF | docker build -t local/openjdk-jre-8-bash:latest -
FROM openjdk:8-jre-alpine
RUN apk --no-cache add --update bash coreutils curl
EOF
```

The example app makes use of `openjdk:8-jre-alpine` as the base image with `bash`, networking utility (i.e. `ping`, `telnet`), and `curl`.


### 4. Build the sample app

Ensure you are at the root directory of the example app. Build the example app by running the following command:

```bash
$ sbt docker:publishLocal
```

### 5. Deploy the cluster on Kubernetes

Deploy the example app by running the following command:

```bash
$ kubectl create -f deploy/kubernetes/resources/myapp
```

### 6. Confirm the sample app is working

Check the logs of the pods created by the example app (i.e. `myapp-0`, `myapp-1`, etc.). The `-f` switch follows the logs emitted by the pod.

```bash
$ kubectl logs -f myapp-0
```

Once the app is started within the pod, a log entry similar to the following should be displayed:

```
[INFO] [10/03/2017 03:44:19.758] [myapp-akka.actor.default-dispatcher-17] [akka.cluster.Cluster(akka://myapp)] Cluster Node [akka.tcp://myapp@myapp-0.myapp.default.svc.cluster.local:2551] - Leader is moving node [akka.tcp://myapp@myapp-0.myapp.default.svc.cluster.local:2551] to [Up]
```

The example app exposes the `/members` endpoint which displays the list of members visible for a particular pod. For example, the following displays the list of members visible from `myapp-0` pod:

```
$ kubectl exec -ti myapp-0 -- curl -v myapp-0:9000/members
  *   Trying 172.17.0.5...
  * TCP_NODELAY set
  * Connected to myapp-0 (172.17.0.5) port 9000 (#0)
  > GET /members HTTP/1.1
  > Host: myapp-0:9000
  > User-Agent: curl/7.55.0
  > Accept: */*
  >
  < HTTP/1.1 200 OK
  < Server: akka-http/10.0.10
  < Date: Tue, 03 Oct 2017 04:06:32 GMT
  < Content-Type: application/json
  < Content-Length: 147
  <
  {
    "members" : [ {
      "address" : "akka.tcp://myapp@myapp-0.myapp.default.svc.cluster.local:2551",
      "status" : "Up",
      "roles" : [ ]
    } ]
  * Connection #0 to host myapp-0 left intact
```


# Links

<!-- pick the relevant ones from below -->
# Learn more

* **Kubernetes on IBM Cloud**: Deliver your apps with the combined the power of [Kubernetes and Docker on IBM Cloud](https://www.ibm.com/cloud-computing/bluemix/containers)

<!--keep this-->

# License
[Apache 2.0](LICENSE)
