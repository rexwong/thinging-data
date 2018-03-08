package com.rexwong.thinkingdata.spark.examples

import org.apache.spark.{SparkConf, SparkContext}

object DoubanMovie {
  def main(args: Array[String]): Unit = {
//    val sparkConf = new SparkConf().setAppName("SparkCrawler")
//    val spark = new SparkContext(sparkConf)
//    val textFile = spark.textFile("hdfs://node1:9000//test/douban/movie/douban-comment.txt")
//    val counts = textFile.map(_.split("\t")).map(_(2)).countByValue()
    "2018-02-16 00:05:42\t50\t11076\t本来对这类电影不感兴趣，陪着男朋友去看的，很意外，还不错，一部很燃的片子，俩个多小时的电影，至少一个半小时的高潮，全程无尿点，据说是根据真实事件改编的，海陆空作战，超级帅。算是春节档电影的一股清流，大家真的要感受一下中国军人的风采，只想说威武！！佟莉炸飞机还有狙击手对战那段太帅了".split("\t").foreach(println)
  }
}
