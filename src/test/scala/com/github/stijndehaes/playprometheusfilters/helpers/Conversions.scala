package com.github.stijndehaes.playprometheusfilters.helpers

import io.prometheus.client.Collector.MetricFamilySamples

import java.util
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
object Conversions {

  case class MetricSample(metricName: String, value: Double, labelValues: List[String])

  def toMetricSamples(javaMetricSamples: util.List[MetricFamilySamples.Sample]): List[MetricSample] =
    javaMetricSamples.map { sample =>
      MetricSample(sample.name, sample.value, sample.labelValues)
    }

  implicit def toScalaList[A](jList: java.util.List[A]): List[A] = jList.asScala.toList
}
