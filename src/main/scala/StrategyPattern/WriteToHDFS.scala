package StrategyPattern

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import java.io.PrintWriter

class WriteToHDFS extends WriteStrategy {
  def write(fileName: String, data: String): Unit = {
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.create(new Path(s"hdfs://somePath/$fileName"))

    val writer = new PrintWriter(output)
    writer.println(data)
    writer.close()
  }
}
