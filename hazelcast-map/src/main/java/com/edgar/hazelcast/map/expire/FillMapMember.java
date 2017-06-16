package com.edgar.hazelcast.map.expire;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.concurrent.TimeUnit;

public class FillMapMember {
  public static void main(String[] args) {
    MapConfig config = new MapConfig("default").setBackupCount(0).setAsyncBackupCount(1);
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(new Config().addMapConfig(config));
    IMap<String, String> capitalcities = hzInstance.getMap("capitals");
    capitalcities.put("1", "Tokyo",500, TimeUnit.MICROSECONDS);
    capitalcities.put("2", "Paris");
    capitalcities.put("3", "Washington");
    capitalcities.put("4", "Ankara");
    capitalcities.put("5", "Brussels" , 1000, TimeUnit.MICROSECONDS);
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