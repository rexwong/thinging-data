package com.rexwong.thinkingdata.spark.examples


import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory

object StreamingExamples {

  val log = LoggerFactory.getLogger(StreamingExamples.getClass)

  def setStreamingLogLevels() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      // We first log something to initialize Spark's default logging, then we override the
      // logging level.
      log.info("Setting log level to [WARN] for streaming example." +
        " To override add a custom log4j.properties to the classpath.")
      Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}
