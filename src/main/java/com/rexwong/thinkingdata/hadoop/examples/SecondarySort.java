package com.rexwong.thinkingdata.hadoop.examples;


import com.rexwong.thinkingdata.hadoop.io.KeyComparator;
import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import com.rexwong.thinkingdata.hadoop.group.SecondSortGroup;
import com.rexwong.thinkingdata.hadoop.partition.SecondSortPartition;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SecondarySort {

    private static final Logger log = LoggerFactory.getLogger(SecondarySort.class);

    public static class MyMapper
            extends Mapper<Object, Text, SecondSortKey, IntWritable> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] arr = value.toString().split(" ");
            // 将内容key value作为复合key输出
            context.write(new SecondSortKey(new Text(arr[0]), new IntWritable(Integer.parseInt(arr[1]))),
                    new IntWritable(Integer.parseInt(arr[1])));
        }
    }
    public static class MyReducer
            extends Reducer<SecondSortKey,IntWritable,Text,IntWritable> {

        public void reduce(SecondSortKey key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            for (IntWritable val : values) {
                // 分组之后
                context.write(new Text(key.getFirstKey()), val);
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", "hdfs://node1:9000");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(SecondarySort.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        job.setNumReduceTasks(2);
        job.setMapOutputKeyClass(SecondSortKey.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 重定义partition
        job.setPartitionerClass(SecondSortPartition.class);
        // 重写排序方法
        job.setSortComparatorClass(KeyComparator.class);
        // 自定义分组策略
        job.setGroupingComparatorClass(SecondSortGroup.class);

        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(new Path(otherArgs[otherArgs.length - 1]))){
            fs.delete(new Path(otherArgs[otherArgs.length - 1]), true);
        }
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
