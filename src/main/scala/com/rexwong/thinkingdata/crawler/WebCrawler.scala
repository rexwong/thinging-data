package com.rexwong.thinkingdata.crawler

import java.io._
import java.net.URL

import org.jsoup.Jsoup

import scala.util.matching.Regex

object WebCrawler {

  def main(args: Array[String]): Unit = {
    var urls: List[String] = List()
    var url = ""
    for (index <- 1 to 10) {
      url = "http://www.kanmeizi.cn/index_" + index + "_16.html"
      urls = urls.:+(url)
    }
    var num = 1
    val regex = new Regex("""(.+)(http://.+)\">""")
    urls.foreach(url => {
      //      val Thread_Num = 30 //指定并发执行线程数
      val html = Jsoup.connect(url).get()
      val imgs = html.select("img[src$=.jpg]").toString().split("\n")

      imgs.foreach(img => {
        var regex(begin, url) = img
        url = url.replaceAll("bmiddle", "large")
        println(s"url=$url")

        //              val pw = new PrintWriter(new File("/Users/jiangzl/tmp/large/"+url.split("/").last))
        //              val is = new URL(url).openStream
        //              pw.write(Source.fromURL(url, "ISO-8859-1").mkString)

        val pw = new FileOutputStream(new File("/Users/rexwong/Documents/imgs/" + num + url.split("/").last))
        val is = new URL(url).openStream
        // 学习地址
        // http://stackoverflow.com/questions/4905393/scala-inputstream-to-arraybyte
        pw.write(Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray)
        pw.close()
        num = num.+(1)
      })
      Thread.sleep(3000)

    })
  }
}
