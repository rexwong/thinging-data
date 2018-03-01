package com.rexwong.thinkingdata.hadoop.partition;

import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * 为二次排序自定义分区策略
 */
public class SecondSortPartition extends Partitioner<SecondSortKey, IntWritable> {
    @Override
    public int getPartition(SecondSortKey text, IntWritable intWritable, int i) {
        // 得到EntityPair中的第一个firstKey
        return Math.abs(text.getFirstKey().hashCode() * 127) % i;
    }
}
