/**
 *
 = Zookeeper Cluster Manager

 This is a cluster manager implementation for Vert.x that uses http://zookeeper.apache.org/[Zookeeper].

 It implements interfaces of vert.x cluster totally. So you can using it to instead of vertx-hazelcast if you want.
 This implementation is packaged inside:

 [source,xml,subs="+attributes"]
 ----
 <dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-zookeeper</artifactId>
 <version>3.3.1</version>
 </dependency>
 ----

 In Vert.x a cluster manager is used for various functions including:

 * Discovery and group membership of Vert.x nodes in a cluster
 * Maintaining cluster wide topic subscriber lists (so we know which nodes are interested in which event bus addresses)
 * Distributed Map support
 * Distributed Locks
 * Distributed Counters

 Cluster managers *do not* handle the event bus inter-node transport, this is done directly by Vert.x with TCP connections.

 == How to work
 We are using [Apache Curator](http://curator.apache.org/) framework rather than zookeeper client directly, so
 we have a dependency for libraries used in Curator such as `guava`, `slf4j` and of course `zookeeper`.

 Since ZK using tree dictionary to store data, we can take root path as namespace default root path is `io.vertx` which in default-zookeeper.properties.
 and there are another 5 sub path to record other information for functions in vert.x cluster manager, all you can change the path is `root path`.

 you can find all the vert.x node information in path of `/io.vertx/cluster/nodes/`,
 `/io.vertx/asyncMap/$name/` record all the `AsyncMap` you created with `io.vertx.core.shareddata.AsyncMap` interface.
 `/io.vertx/asyncMultiMap/$name/` record all the `AsyncMultiMap` you created with `io.vertx.core.spi.cluster.AsyncMultiMap` interface.
 `/io.vertx/locks/` record distributed Locks information.
 `/io.vertx/counters/` record distributed Count information.

 == Using this cluster manager

 If you are using Vert.x from the command line, the jar corresponding to this cluster manager (it will be named `vertx-zookeeper-${version}`.jar`
 should be in the `lib` directory of the Vert.x installation.

 If you want clustering with this cluster manager in your Vert.x Maven or Gradle project then just add a dependency to
 the artifact: `io.vertx:vertx-zookeeper:${version}` in your project.

 If the jar is on your classpath as above then Vert.x will automatically detect this and use it as the cluster manager.
 Please make sure you don't have any other cluster managers on your classpath or Vert.x might
 choose the wrong one.

 You can also specify the cluster manager programmatically if you are embedding Vert.x by specifying it on the options
 when you are creating your Vert.x instance, for example:

 [source,java]
 ----
 ClusterManager mgr = new ZookeeperClusterManager();

 VertxOptions options = new VertxOptions().setClusterManager(mgr);

 Vertx.clusteredVertx(options, res -> {
 if (res.succeeded()) {
 Vertx vertx = res.result();
 } else {
 // failed!
 }
 });
 ----

 == Configuring this cluster manager

 Usually the cluster manager is configured by a file
 https://github.com/vert-x3/vertx-zookeeper/blob/master/src/main/resources/default-zookeeper.properties[`default-zookeeper.properties`]
 which is packaged inside the jar.

 If you want to override this configuration you can provide a file called `zookeeper.properties` on your classpath and this
 will be used instead. If you want to embed the `zookeeper.properties` file in a fat jar, it must be located at the root of the
 fat jar. If it's an external file, the **directory** containing the file must be added to the classpath. For
 example, if you are using the _launcher_ class from Vert.x, the classpath enhancement can be done as follows:

 [source]
 ----
 # If the cluster.xml is in the current directory:
 java -jar ... -cp . -cluster
 vertx run MyVerticle -cp . -cluster

 # If the zookeeper.properties is in the conf directory
 java -jar ... -cp conf -cluster
 ----

 Another way to override the configuration is by providing the system property `vertx.zookeeper.conf` with a
 location:

 [source]
 ----
 # Use a cluster configuration located in an external file
 java -Dvertx.zookeeper.config=./config/my-zookeeper-conf.properties -jar ... -cluster

 # Or use a custom configuration from the classpath
 java -Dvertx.zookeeper.config=classpath:my/package/config/my-cluster-config.xml -jar ... -cluster
 ----

 The `vertx.zookeeper.config` system property, when present, overrides any `zookeeper.properties` on the classpath, but if
 loading
 from this system property fails, then loading falls back to either `zookeeper.properties` or the Zookeeper default configuration.

 The properties file is described in detail in `default-zookeeper.properties`'s comment.

 You can also specify configuration programmatically if embedding:

 [source,java]
 ----
 Properties zkConfig = new Properties();
 zkConfig.setProperty("hosts.zookeeper", "127.0.0.1");
 zkConfig.setProperty("path.root", "io.vertx");
 zkConfig.setProperty("retry.initialSleepTime", "1000");
 zkConfig.setProperty("retry.intervalTimes", "3");

 ClusterManager mgr = new ZookeeperClusterManager(zkConfig);
 VertxOptions options = new VertxOptions().setClusterManager(mgr);

 Vertx.clusteredVertx(options, res -> {
 if (res.succeeded()) {
 Vertx vertx = res.result();
 } else {
 // failed!
 }
 });
 ----

 === Enabling logging

 When trouble-shooting clustering issues with Zookeeper it's often useful to get some logging output from Zookeeper
 to see if it's forming a cluster properly. You can do this (when using the default JUL logging) by adding a file
 called `vertx-default-jul-logging.properties` on your classpath. This is a standard java.util.logging (JUL)
 configuration file. Inside it set:

 ----
 org.apache.zookeeper.level=INFO
 ----

 and also

 ----
 java.util.logging.ConsoleHandler.level=INFO
 java.util.logging.FileHandler.level=INFO
 ----

 == About Zookeeper version
 We use Curator 2.x.x, as Zookeeper latest stable is 3.4.8 so we do not support any features of 3.5.x
 *
 */


@Document(fileName = "index.adoc")
package io.vertx.spi.cluster.zookeeper;

import io.vertx.docgen.Document;
