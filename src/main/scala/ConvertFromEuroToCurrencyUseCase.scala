//  {
//    "from": "EUR"
//    "to": "JPY",
//    "amount": 100,
//    "convertedAmount": 9800
//  }
case class FromEuroToCurrencyResponse(from: String, to: String, amount: Double, convertedAmount: Double)

class ConvertFromEuroToCurrencyUseCase(currencyConverterEngine: CurrencyDataResourceAccess) extends AmountUtils {
  def apply(currency: String, stringAmount: String): Either[String, FromEuroToCurrencyResponse] = {
    val currencyData = currencyConverterEngine.GetData()

    val currencyUpperCase = currency.toUpperCase()
    val parsedAmount = parseAmount(stringAmount)
    val currencyDataItem = currencyData.find( _.code == currencyUpperCase )

    (parsedAmount, currencyDataItem) match {
      case (Right(amount), Some(d)) =>
        val convertedAmount = d.rate * amount
        Right(FromEuroToCurrencyResponse(
          from="EUR",
          to=currencyUpperCase,
          amount=formatAmount(amount),
          convertedAmount=formatAmount(convertedAmount)))
      case (Left(_), None) =>
        Left("Unknown currency and incorrect amount")
      case (Left(e), _) =>
        Left(e)
      case (_, None) =>
        Left("Unknown currency")
    }
  }
}
