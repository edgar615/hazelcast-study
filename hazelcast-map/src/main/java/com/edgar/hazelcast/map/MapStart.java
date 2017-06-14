package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

/**
 * Created by edgar on 17-2-25.
 */
public class MapStart {

  public static void main(String[] args) {
    HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
    //Hazelcast offers a get method for most of its distributed objects.
    //To load an object, first create a Hazelcast instance and then use the related get method on this instance.

    //Note that, most of Hazelcast's distributed objects are created lazily, i.e., a distributed object is created once the first operation accesses it.

    //If you want to use an object you loaded in other places, you can safely reload it using its reference without creating a new Hazelcast instance (customers in the above example).

    //To destroy a Hazelcast distributed object, you can use the method destroy. This method clears and releases all resources of the object. Therefore, you must use it with care since a reload with the same object reference after the object is destroyed creates a new data structure without an error. Please see the following example code where one of the queues are destroyed and the other one is accessed.
    Map<Integer, String> customers = hazelcastInstance.getMap("customers");

  }
}
