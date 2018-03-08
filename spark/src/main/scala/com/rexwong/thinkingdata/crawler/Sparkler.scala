package com.rexwong.thinkingdata.crawler

import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkConf, SparkContext, TaskContext}

import scala.collection.mutable.ArrayBuffer

class CrawlPartition(rddId: Int, idx: Int, val baseURL: String) extends Partition {
  override def index: Int = ???
}

class CrawlRDD[X](baseURL: String, sc: SparkContext) extends RDD[X](sc, Nil) {

  override protected def getPartitions: Array[CrawlPartition] = {
    val partitions = new ArrayBuffer[CrawlPartition]
    //split baseURL to subsets and populate the partitions
    partitions.toArray
  }

  override def compute(part: Partition, context: TaskContext): Iterator[X] = {
    val p = part.asInstanceOf[CrawlPartition]
    val baseUrl = p.baseURL

    new Iterator[X] {
      var nextURL = ""

      override def hasNext: Boolean = {
        //logic to find next url if has one, fill in nextURL and return true
        // else false
        false
      }

      override def next(): Boolean = {
        //logic to crawl the web page nextURL and return the content in X
        false
      }
    }
  }
}

object Sparkler {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("Crawler")
    val sc = new SparkContext(sparkConf)

    val crdd = new CrawlRDD("baseURL", sc)
    crdd.saveAsTextFile("hdfs://path_here")
    sc.stop()
  }
}
