/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package com.rexwong.thinkingdata.ml

// $example on$
import com.hankcs.hanlp.tokenizer.NLPTokenizer
import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.Row

import scala.collection.JavaConversions._
// $example off$
import org.apache.spark.sql.SparkSession

/**
  * 特征值提取
  *
  * ./spark-submit --class "com.rexwong.thinkingdata.nlp.Word2VecExample" \
  * --master yarn --deploy-mode cluster --driver-memory 1G  \
  * --executor-memory 1G --executor-cores 1 \
  * thinking-data-spark-1.0-SNAPSHOT.jar
  */
object Word2VecExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("Word2Vec example")
      .getOrCreate()

    val commentsData = spark.read.textFile("/user/hadoop/data/comments.txt").rdd

    val data = commentsData.map(row => {
      row.replaceAll("#", ",").wordSplit()
    })
    //    data.saveAsTextFile("/data/words");
    val documentDF = spark.createDataFrame(data.map(Tuple1.apply)).toDF("text")
    //
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(3)
      .setMinCount(0)

    val model = word2Vec.fit(documentDF)

    val result = model.transform(documentDF)

    result.show()

    spark.stop()
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

// scalastyle:on println
