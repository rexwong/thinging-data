package com.rexwong.thinkingdata.crawler

import org.jsoup.Jsoup

/**
  * 豆瓣电影短评爬虫
  * 内容是已经观看电影关注的短评内容
  */
object DoubanMovieCrawler {
  def main(args: Array[String]): Unit = {
    var urls: List[String] = List()
    var url = ""
    for (index <- 0 to 0) {
      url = "https://movie.douban.com/subject/26861685/comments?start=" + index + "&limit=20&sort=new_score&status=P&percent_type="
      urls = urls.:+(url)
    }
    urls.foreach(url => {
      val html = Jsoup.connect(url).get()
      val elements = html.select("div.comment")
      elements.forEach(el => {
        val votes = el.select("span.votes").first().ownText()
        val content = el.select("p").first().ownText()
        println(content)
      })
      Thread.sleep(3000)
    })
  }
}
