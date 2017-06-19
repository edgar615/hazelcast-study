package com.edgar.hazelcast.map.storage;

import java.io.Serializable;

/**
 * Created by edgar on 17-6-19.
 */
public class Device implements Serializable {
  private int deviceId;

  private String macAddress;

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  @Override
  public String toString() {
    return "Device{" +
        "deviceId=" + deviceId +
        ", macAddress='" + macAddress + '\'' +
        '}';
  }
}
