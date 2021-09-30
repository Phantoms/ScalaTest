//  {
//    "supportedCurrencies": ["USD", ...]
//  }
final case class SupportedCurrenciesResponse(supportedCurrencies: List[String])

class ListSupportedCurrenciesUseCase(currencyDataResourceAccess: CurrencyDataResourceAccess) {
  def apply(): SupportedCurrenciesResponse = {
    val currencyData = currencyDataResourceAccess.GetData()
    val sortedCurrencySymbols = currencyData.map(_.code).sorted
    SupportedCurrenciesResponse(sortedCurrencySymbols)
  }
}
