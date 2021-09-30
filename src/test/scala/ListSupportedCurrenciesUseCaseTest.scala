import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}

class ListSupportedCurrenciesUseCaseTest extends AnyFunSuite {
  test("All") {
    val mockData = new MockDataResourceAccess(
      List(
        CurrencyData("USD", "Dollars", 1.167, 0.856),
        CurrencyData("JPY", "Yen", 130.000, 0.007)
      ))

    val useCase = new ListSupportedCurrenciesUseCase(mockData)

    useCase() should equal(SupportedCurrenciesResponse(List("JPY", "USD")))
  }
}