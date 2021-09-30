import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ConvertFromEuroToCurrencyUseCaseTest extends AnyFunSuite {
  test("All") {
    val mockDataResource = new MockDataResourceAccess(
      List(
        CurrencyData("USD", "Dollars", 2, 0.5),
        CurrencyData("JPY", "Yen", 10, 0.1)
      ))

    val useCase = new ConvertFromEuroToCurrencyUseCase(mockDataResource)

    // Happy path
    useCase("USD", "2000") should equal(Right(FromEuroToCurrencyResponse("EUR", "USD", 2000.0, 4000.0)))

    // Unknown currency
    useCase("USDK", "2000") should equal(Left("Unknown currency"))

    // Negative amount
    useCase("USD", "-2000") should equal(Left("Amount must be positive number"))

    // Invalid amount
    useCase("USD", "zzzz") should equal(Left("Incorrect amount, must be a number"))

    // Error in both the currency and amount
    useCase("USDK", "zzz") should equal(Left("Unknown currency and incorrect amount"))
  }
}