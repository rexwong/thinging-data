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
import com.rexwong.thinkingdata.ml.NaiveBayesExample.RawDataRecord
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

object PipelineExample {

  case class RawDataRecord(videoId: Double, sentence: String)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("PipelineExample")
      .getOrCreate()

    // Prepare training documents from a list of (videoId, sentence, label) tuples.
    import spark.implicits._
    val training = spark.read.textFile("/user/hadoop/data/comments.txt").map {
      row =>
        val data = row.split("^")
        RawDataRecord(data(0).toDouble, data(1))
    }

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    val tokenizer = new Tokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")

    val hashingTF = new HashingTF()
      .setNumFeatures(1000)
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("features")

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.001)

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, hashingTF, lr))

    // Fit the pipeline to training documents.
    val model = pipeline.fit(training)

    // Now we can optionally save the fitted pipeline to disk
    model.write.overwrite().save("/tmp/spark-logistic-regression-model")

    // We can also save this unfit pipeline to disk
    pipeline.write.overwrite().save("/tmp/unfit-lr-model")

    // And load it back in during production
    val sameModel = PipelineModel.load("/tmp/spark-logistic-regression-model")

    // Prepare test documents, which are unlabeled (id, text) tuples.
    val test = spark.createDataFrame(Seq(
      (4L, "spark i j k"),
      (5L, "l m n"),
      (6L, "spark hadoop spark"),
      (7L, "apache hadoop")
    )).toDF("videoId", "sentence")

    // Make predictions on test documents.
    model.transform(test)
      .select("videoId", "sentence", "probability", "prediction")
      .collect()
      .foreach { case Row(videoId: Long, sentence: String, prob: Vector, prediction: Double) =>
        println(s"($videoId, $sentence) --> prob=$prob, prediction=$prediction")
      }

    spark.stop()
  }
}
