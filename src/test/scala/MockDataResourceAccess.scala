class MockDataResourceAccess(currencyData: List[CurrencyData]) extends CurrencyDataResourceAccess {
  def GetData(): List[CurrencyData] = currencyData
  def LoadData(): Either[Throwable, _] = Right()
}
