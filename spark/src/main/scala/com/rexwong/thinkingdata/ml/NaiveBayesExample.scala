package com.rexwong.thinkingdata.ml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.sql.SparkSession

/**
  * $SPARK_HOME/bin/spark-submit --class "com.rexwong.thinkingdata.ml.NaiveBayesExample" \
  * --master yarn \
  * --deploy-mode cluster \
  * --driver-memory 2G \
  * --executor-memory 2G \
  * --executor-cores 3 \
  * thinking-data-spark-1.0-SNAPSHOT.jar
  */
object NaiveBayesExample {

  case class RawDataRecord(videoId: Double, text: String)

  def main(args: Array[String]): Unit = {

    val sc = SparkSession
      .builder
      .appName("NaiveBayesExample")
      .getOrCreate()

    val file = sc.read.textFile("/train-data/sougou/")

    import sc.implicits._
    val sentenceData = file.map {
      row =>
        var data = row.split(",")
        RawDataRecord(data(0).toDouble, data(1))
    }

    val srcDF = sentenceData.toDF()
    val Array(trainingDF, testDF) = srcDF.randomSplit(Array(0.7, 0.3))

    //将词语转换成数组
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val wordsData = tokenizer.transform(trainingDF)

    //计算每个词在文档中的词频
    val hashingTF = new HashingTF().setNumFeatures(500000)
      .setInputCol("words").setOutputCol("rawFeatures")

    val featurizedData = hashingTF.transform(wordsData)

    //计算每个词的TF-IDF
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)
    val rescaledData = idfModel.transform(featurizedData)

    //转换成Bayes的输入格式
    val trainDataRdd = rescaledData.select($"videoId", $"features")
    trainDataRdd.show(10, false)

    //训练模型
    val model = new NaiveBayes()
      .setFeaturesCol("features")
      .setLabelCol("videoId")
      .setModelType("multinomial")
      .fit(trainDataRdd)

    //测试数据集，做同样的特征表示及格式转换
    val testwordsData = tokenizer.transform(testDF)
    val testfeaturizedData = hashingTF.transform(testwordsData)
    val testrescaledData = idfModel.transform(testfeaturizedData)
    val testDataRdd = testrescaledData.select($"videoId", $"features")

    //对测试数据集使用训练模型进行分类预测
    val predictions = model.transform(testDataRdd)
    predictions.show()

//    Select(prediction, true label) and compute test error
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("videoId")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val testpredictionAndLabel = evaluator.evaluate(predictions)

    println(s"Test set accuracy = $testpredictionAndLabel")

    sc.stop()
  }


}
