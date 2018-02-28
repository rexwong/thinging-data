package com.rexwong.thinkingdata.crawler

import org.jsoup.Jsoup

import scala.util.matching.Regex

/**
  * 豆瓣电影评论爬虫
  */
object DoubanMovieCrawler {
  def main(args: Array[String]): Unit = {
    var urls: List[String] = List()
    var url = ""
    for (index <- 0 to 1) {
      url="https://movie.douban.com/subject/25662329/comments?start=" + index + "&limit=20&sort=new_score&status=P&percent_type="
      urls = urls.:+(url)
    }
    val regex = new Regex("""(.+)(http://.+)\">""")
    urls.foreach(url => {
      val html = Jsoup.connect(url).get()
      val elements  = html.select("div.comment")
      val imgs = html.select("div#comments").toString().split("\n")
      elements.forEach(el=>{
        val votes = el.select("span.votes").first().ownText()
        val content = el.select("p").first().ownText()
        println(content)
      })
      Thread.sleep(3000)
    })
  }
}
