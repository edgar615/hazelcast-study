package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.Map;

public class FillMapMember {
  public static void main(String[] args) {
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
//    Map<String, String> capitalcities = hzInstance.getMap("capitals");
    IMap<String, String> capitalcities = hzInstance.getMap("capitals");
    capitalcities.put("1", "Tokyo");
    capitalcities.put("2", "Paris");
    capitalcities.put("3", "Washington");
    capitalcities.put("4", "Ankara");
    capitalcities.put("5", "Brussels");
    capitalcities.put("6", "Amsterdam");
    capitalcities.put("7", "New Delhi");
    capitalcities.put("8", "London");
    capitalcities.put("9", "Berlin");
    capitalcities.put("10", "Oslo");
    capitalcities.put("11", "Moscow");
    capitalcities.put("120", "Stockholm");

    System.out.println("map1.partition: " + capitalcities.getPartitionKey());
    System.out.println(capitalcities.getLocalMapStats().getOwnedEntryCount());
  }
}