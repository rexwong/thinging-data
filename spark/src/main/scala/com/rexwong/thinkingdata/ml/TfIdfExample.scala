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
import grizzled.slf4j.Logger
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
// $example off$
import org.apache.spark.sql.SparkSession

/**
  * $SPARK_HOME/bin/spark-submit --class "com.rexwong.thinkingdata.ml.TfIdfExample" \
  * --jars lib/grizzled-slf4j_2.11-1.3.2.jar \
  * --master yarn \
  * --deploy-mode cluster \
  * --driver-memory 2G \
  * --executor-memory 2G \
  * --executor-cores 3 \
  * thinking-data-spark-1.0-SNAPSHOT.jar
  */
object TfIdfExample {
  @transient lazy implicit val log: Logger = Logger[this.type]

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("TfIdfExample")
      .getOrCreate()

    val sentenceData =
      spark.read.textFile("/train/youku-douban-commit")
    sentenceData.show(100)

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)
    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)

    val featurizedData = hashingTF.transform(wordsData)
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.show(20, true)
    rescaledData.rdd.saveAsTextFile("/data/comments-tf-idf")
    spark.stop()
  }
}

// scalastyle:on println
