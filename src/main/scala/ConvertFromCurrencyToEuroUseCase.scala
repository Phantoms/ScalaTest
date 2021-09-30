//  {
//    "from": "JPY",
//    "to": "EUR",
//    "amount": 100,
//    "convertedAmount": 9800
//  }
final case class FromCurrencyToEuroResponse(from: String, to: String, amount: Double, convertedAmount: Double)

class ConvertFromCurrencyToEuroUseCase(currencyDataResourceAccess: CurrencyDataResourceAccess) extends AmountUtils {
  def apply(currency: String, stringAmount: String): Either[String, FromCurrencyToEuroResponse] = {
    val currencyData = currencyDataResourceAccess.GetData()

    val currencyUpperCase = currency.toUpperCase()
    val parsedAmount = parseAmount(stringAmount)
    val currencyDataItem = currencyData.find( _.code == currencyUpperCase )

    (parsedAmount, currencyDataItem) match {
      case (Right(amount), Some(d)) =>
        val convertedAmount = d.inverseRate * amount
        Right(FromCurrencyToEuroResponse(
          from=currencyUpperCase,
          to="EUR",
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

