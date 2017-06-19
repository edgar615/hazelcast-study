package com.edgar.hazelcast.map.storage;

import com.hazelcast.core.MapStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edgar on 17-6-19.
 */
public class DeviceStore implements MapStore<Integer, Device> {

  private Connection con;

  public DeviceStore() {
    try {
      con = DriverManager.getConnection("jdbc:mysql://localhost:3306/device", "root", "123456");
//      con.createStatement().executeUpdate(
//          "create table if not exists person (id bigint, name varchar(45))");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void store(Integer deviceId, Device device) {
    try {
      con.createStatement().executeUpdate(
          String.format("insert into device(device_id, mac_address) values(%d,'%s')", deviceId, device.getMacAddress()));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void storeAll(Map<Integer, Device> map) {
    map.forEach((k, v) -> {
      store(k, v);
    });
  }

  @Override
  public void delete(Integer deviceId) {
    try {
      con.createStatement().executeUpdate(
          String.format("delete from device where device_id = %d", deviceId));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll(Collection<Integer> collection) {
    collection.forEach(id -> delete(id));
  }

  @Override
  public Device load(Integer id) {
    try {
      ResultSet resultSet = con.createStatement().executeQuery(
          String.format("select mac_address from device where device_id =%d", id));
      try {
        if (!resultSet.next()) return null;
        String macAddress = resultSet.getString(1);
        Device device = new Device();
        device.setDeviceId(id);
        device.setMacAddress(macAddress);;
        return device;
      } finally {
        resultSet.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Map<Integer, Device> loadAll(Collection<Integer> collection) {
    Map<Integer, Device> result = new HashMap<>();
    for (Integer key : collection) result.put(key, load(key));
    return result;
  }

  @Override
  public Iterable<Integer> loadAllKeys() {
    return null;
  }
}
