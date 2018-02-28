package com.rexwong.thinkingdata.hadoop.format;

import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import com.rexwong.thinkingdata.hadoop.mapreduce.SecondarypairLineRecordReader;
import org.apache.commons.compress.utils.Charsets;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class TotalSortFileInputFormat extends FileInputFormat<SecondSortKey, Text> {
    @Override
    public RecordReader<SecondSortKey, Text> createRecordReader
            (InputSplit inputSplit, TaskAttemptContext taskAttemptContext){

        String delimiter = taskAttemptContext.getConfiguration()
                .get("textinputformat.record.delimiter");

        byte[] recordDelimiterBytes = null;
        if (null != delimiter)
            recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
        // 模仿 TextInputFormat 的实现方式 并改写 LineRecordReader
        // 自定义了行读取方法
        return new SecondarypairLineRecordReader(recordDelimiterBytes);
    }
}
