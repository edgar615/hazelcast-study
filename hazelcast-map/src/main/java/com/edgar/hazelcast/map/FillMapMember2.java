package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class FillMapMember2 {
  public static void main(String[] args) {
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    IMap<String, String> capitalcities = hzInstance.getMap("capitals");
    System.out.println("map2.partition: " + capitalcities.getPartitionKey());
    System.out.println(capitalcities.getLocalMapStats().getOwnedEntryCount());
    System.out.println(capitalcities.getLocalMapStats().getBackupEntryCount());
  }
}