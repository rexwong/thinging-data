package com.rexwong.thinkingdata.app.config;

import org.apache.kafka.clients.producer.internals.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

/**

 */
public class KafkaPartition extends Partitioner {

    public int partition(String topic, Object key, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        List<PartitionInfo> partitionerList = cluster.partitionsForTopic(topic);
        int numPart = partitionerList.size(); //获得所有的分区
        int hashCode = key.hashCode(); //获得key的 hashcode
        return 1;
    }

    public void close() {

    }

    public void configure(Map<String, ?> map) {

    }
}
