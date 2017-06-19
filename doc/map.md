hazelcast的map继承自java.util.concurrent.ConcurrentMap

hazelcast会将map分区使它们几乎均匀地分布到所有对hazelcast节点上，每个节点的数据数量大约为`(1/n * total-data) + backups`,n是集群中成员的数量.

例如，集群中有一个节点，集群中存储1000个对象，当启动第二个节点后，每个节点将存储500个对象，并备份另一个节点的500个对象

示例：

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

输出

    map1.partition: capitals
    12

再启动一个应用

    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    IMap<String, String> capitalcities = hzInstance.getMap("capitals");
    System.out.println("map2.partition: " + capitalcities.getPartitionKey());
    System.out.println(capitalcities.getLocalMapStats().getOwnedEntryCount());
    System.out.println(capitalcities.getLocalMapStats().getBackupEntryCount());

输出

    map2.partition: capitals
    7
    5

创建同步备份

    <hazelcast>
      <map name="default">
        <backup-count>1</backup-count>
      </map>
    </hazelcast>

backup-count设置为１，一个map的entry会在集群的另一个节点备份.backup-count设置为2，一个map的entry会在集群的另外两个节点备份.
最大值是６

节点１：

    MapConfig config = new MapConfig("default").setBackupCount(0);
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(new Config().addMapConfig(config));
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

节点２:

    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
    IMap<String, String> capitalcities = hzInstance.getMap("capitals");
    System.out.println("map2.partition: " + capitalcities.getPartitionKey());
    System.out.println(capitalcities.getLocalMapStats().getOwnedEntryCount());
    System.out.println(capitalcities.getLocalMapStats().getBackupEntryCount());

输出：

map2.partition: capitals
7
0

创建异步备份

    <hazelcast>
       <map name="default">
           <backup-count>0</backup-count>
           <async-backup-count>1</async-backup-count>
       </map>
    </hazelcast>

启用备份读取

默认情况下，Hazelcast使用同步备份.如果备份计数设置为1以上，则每个节点将携带其他节点的所有数据和备份副本．
因此，对于map.get(key)的调用，可以调用该节点的备份副本。默认情况下，map.get(key)将始终从实际所有者处读取相应值以获得一致性。

这样可能出现数据不一致的情况：从备份节点上获取的一个值，可能在主节点上已经过期

驱逐数据

除非手动删除数据，或者使用驱逐策略，map中的数据会永远存在.
Hazelcast目前支持两种策略: LRU (Least Recently Used) 和 LFU (Least Frequently Used).

Hazelcast基于分区实现驱逐策略，可以在` max-size`上使用`PER_NODE`属性，指定每个分区对最大数量.
Hazelcast内部使用下面的公式计算每个公式的最大数量：

    partition maximum size = max-size * member-count / partition-count

当试图向map中加入一个元素，Hazelcast会基于分区的最大数量启动驱逐进程.当分区的元素数量超过了该分区的最大数量时，开始在这个分区上进行驱逐

示例：

    Partition count: 200
    Entry count for each partition: 100
    max-size (PER_NODE): 20000


配置

    <hazelcast>
        <map name="default">
            <time-to-live-seconds>0</time-to-live-seconds>
            <max-idle-seconds>0</max-idle-seconds>
            <eviction-policy>LRU</eviction-policy>
            <max-size policy="PER_NODE">5000</max-size>
        </map>
    </hazelcast>


- time-to-live：每个元素在map中存活的最大时间，单位为秒。如果不是0，则超过此时间而未更新的数据将自动驱逐。有效范围是0到Integer.MAX VALUE之间。默认值是0，表示无限。如果不是0，则不管map的驱逐策略，数据都会被逐出。
- max-idle-seconds. 每个元素在map中的最大空闲时间. 空闲时间超过这个时间的元素将自动驱逐．如果一个元素没有get, put, EntryProcessor.process或者containsKey方法被调用，则说明这个元素是空闲的.有效范围是0到Integer.MAX VALUE之间。默认值是0，表示无限。
- eviction-policy. 驱逐策略，可选值如下：
    - NONE: 默认策略. 驱逐策略会被忽略，没有值被驱逐
    - LRU: Least Recently Used.
    - LFU: Least Frequently Used.
