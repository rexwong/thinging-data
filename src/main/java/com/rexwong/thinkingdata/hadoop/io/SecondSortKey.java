package com.rexwong.thinkingdata.hadoop.io;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SecondSortKey implements WritableComparable<SecondSortKey> {

    private Text firstKey;
    private IntWritable secondKey;

    public Text getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(Text firstKey) {
        this.firstKey = firstKey;
    }

    public IntWritable getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(IntWritable secondKey) {
        this.secondKey = secondKey;
    }

    public SecondSortKey(Text firstKey, IntWritable secondKey) {
        this.firstKey = firstKey;
        this.secondKey = secondKey;
    }
    public SecondSortKey() {
    }
    public int compareTo(SecondSortKey o) {
        return this.firstKey.compareTo(o.getFirstKey());
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(firstKey.toString());
        dataOutput.writeInt(secondKey.get());
    }
    public void readFields(DataInput dataInput) throws IOException {
        firstKey = new Text(dataInput.readUTF());
        secondKey = new IntWritable(dataInput.readInt());
    }
    @Override
    public String toString(){
        return this.getFirstKey() + " " + this.getSecondKey();
    }
}
