package TypeClass

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import java.io.PrintWriter

trait Write {
  def write(fileName: String, data: String): Unit
}

object WriterInstances {
  implicit val localFSWriter: Write =
    (fileName: String, data: String) => {
      val writer = new PrintWriter(fileName)
      writer.println(data)
      writer.close()
    }

  implicit val HDFSWriter: Write =
    (fileName: String, data: String) => {
      val conf = new Configuration()
      val fs = FileSystem.get(conf)
      val output = fs.create(new Path(s"hdfs://somePath/$fileName"))

      val writer = new PrintWriter(output)
      writer.println(data)
      writer.close()
    }
}
