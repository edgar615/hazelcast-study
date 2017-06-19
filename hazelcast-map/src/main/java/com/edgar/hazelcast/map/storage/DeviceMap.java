package com.edgar.hazelcast.map.storage;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class DeviceMap {
  public static void main(String[] args) {
    MapConfig config = new MapConfig("default").setMapStoreConfig(new MapStoreConfig().setClassName(DeviceStore.class.getName()));
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(new Config().addMapConfig(config));
    IMap<Integer, Device> capitalcities = hzInstance.getMap("devices");
    Device device = capitalcities.get(13);

    System.out.println(device);
  }
}