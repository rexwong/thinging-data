package com.rexwong.thinkingdata.hadoop.group;

import com.rexwong.thinkingdata.hadoop.io.SecondSortKey;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SecondSortGroup extends WritableComparator {

    public SecondSortGroup(){
        super(SecondSortKey.class, true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        SecondSortKey entityPair1 = (SecondSortKey) a;
        SecondSortKey entityPair2 = (SecondSortKey) b;
        System.out.println("=========GroupComparator=========");
        return entityPair1.getFirstKey().toString().compareTo(entityPair2.getFirstKey().toString());
    }
}
