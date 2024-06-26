package com.github.stijndehaes.playprometheusfilters.controllers

import org.apache.pekko.util.ByteString

import javax.inject._
import play.api.mvc._
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.slf4j.LoggerFactory
import play.api.http.HttpEntity

import java.io.StringWriter

/**
 * A Play controller implementation to return collected metrics. Use this controller to create an endpoint which can be
 * scraped by Prometheus.
 *
 * Add to your `routes.conf`:
 * {{{
 *   # Prometheus metrics
 *   GET  /metrics  io.github.stijndehaes.playprometheusfilters.controllers.PrometheusController.getMetrics
 * }}}
 */
class PrometheusController @Inject() (registry: CollectorRegistry, cc: ControllerComponents)
    extends AbstractController(cc) {

  private val logger = LoggerFactory.getLogger(classOf[PrometheusController])

  def getMetrics: Action[AnyContent] = Action {
    logger.trace("Metrics call received")
    val samples = new StringWriter()
    TextFormat.write004(samples, registry.metricFamilySamples())

    Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Strict(ByteString(samples.toString), Some(TextFormat.CONTENT_TYPE_004))
    )
  }

}
