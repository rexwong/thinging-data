package com.rexwong.thinkingdata.hadoop.io;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class KeyComparator extends WritableComparator {

    // 构造函数必须有
    public KeyComparator(){
        super(Text.class, true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        // 对复合键进行切分
        String[] arr_a = a.toString().split(" ");
        String[] arr_b = b.toString().split(" ");
        System.out.println("=========KeyComparator=========");
        // 先比较第一个字段然后再比较第二个
        if (arr_a[0].compareTo(arr_b[0]) != 0){
            return arr_a[0].compareTo(arr_b[0]);
        }else {
            return Integer.parseInt(arr_a[1]) - Integer.parseInt(arr_b[1]);
        }
    }
}
