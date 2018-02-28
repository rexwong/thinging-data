package com.rexwong.thinkingdata.hadoop.mapreduce;

import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.SplitLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class SecondarypairLineRecordReader extends RecordReader<SecondSortKey, Text> {

    private static final Logger LOG = LoggerFactory.getLogger(SecondarypairLineRecordReader.class);

    public static final String MAX_LINE_LENGTH =
            "mapreduce.input.linerecordreader.line.maxlength";
    private long start;
    private long pos;
    private long end;
    private SplitLineReader in;
    private FSDataInputStream fileIn;
    private Seekable filePosition;
    private SecondSortKey key;
    private Text value;
    private int maxLineLength;
    private boolean isCompressedInput;
    private byte[] recordDelimiterBytes;

    public SecondarypairLineRecordReader(byte[] recordDelimiterBytes) {
        this.recordDelimiterBytes = recordDelimiterBytes;
    }

    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext)
            throws IOException {

        FileSplit split = (FileSplit) inputSplit;
        Configuration job = taskAttemptContext.getConfiguration();
        this.maxLineLength = job.getInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
        start = split.getStart();
        end = start + split.getLength();
        final Path file = split.getPath();
        // open the file and seek to the start of the split
        final FileSystem fs = file.getFileSystem(job);
        fileIn = fs.open(file);
        fileIn.seek(start);
        in = new SplitLineReader(fileIn, job, this.recordDelimiterBytes);
        filePosition = fileIn;
        // If this is not the first split, we always throw away first record
        // because we always (except the last split) read one extra line in
        // next() method.
        if (start != 0) {
            start += in.readLine(new Text(), 0, maxBytesToConsume(start));
        }
        this.pos = start;
    }

    private int maxBytesToConsume(long pos) {
        return isCompressedInput
                ? Integer.MAX_VALUE
                : (int) Math.max(Math.min(Integer.MAX_VALUE, end - pos), maxLineLength);
    }

    // 给key和value赋值
    @Override
    public boolean nextKeyValue() throws IOException {
        if (key == null) {
            key = new SecondSortKey();
        }
        if (value == null) {
            value = new Text();
        }
        int newSize = 0;
        while (getFilePosition() <= end || in.needAdditionalRecordAfterSplit()) {
            if (pos == 0) {
                newSize = skipUtfByteOrderMark();
            } else {
                newSize = in.readLine(value, maxLineLength, maxBytesToConsume(pos));
                pos += newSize;
            }
            if ((newSize == 0) || (newSize < maxLineLength)) {
                break;
            }
            // line too long. try again
            LOG.info("Skipped line of size " + newSize + " at pos " +
                    (pos - newSize));
        }
        if (newSize == 0) {
            key = null;
            value = null;
            return false;
        } else {
            // 打印value的值，可以看出value就是源文件中的数据
            LOG.info("====SecondarypairLineRecordReader.value : " + value.toString());
            key = new SecondSortKey(new Text(value.toString().split(" ")[0]),
                    new IntWritable(Integer.parseInt(value.toString().split(" ")[1])));
//            key.set(new Text(value.toString().split(" ")[0]),
//                    new IntWritable(Integer.parseInt(value.toString().split(" ")[1])));
            return true;
        }
    }

    private int skipUtfByteOrderMark() throws IOException {
        // Strip BOM(Byte Order Mark)
        // Text only support UTF-8, we only need to check UTF-8 BOM
        // (0xEF,0xBB,0xBF) at the start of the text stream.
        int newMaxLineLength = (int) Math.min(3L + (long) maxLineLength,
                Integer.MAX_VALUE);
        int newSize = in.readLine(value, newMaxLineLength, maxBytesToConsume(pos));
        // Even we read 3 extra bytes for the first line,
        // we won't alter existing behavior (no backwards incompat issue).
        // Because the newSize is less than maxLineLength and
        // the number of bytes copied to Text is always no more than newSize.
        // If the return size from readLine is not less than maxLineLength,
        // we will discard the current line and read the next line.
        pos += newSize;
        int textLength = value.getLength();
        byte[] textBytes = value.getBytes();
        if ((textLength >= 3) && (textBytes[0] == (byte) 0xEF) &&
                (textBytes[1] == (byte) 0xBB) && (textBytes[2] == (byte) 0xBF)) {
            // find UTF-8 BOM, strip it.
            LOG.info("Found UTF-8 BOM and skipped it");
            textLength -= 3;
            newSize -= 3;
            if (textLength > 0) {
                // It may work to use the same buffer and not do the copyBytes
                textBytes = value.copyBytes();
                value.set(textBytes, 3, textLength);
            } else {
                value.clear();
            }
        }
        return newSize;
    }

    @Override
    public SecondSortKey getCurrentKey(){
        return this.key;
    }

    @Override
    public Text getCurrentValue(){
        return this.value;
    }

    @Override
    public float getProgress() throws IOException {
        if (start == end) {
            return 0.0f;
        } else {
            return Math.min(1.0f, (getFilePosition() - start) / (float) (end - start));
        }
    }

    private long getFilePosition() throws IOException {
        long retVal;
        if (isCompressedInput && null != filePosition) {
            retVal = filePosition.getPos();
        } else {
            retVal = pos;
        }
        return retVal;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
