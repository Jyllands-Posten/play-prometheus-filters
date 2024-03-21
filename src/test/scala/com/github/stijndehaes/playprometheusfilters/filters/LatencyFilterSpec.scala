package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.helpers.Conversions._
import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import io.prometheus.client.CollectorRegistry
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class LatencyFilterSpec
    extends PlaySpec
    with MockitoSugar
    with Results
    with DefaultAwaitTimeout
    with FutureAwaits
    with GuiceOneAppPerSuite {

  private implicit val actorSystem: ActorSystem = ActorSystem("test")

  private val configuration: Configuration = mock[Configuration]

  "Filter constructor" should {
    "Add a histogram to the prometheus registry" in {
      val collectorRegistry = mock[CollectorRegistry]
      new LatencyFilter(collectorRegistry, configuration)
      verify(collectorRegistry).register(any())
    }
  }

  "Apply method" should {
    "Measure the latency" in {

      implicit val mat: Materializer = app.materializer
      val filter = new LatencyFilter(mock[CollectorRegistry], configuration)
      val rh = FakeRequest()
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics.head.metric.collect()

      val maybeRequestLatencySecondsCount: Option[MetricSample] =
        toMetricSamples(metrics.head.samples)
          .find(_.metricName.equals("requests_latency_seconds_count"))

      metrics must have size 1
      maybeRequestLatencySecondsCount must not be empty
      maybeRequestLatencySecondsCount.map { result =>
        result.value mustBe 1.0
        result.labelValues must have size 0
      }
    }
  }
}
