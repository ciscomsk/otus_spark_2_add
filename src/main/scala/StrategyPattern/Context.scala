package StrategyPattern

class Context {
  private var writeStrategy: Option[WriteStrategy] = None

  def setStrategy(writeStrategy: Option[WriteStrategy]): Unit =
    this.writeStrategy = writeStrategy

  def write(fileName: String, data: String): Unit =
    writeStrategy match {
      case Some(strategy) => strategy.write(fileName, data)
      case None => println("Write strategy is not set")
    }

}
