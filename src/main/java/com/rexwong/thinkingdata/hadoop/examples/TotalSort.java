package com.rexwong.thinkingdata.hadoop.examples;

import com.rexwong.thinkingdata.hadoop.group.SecondSortGroup;
import com.rexwong.thinkingdata.hadoop.io.SecondSortComparator;
import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TotalSort {

    private static final Logger log = LoggerFactory.getLogger(TotalSort.class);

    public static class MyMapper
            extends Mapper<Text, Text, SecondSortKey, IntWritable> {

        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            String[] arr = value.toString().split(" ");
            // key 和 value形成组合键，后期二次排序
            context.write(new SecondSortKey(new Text(arr[0]), new IntWritable(Integer.parseInt(arr[1]))),
                    new IntWritable(Integer.parseInt(value.toString())));
        }
    }
    public static class MyReducer
            extends Reducer<Text,IntWritable,Text,NullWritable> {

        public void reduce(SecondSortKey key, Iterable<IntWritable> values,Context context)
                throws IOException, InterruptedException {

            for (IntWritable val : values) {
                // 将内容key value作为复合key输出
                context.write(new Text(key.getFirstKey()), NullWritable.get());
            }
        }
    }
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://node1:9000");
        // 设置源文件中字段之间的分隔符，默认是\t
        // 此分隔符在 KeyValueTextInputFormat 中使用
        conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", " ");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        // partition file 在hdfs上的存储路径
        Path partitionFile = new Path("hdfs://node1:9000/secondPartition");

        // 使用 RandomSampler 采集器对数据进行抽样划分界限
        // 这里 RandomSampler<> 内K V的数据类型在本例中无用，可以随便写也可不写
        // 因为在代码中使用的数据类型是 InputFormat 的数据类型
        InputSampler.RandomSampler<NullWritable, NullWritable> randomSampler
                = new InputSampler.RandomSampler(0.5, 3);
        // RandomSampler的另一个构造方法，当split较多时，可以使用
//                = new InputSampler.RandomSampler<Text, Text>(0.5, 3, 2);
        TotalOrderPartitioner.setPartitionFile(conf, partitionFile);
        Job job = Job.getInstance(conf, "TotalSort");
        // 使用KeyValueTextInputFormat，默认是FileInputFormat
        job.setInputFormatClass(KeyValueTextInputFormat.class);
        job.setJarByClass(TotalSort.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        // 模拟全局排序，设置reduce个数大于1
        job.setNumReduceTasks(2);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        // 全局排序分区
        job.setPartitionerClass(TotalOrderPartitioner.class);

        // 重写排序方法
        job.setSortComparatorClass(SecondSortComparator.class);
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
        // 将数据的分界点写入partition file中
        InputSampler.writePartitionFile(job, randomSampler);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
