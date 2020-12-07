package StrategyPattern

trait WriteStrategy {
  def write(fileName: String, data: String): Unit
}
