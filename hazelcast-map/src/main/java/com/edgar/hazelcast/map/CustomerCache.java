package com.edgar.hazelcast.map;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by edgar on 17-6-14.
 */
public class CustomerCache {
  private final HazelcastInstance hazelcastInstance;

  public CustomerCache() {
    this.hazelcastInstance = Hazelcast.newHazelcastInstance();
  }

  public static void main(String[] args) {
    CustomerCache cache = new CustomerCache();
    Customer customer = cache.getCustomer("a");
  }

  Customer getCustomer(String id) {
    ConcurrentMap<String, Customer> customers = hazelcastInstance.getMap("customers");
    Customer customer = customers.get(id);
    if (customer == null) {
      customer = new Customer(id);
      customer = customers.putIfAbsent(id, customer);
    }
    return customer;
  }

  public boolean updateCustomer(Customer customer) {
    ConcurrentMap<String, Customer> customers = hazelcastInstance.getMap("customers");
    return (customers.replace(customer.getId(), customer) != null);
  }

  public boolean removeCustomer(Customer customer) {
    ConcurrentMap<String, Customer> customers = hazelcastInstance.getMap("customers");
    return customers.remove(customer.getId(), customer);
  }
}
