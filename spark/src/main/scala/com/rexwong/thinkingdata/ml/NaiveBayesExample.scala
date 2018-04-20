package com.rexwong.thinkingdata.ml

import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, IDF, LabeledPoint, Tokenizer}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}


object NaiveBayesExample {
  case class RawDataRecord(category: String, text: String)
  def main(args : Array[String]): Unit = {

    val sc = SparkSession
      .builder
      .appName("NaiveBayesExample")
      .getOrCreate()
    import sc.implicits._

    val srcRDD = sc.read.textFile("/train/sougou/").map {
      x =>
        var data = x.split(",")
        RawDataRecord(data(0),data(1))
    }

    //70%作为训练数据，30%作为测试数据
    val splits = srcRDD.randomSplit(Array(0.7, 0.3))
    var trainingDF = splits(0).toDF()
    var testDF = splits(1).toDF()

    //将词语转换成数组
    var tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    var wordsData = tokenizer.transform(trainingDF)
    println("output1：")
    wordsData.select($"category",$"text",$"words").take(1)

    //计算每个词在文档中的词频
    var hashingTF = new HashingTF().setNumFeatures(500000).setInputCol("words").setOutputCol("rawFeatures")
    var featurizedData = hashingTF.transform(wordsData)
    println("output2：")
    featurizedData.select($"category", $"words", $"rawFeatures").take(1)


    //计算每个词的TF-IDF
    var idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    var idfModel = idf.fit(featurizedData)
    var rescaledData = idfModel.transform(featurizedData)
    println("output3：")
    rescaledData.select($"category", $"features").take(1)

    //转换成Bayes的输入格式
    var trainDataRdd = rescaledData.select($"category",$"features").map {
      case Row(label: String, features: Vector[Double]) =>
        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
    }
    println("output4：")
    trainDataRdd.take(1)
    //训练模型
//    val model = new NaiveBayes.train(trainDataRdd, lambda = 1.0, modelType = "multinomial")
    val model = new NaiveBayes().fit(trainDataRdd)

    //测试数据集，做同样的特征表示及格式转换
    var testwordsData = tokenizer.transform(testDF)
    var testfeaturizedData = hashingTF.transform(testwordsData)
    var testrescaledData = idfModel.transform(testfeaturizedData)
    var testDataRdd = testrescaledData.select($"category",$"features").map {
      case Row(label: String, features: Vector[Double]) =>
        LabeledPoint(label.toDouble, Vectors.dense(features.toArray))
    }

    //对测试数据集使用训练模型进行分类预测
    val predictions = model.transform(testDataRdd)
    predictions.show()
    // Select (prediction, true label) and compute test error
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val testpredictionAndLabel = evaluator.evaluate(predictions)
    println(s"Test set accuracy = $testpredictionAndLabel")
//    val testpredictionAndLabel = testDataRdd.map(p => (model.predict(p.features), p.label))

//    //统计分类准确率
//    var testaccuracy = 1.0 * testpredictionAndLabel.filter(x => x._1 == x._2).count() / testDataRdd.count()
//    println("output5：")
//    println(testaccuracy)

  }
}
