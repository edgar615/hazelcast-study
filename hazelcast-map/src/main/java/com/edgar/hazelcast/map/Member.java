package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class Member {
  public static void main(String[] args) throws Exception {
//    To destroy a Hazelcast distributed object, you can use the method destroy.
// This method clears and releases all resources of the object.
// Therefore, you must use it with care since a reload with the same object reference after the object is destroyed creates a new data structure without an error.
// Please see the following example code where one of the queues are destroyed and the other one is accessed.

    HazelcastInstance hz1 = Hazelcast.newHazelcastInstance();
    HazelcastInstance hz2 = Hazelcast.newHazelcastInstance();

    IQueue<String> q1 = hz1.getQueue("q");
    IQueue<String> q2 = hz2.getQueue("q");
    q1.add("foo");

    System.out.println("q1.size: " + q1.size() + " q2.size:" + q2.size());
    q1.destroy();
    System.out.println("q1.size: " + q1.size() + " q2.size:" + q2.size());
  }
}