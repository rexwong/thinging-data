package com.rexwong.thinkingdata.crawl

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import com.hankcs.hanlp.tokenizer.NLPTokenizer
import org.apache.spark.ml.feature._
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import scalaj.http.{Http, HttpOptions}

import scala.collection.JavaConversions._

/**
  * 抓取youku评论
  *
  * ./spark-submit --class "com.rexwong.thinkingdata.crawl.YoukuCommit" \
  * --master yarn --deploy-mode cluster --driver-memory 1G  \
  * --executor-memory 1G --executor-cores 1 \
  * thinking-data-spark-1.0-SNAPSHOT.jar
  *
  * @author rex
  */
object YoukuCommit {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("YoukuCommitExample")
      .getOrCreate()
    val data = crawlDouban("1")
    val pages = Array("1", "2", "3", "4","5","6")
    var array: Seq[(Double, String)] = Seq()
    pages.foreach(page => {
      val data = crawlDouban(page)
      data.foreach(row=>{
        val json = row.asInstanceOf[JSONObject]
        val content = json.getString("content")
        val videoId = json.getDouble("id")
        val commit = content.replaceAll("#", ",").wordSplit()
        array = array.:+(videoId.toDouble, commit.mkString("\t"))
      })
    })
    val sentenceData = spark.createDataFrame(array).toDF("videoId", "sentence")
    //      sentencdata.write.save("/train/youku-douban-commit")
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)
    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20000)//设置哈希桶的数量

    val featurizedData = hashingTF.transform(wordsData)
    featurizedData.cache()
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    val idf = new IDF().setMinDocFreq(3).setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)

    rescaledData.select("words","features").show(50)

    val df = rescaledData.select("videoId", "words", "features")

    df.show(50, false)

//    val selector = new ChiSqSelector()
//      .setNumTopFeatures(10)
//      .setFeaturesCol("features")
//      .setLabelCol("videoId")
//      .setOutputCol("selectedFeatures")
//
//    val result = selector.fit(df).transform(df)
//
//    println(s"ChiSqSelector output with top ${selector.getNumTopFeatures} features selected")
//    result.select("words","selectedFeatures").show(50, false)
    //      rescaledData.rdd.saveAsTextFile("/data/comments-tf-idf")

    spark.stop()
  }
  def crawlDouban(page:String):Seq[Object]={
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
      Nil
    }
    val pageResule = json.getJSONObject("data").getJSONObject("con").getJSONObject("pageResult")
    pageResule.getJSONArray("data").toArray().toSeq
  }
  implicit class WordSplit(word: String) extends Serializable {

    def wordSplit(flag: Boolean = false): Seq[String] = {
      Option(word) match {
        case None => Seq.empty[String]
        case Some(s) => {
          val el = NLPTokenizer.segment(s.trim)
          val result = if (el.isEmpty) Seq.empty[String]
          else {
            el.map(x => {
              x.word.trim.replaceAll(" ", "")
            }).filterNot(_.isEmpty).distinct
          }
          flag match {
            case false => result
            case true => result.map(_.replaceAll(result.mkString("  "), ""))
          }
        }
      }
    }
  }

}
