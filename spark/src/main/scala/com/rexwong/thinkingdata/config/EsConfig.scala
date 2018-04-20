package com.rexwong.thinkingdata.config

object EsConfig {
  val esEndPoint = s"elasticsearch://search-pio-test02-oancf2y3wa5lkuhvshqxrnmixa.ap-southeast-1.es.amazonaws.com:443?ssl=true"
  val esIndex = "idflabel"
  val esType = "1"
  val writeEsBatch = 500
}
