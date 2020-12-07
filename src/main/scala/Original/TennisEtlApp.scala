package Original

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import java.io.PrintWriter
import scala.io.Source
import scala.util.{Failure, Success, Try}

object TennisEtlApp extends App {

  case class Date(year: Int, month: Int, day: Int)

  case class TennisMatch(
                          tourneyName: String,
                          winnerName: String,
                          loserName: String,
                          date: Date
                        )

  def parseHeaders(headers: String): Map[String, Int] =
    headers.split(",").zipWithIndex.toMap

  def parseLine(line: String, headers: Map[String, Int]): TennisMatch = {
    val tokens = line.split(",")
    val dateString = tokens(headers("tourney_date"))

    val date = Date(
      dateString.take(4).toInt,
      dateString.slice(4, 6).toInt,
      dateString.drop(6).toInt
    )

    TennisMatch(
      tokens(headers("tourney_name")),
      tokens(headers("winner_name")),
      tokens(headers("loser_name")),
      date
    )
  }

  def source = Source.fromURL("https://raw.githubusercontent.com/JeffSackmann/tennis_atp/master/atp_matches_2020.csv")

  val head :: records = source.getLines.toList
  val headers: Map[String, Int] = parseHeaders(head)
  val matches: Seq[TennisMatch] = records.map(parseLine(_, headers))

  def filterRecords(playerName: String, month: Int)(records: Iterable[TennisMatch]): Iterable[TennisMatch] =
    records
      .filter(record =>
        (record.loserName == playerName || record.winnerName == playerName)
          && record.date.month == month
      )

  def renderRecord(playerName: String)(record: TennisMatch): String =
    record match {
      case TennisMatch(tourneyName, winnerName, loserName, _) if loserName == playerName
      => List(tourneyName, winnerName, "L").mkString(",")
      case TennisMatch(tourneyName, winnerName, loserName, _) if winnerName == playerName
      => List(tourneyName, loserName, "W").mkString(",")
    }

  def prepareData(playerName: String, month: Int): String =
    filterRecords(playerName, month)(matches)
      .map(Tmatch => renderRecord(playerName)(Tmatch))
      .mkString("\n")

  def writeToFile(fileName: String, data: String): Unit = {
    val writer = new PrintWriter(fileName)
    writer.println(data)
    writer.close()
  }

  def writeToHDFS(fileName: String, data: String): Unit = {
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.create(new Path(s"hdfs://somePath/$fileName"))

    val writer = new PrintWriter(output)
    writer.println(data)
    writer.close()
  }

  val playerName = args(0)
  val monthT = Try {
    args(1).toInt
  }
  val outFile = args(2)

  monthT match {
    case Success(month) =>
      val data: String = prepareData(playerName, month)
      writeToFile(outFile, data)
    case Failure(ex) => println(ex)
  }

}
