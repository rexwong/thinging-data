package com.rexwong.thinkingdata.hadoop.examples;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.List;

public class OrderDemo {

    private static final String keyValueDelimiter = "\t";

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) {
            String[] lineArray = value.toString().split(",", -1);
            try {
                context.write(new Text(lineArray[0]),
                        new Text(lineArray[1] + "\t" + lineArray[2] + "\t" + lineArray[3] + "\t" + lineArray[4]));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) {

            List<List<String>> data = Lists.newArrayList();
            for (Text value : values) {
                String[] lineArray = value.toString().split(keyValueDelimiter, -1);
                int index = 0;
                List<String> rowdata = Lists.newArrayList();
                for (String val : lineArray) {
                    if ("NULL".equals(val)) {
                        val = "NULL-" + index;
                    }
                    rowdata.add(val);
                    index++;
                }
                data.add(rowdata);

            }
            try {
                int cursor = 0;
                for (List<String> row : data) {
                    String outValue = "";
                    for (String cell : row) {
                        if (cell.contains("NULL-")) {
                            int jump = cursor + 1;
                            while (jump != data.size()) {
                                cell = data.get(jump).get(Integer.parseInt(cell.replaceAll("NULL-", "")));
                                if (!cell.contains("NULL-")) {
                                    break;
                                }
                                jump++;
                            }
                        }
                        outValue = outValue + cell + "\t";
                    }
                    context.write(key, new Text(outValue));
                    cursor++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class OrderMapper
            extends Mapper<Object, Text, Text, Text> {

        public void map(Object key, Text value, Context context) {
            String[] lineArray = value.toString().split(keyValueDelimiter, -1);
            try {
                context.write(new Text(lineArray[4]), value);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class OrderReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) {
            try {
                for (Text value : values) {
                    String[] lineArray = value.toString().split(keyValueDelimiter, -1);
                    context.write(new Text(lineArray[0]), new Text(lineArray[1] + "\t" + lineArray[2] + "\t" + lineArray[3] + "\t" + lineArray[4]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "order test");
        job.setJarByClass(OrderDemo.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);

        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        if (!job.waitForCompletion(true)) {
            System.exit(1);
        }
        Job job2 = Job.getInstance(conf, "order test2");
        job2.setJarByClass(OrderDemo.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        job2.setMapperClass(OrderMapper.class);
        job2.setReducerClass(OrderReducer.class);

        job2.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));

        System.exit(job2.waitForCompletion(true) ? 0 : 1);
    }
}
