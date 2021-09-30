import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends App {
  implicit lazy val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "CurrencyConverter")
  implicit lazy val executionContext: ExecutionContextExecutor = system.executionContext

  implicit lazy val supportedCurrenciesResponseFormat = jsonFormat1(SupportedCurrenciesResponse)
  implicit lazy val convertFromCurrencyToEuroUseCaseFormat = jsonFormat4(FromCurrencyToEuroResponse)
  implicit lazy val convertFromEuroToCurrencyUseCaseFormat = jsonFormat4(FromEuroToCurrencyResponse)

  val currencyDataResourceAccess: CurrencyDataResourceAccess = new FloatRatesResourceAccess()
  val listSupportedCurrenciesUseCase = new ListSupportedCurrenciesUseCase(currencyDataResourceAccess)
  val convertFromCurrencyToEuroUseCase = new ConvertFromCurrencyToEuroUseCase(currencyDataResourceAccess)
  val convertFromEuroToCurrencyUseCase = new ConvertFromEuroToCurrencyUseCase(currencyDataResourceAccess)

  // Load all the data on start, fail and exit early if could not load
  println("Loading currency conversion data...")
  currencyDataResourceAccess.LoadData() match {
    case Right(_) =>
      println("Currency conversion data was successfully loaded.")
    case Left(_) =>
      println("Could not load currency conversion data, exiting...")
      System.exit(1)
  }

  // Build path matchers and routes
  val pathListCurrencies =
    path( "supported" ) {
      complete(listSupportedCurrenciesUseCase())
    }

  val pathConvertFromEurToAny =
    path( "from" / Segment / Segment ) { (currency, amount) =>
      convertFromCurrencyToEuroUseCase(currency, amount) match {
        case Right(v) => complete(v)
        case Left(e) => complete(StatusCodes.BadRequest, e)
      }
    }

  val pathConvertFromAnyToEur =
    path( "to" / Segment / Segment ) { (currency, amount) =>
      convertFromEuroToCurrencyUseCase(currency, amount) match {
        case Right(v) => complete(v)
        case Left(e) => complete(StatusCodes.BadRequest, e)
      }
    }

  val routes =
    pathListCurrencies ~ pathConvertFromEurToAny ~ pathConvertFromAnyToEur

  // Start server
  val hostName = "localhost"
  val port = 8080
  val bindingFuture = Http().newServerAt(hostName, port).bind(routes)

  println("\nServer now online.")
  println("Available endpoint examples:")
  println(s"http://${hostName}:${port}/supported")
  println(s"http://${hostName}:${port}/from/usd/1000")
  println(s"http://${hostName}:${port}/to/jpy/1000")
  println("\nPress RETURN to stop...")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}