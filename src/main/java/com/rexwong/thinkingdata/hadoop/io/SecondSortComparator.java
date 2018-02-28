package com.rexwong.thinkingdata.hadoop.io;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 二次排序自定义比较器
 */
public class SecondSortComparator extends WritableComparator {

    public SecondSortComparator(){
        super(SecondSortKey.class, true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        SecondSortKey entityPair1 = (SecondSortKey) a;
        SecondSortKey entityPair2 = (SecondSortKey) b;
        System.out.println("=========Comparator=========");
        if (!entityPair1.getFirstKey().toString().equals(entityPair2.getFirstKey().toString())){
            return entityPair1.getFirstKey().toString().compareTo(entityPair2.getFirstKey().toString());
        }else {
            return entityPair1.getSecondKey().get() - entityPair2.getSecondKey().get();
        }
    }
}
