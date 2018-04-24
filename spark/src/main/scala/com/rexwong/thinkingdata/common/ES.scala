package com.rexwong.thinkingdata.common

import com.rexwong.thinkingdata.config.EsConfig
import com.sksamuel.elastic4s.RefreshPolicy
import grizzled.slf4j.Logger

import scala.concurrent.duration._

object ES {

  @transient lazy implicit val log: Logger = Logger[this.type]

  import com.sksamuel.elastic4s.ElasticsearchClientUri
  import com.sksamuel.elastic4s.http.HttpClient
  import com.sksamuel.elastic4s.http.ElasticDsl._
  def batchWrite(index: String, `type`: String, content: List[Map[String, Any]]): Unit = {

    val client = HttpClient(ElasticsearchClientUri(EsConfig.esEndPoint))
    client.execute {
      bulk(
        content.map(row => {
          update(row.get("id").get.toString).in(index / "1").docAsUpsert(row)
        })
      ).refresh(RefreshPolicy.WAIT_UNTIL)
    }.await(1800.seconds)

    client.close()
  }
}
