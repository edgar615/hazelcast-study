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