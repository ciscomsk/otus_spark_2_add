package StrategyPattern

import java.io.PrintWriter

class WriteToLocalFS extends WriteStrategy {
  def write(fileName: String, data: String): Unit = {
    val writer = new PrintWriter(fileName)
    writer.println(data)
    writer.close()
  }
}
