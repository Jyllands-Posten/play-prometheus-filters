package com.github.stijndehaes.playprometheusfilters.utils

import java.io.Writer

class WriterAdapter(buffer: StringBuilder) extends Writer {

  override def write(charArray: Array[Char], offset: Int, length: Int): Unit =
    buffer.appendAll(charArray, offset, length)

  override def flush(): Unit = {}

  override def close(): Unit = {}
}