- max-size. map的最大值. 有效范围是0到Integer.MAX VALUE之间. 默认值０. 0表示最大值,如果希望max-size其作用，需要将eviction-policy的属性设置为非`NONE`,max-size的属性如下：
    - PER_NODE 每个节点map元素的最大数量. 这是默认策略.如果使用这个策略,注意不能将max-size设置为小于分区数量(默认271) `<max-size policy="PER_NODE">5000</max-size>`
    - PER_PARTITION 每个分区map元素的最大数量.节点存储的数据大小依赖于分区的数量．这个属性不应该经常使用．例如，避免在小集群里使用这个属性．如果集群很小，相比大集群它将托管更多的分区．对于小集群，驱逐元素将降低性能（数据的数量很大）　`<max-size policy="PER_PARTITION">27100</max-size>`
    - USED_HEAP_SIZE 每个Hazelcast实例中每个map的最大堆大小（单位megabytes）．注意：当内存格式设置为对象时，此策略不起作用．因为当数据作为对象时不能确定内存占用量 `<max-size policy="USED_HEAP_SIZE">4096</max-size>`
    - USED_HEAP_PERCENTAGE 每个Hazelcast实例的中每个map占最大堆大小的百分比.例如,JVM配置为1000MB，这个值设置为10，那么当使用堆大小超过100MB时，map的元素将被驱逐.注意：当内存格式设置为对象时，此策略不起作用．因为当数据作为对象时不能确定内存占用量 `<max-size policy="USED_HEAP_PERCENTAGE">10</max-size>`
    - FREE_HEAP_SIZE. 每个JVM最小的空闲堆大小.`<max-size policy="FREE_HEAP_SIZE">512</max-size>`
    - FREE_HEAP_PERCENTAGE.最小空闲堆大小占JVM堆的百分比.例如JVM配置为1000MB，这个值设置为10，那么当使用空闲堆大小小于100MB时，map的元素将被驱逐 `<max-size policy="FREE_HEAP_PERCENTAGE">10</max-size>`
    - USED_NATIVE_MEMORY_SIZE. (Hazelcast IMDG Enterprise HD) Maximum used native memory size in megabytes per map for each Hazelcast instance.`<max-size policy="USED_NATIVE_MEMORY_SIZE">1024</max-size>`
    - USED_NATIVE_MEMORY_PERCENTAGE. (Hazelcast IMDG Enterprise HD) Maximum used native memory size percentage per map for each Hazelcast instance.`<max-size policy="USED_NATIVE_MEMORY_PERCENTAGE">65</max-size>`
    - FREE_NATIVE_MEMORY_SIZE. (Hazelcast IMDG Enterprise HD) Minimum free native memory size in megabytes for each Hazelcast instance.`<max-size policy="FREE_NATIVE_MEMORY_SIZE">256</max-size>`
    - FREE_NATIVE_MEMORY_PERCENTAGE. (Hazelcast IMDG Enterprise HD) Minimum free native memory size percentage for each Hazelcast instance.`<max-size policy="FREE_NATIVE_MEMORY_PERCENTAGE">5</max-size>`

示例1：

    <map name="documents">
      <max-size policy="PER_NODE">10000</max-size>
      <eviction-policy>LRU</eviction-policy>
      <max-idle-seconds>60</max-idle-seconds>
    </map>

当节点中map的大小到达10000时，documents map开始驱逐map中的元素.如果元素超过60秒未被使用，它一样会被驱逐

示例2:

    <map name="nativeMap*">
        <in-memory-format>NATIVE</in-memory-format>
        <eviction-policy>LFU</eviction-policy>
        <max-size policy="USED_NATIVE_MEMORY_PERCENTAGE">99</max-size>
    </map>


驱逐特定的元素

    capitalcities.put("1", "Tokyo",500, TimeUnit.MICROSECONDS);

驱逐所有元素

    capitalcities.evictAll();

自定义驱逐策略

    public class MapCustomEvictionPolicy {

        public static void main(String[] args) {
            Config config = new Config();
            config.getMapConfig("test")
                    .setMapEvictionPolicy(new OddEvictor())
                    .getMaxSizeConfig()
                    .setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.PER_NODE).setSize(10000);

            HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
            IMap<Integer, Integer> map = instance.getMap("test");

            final Queue<Integer> oddKeys = new ConcurrentLinkedQueue<Integer>();
            final Queue<Integer> evenKeys = new ConcurrentLinkedQueue<Integer>();

            map.addEntryListener(new EntryEvictedListener<Integer, Integer>() {
                @Override
                public void entryEvicted(EntryEvent<Integer, Integer> event) {
                    Integer key = event.getKey();
                    if (key % 2 == 0) {
                        evenKeys.add(key);
                    } else {
                        oddKeys.add(key);
                    }
                }
            }, false);

            // Wait some more time to receive evicted events.
          try {
            TimeUnit.SECONDS.sleep(5);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
    //        parkNanos(TimeUnit.SECONDS.toNanos(5));

            for (int i = 0; i < 15000; i++) {
                map.put(i, i);
            }

            String msg = "IMap uses sampling based eviction. After eviction is completed, we are expecting " +
                    "number of evicted-odd-keys should be greater than number of evicted-even-keys" +
                    "\nNumber of evicted-odd-keys = %d, number of evicted-even-keys = %d";
            System.out.println(String.format(msg, oddKeys.size(), evenKeys.size()));

            instance.shutdown();
        }

        /**
         * Odd evictor tries to evict odd keys first.
         */
        private static class OddEvictor extends MapEvictionPolicy {

            @Override
            public int compare(EntryView o1, EntryView o2) {
                Integer key = (Integer) o1.getKey();
                if (key % 2 != 0) {
                    return -1;
                }

                return 1;
            }
        }
    }

或者

    <map name="test">
       ...
       <map-eviction-policy-class-name>com.package.OddEvictor</map-eviction-policy-class-name>
       ....
    </map>

Setting In-Memory Format

略

加载和存储持久化数据

Hazelcast允许你从持久化数据（例如关系数据库）中加载或者保存分布式map entries.可以使用Hazelcast的MapStore和MapLoader接口

如果从内存中没有取到对应的数据（IMap.get()），可以通过MapLoader中的load方法或者loadall方法从数据加载数据．
加载的数据会被放置到map中，并将一直保留再那里，直到它被移除或被驱逐为止

MapStore接口可以将数据保存到用户定义的存储层

当一个mapstore实施提供，入口也投入到一个用户定义的数据存储。

*注意：*　数据持久层需要被所有的节点访问到
