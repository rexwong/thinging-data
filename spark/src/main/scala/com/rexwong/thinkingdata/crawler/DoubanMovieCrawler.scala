package com.rexwong.thinkingdata.crawler


import java.io.FileWriter
import collection.JavaConverters._

import org.jsoup.{Connection, Jsoup}
/**
  * 豆瓣电影短评爬虫
  * 内容是已经观看电影关注的短评内容
  */
object DoubanMovieCrawler {
  def main(args: Array[String]): Unit = {
    var urls: List[String] = List()
    var url = "" //
    var index=220;
    while (index < 124170/20+1) {
      url = "https://movie.douban.com/subject/26861685/comments?start=" + index + "&limit=20&sort=new_score&status=P&percent_type="
      urls = urls.:+(url)
      index=index+20
    }
    val out = new FileWriter("/Users/rexwong/Documents/douban-comment.txt",true)
    val formData = Map(
      "redir"-> "https://www.douban.com",
      "form_email"-> "yongpeng.wong@gmail.com",
      "form_password"-> "zx569w9feo",
      "login"-> "u'登录'",
      "source"-> "None"
    )
    val headers = Map(
      "User-Agent"-> "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36",
      "Referer"-> "https://douban.com/accounts/login",
      "Host"-> "accounts.douban.com",
      "Connection"-> "Keep-Alive",
      "Content-Type"-> "application/x-www-form-urlencoded"
    )
    val con = Jsoup.connect("https://douban.com/accounts/login")
    val loginResponse =
      con.data(mapAsJavaMap(formData)).headers(mapAsJavaMap(headers)).method(Connection.Method.POST).execute()

    val userCookies = loginResponse.cookies()

    urls.foreach(url => {
      val html = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36").cookies(userCookies).get()
      val elements = html.select("div.comment")
      val ite = elements.iterator()
      while (ite.hasNext){
        val element = ite.next()
        val time = element.select("span.comment-time").first().attr("title")
        val rate = element.select("span.rating");
        var star=""
        if(!rate.isEmpty){
          val classNames = element.select("span.rating").first().classNames()
          classNames.forEach(classname=>{
            if(classname.contains("allstar")){
              star = classname.replaceAll("allstar","");
            }
          })
        }
        val votes = element.select("span.votes").first().ownText()
        val content = element.select("p").first().ownText()
        out.write(time+"\t"+star+"\t"+votes+"\t"+content+"\n")
      }
      println(url)
      Thread.sleep(3000)
    })
    out.close()
  }
}
