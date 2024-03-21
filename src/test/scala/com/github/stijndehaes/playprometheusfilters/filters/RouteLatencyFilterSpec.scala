package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.helpers.Conversions._
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults.UnmatchedRouteString
import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.libs.typedmap.TypedMap
import play.api.mvc._
import play.api.routing.{HandlerDef, Router}
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class RouteLatencyFilterSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with Results
    with DefaultAwaitTimeout
    with FutureAwaits
    with GuiceOneAppPerSuite {

  private implicit val mat: Materializer = app.materializer
  private val configuration = mock[Configuration]

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new RouteLatencyFilter(collectorRegistry, configuration)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {
      val filter = new RouteLatencyFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest().withAttrs(
        TypedMap(
          Router.Attrs.HandlerDef -> HandlerDef(null, null, null, "test", null, null, null, null, null)
        )
      )
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val latencyMetrics = filter.metrics.head.metric.collect()
      latencyMetrics must have size 1

      val maybeRequestLatencySecondsCount: Option[MetricSample] =
        toMetricSamples(latencyMetrics.head.samples)
          .find(_.metricName.equals("requests_latency_seconds_count"))

      maybeRequestLatencySecondsCount must not be empty
      maybeRequestLatencySecondsCount.map { result =>
        result.value mustBe 1.0
        result.labelValues must have size 1
        result.labelValues mustBe List("test")
      }
    }

    "Measure the latency for an unmatched route" in {
      val filter = new RouteLatencyFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).error

      await(filter(action)(rh).run())

      val latencyMetrics = filter.metrics.head.metric.collect()
      latencyMetrics must have size 1

      val maybeRequestLatencySecondsCount: Option[MetricSample] =
        toMetricSamples(latencyMetrics.head.samples)
          .find(_.metricName.equals("requests_latency_seconds_count"))

      maybeRequestLatencySecondsCount must not be empty
      maybeRequestLatencySecondsCount.map { result =>
        result.value mustBe 1.0
        result.labelValues must have size 1
        result.labelValues mustBe List(UnmatchedRouteString)
      }
    }
  }
}
