package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

import java.util.Map;

public class Partition {
  public static void main(String[] args) throws Exception {
//Hazelcast uses the name of a distributed object to determine which partition it will be put. Let's load two semaphores as shown below:
    HazelcastInstance hz1 = Hazelcast.newHazelcastInstance();
    HazelcastInstance hz2 = Hazelcast.newHazelcastInstance();

    IMap<String, String> map1 = hz1.getMap("map1");
    IMap<String, String> map2= hz2.getMap("map2");

    System.out.println("map1.partition: " + map1.getPartitionKey() + " map2.partition:" + map2.getPartitionKey());


//    Since these semaphores have different names, they will be placed into different partitions. If you want to put these two into the same partition, you use the @ symbol as shown below:
    IMap<String, String> map3 = hz1.getMap("map3@foo");
    IMap<String, String> map4= hz2.getMap("map4@foo");

    System.out.println("map3.partition: " + map3.getPartitionKey() + " map4.partition:" + map4.getPartitionKey());

    IMap<String, String> map5= hz2.getMap("map5@" + map1.getPartitionKey());

    System.out.println("map5.partition: " + map5.getPartitionKey());
  }
}