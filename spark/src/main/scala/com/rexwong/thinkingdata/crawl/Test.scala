package com.rexwong.thinkingdata.crawl

import com.alibaba.fastjson.JSON
import scalaj.http.{Http, HttpOptions}

object Test {

  def main(args: Array[String]): Unit = {

    val pages = Array("1", "2", "3", "4","5","6")
    var data = Seq()
    pages.foreach(page => {
      val data_o = crawlDouban(page)
      println(data_o.size)
    })
  }
  def addOne(m: Int): Int = m + 1
  def crawlDouban(page: String): Seq[Object] ={
    val result = Http(HttpConfig.url)
      .params(Map("app" -> "100-DDwODVkv",
        "page" -> page,
        "videoId" -> "883924481",
        "showId" -> "322277",
        "sign" -> "bc7fe10a462227b9e0158a0984fdf870",
        "time" -> "1524448799"))
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
    val json = JSON.parseObject(result.body)
    if (!json.getString("message").equals("success")) {
      null
    }
    val pageResule = json.getJSONObject("data").getJSONObject("con").getJSONObject("pageResult")
    pageResule.getJSONArray("data").toArray().toSeq
  }
}
