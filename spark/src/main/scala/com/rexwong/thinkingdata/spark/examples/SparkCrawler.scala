package com.rexwong.thinkingdata.spark.examples

import org.apache.spark.{SparkConf, SparkContext}
import org.jsoup.Jsoup

object SparkCrawler {
  def main(args: Array[String]): Unit = {

    StreamingExamples.setStreamingLogLevels()
    val sparkConf = new SparkConf().setAppName("SparkCrawler")
    val spark = new SparkContext(sparkConf)

    var urls: List[String] = List()
    var url = ""
    val maxPage=10 //121800/20+1;
    for (index <- 0 to 121800/20+1) {
      url = "https://movie.douban.com/subject/26861685/comments?start=" + index + "&limit=20&sort=new_score&status=P&percent_type="
      urls = urls.:+(url)
    }
    var result: List[String] = List()
    val count = spark.parallelize(urls,100).map(url => {
      val html = Jsoup.connect(url).get()
      val elements = html.select("div.comment")
      val ite = elements.iterator()
      while (ite.hasNext){
        val element = ite.next()
        val votes = element.select("span.votes").first().ownText()
        val content = element.select("p").first().ownText()
        result=result.:+(votes+"\t"+content)
      }
    })
    val resultRDD = spark.parallelize(urls,100)
    resultRDD.saveAsTextFile("hdfs://node1:9000/test/urls")
  }
}
