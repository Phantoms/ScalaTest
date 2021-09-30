import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json.DefaultJsonProtocol.{DoubleJsonFormat, StringJsonFormat, jsonFormat4, mapFormat}

import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

final case class CurrencyData(code: String, name: String, rate: Double, inverseRate: Double)

trait CurrencyDataResourceAccess {
  def GetData(): List[CurrencyData]
  def LoadData(): Either[Throwable, _]
}

class FloatRatesResourceAccess(implicit executionContext: ExecutionContext) extends CurrencyDataResourceAccess {
  implicit val system = ActorSystem(Behaviors.empty, "CurrencyConverterEngine")

  //  External Data Model & Formatters
  //  {
  //    "usd": {
  //        "code":"USD","alphaCode":"USD","numericCode":"840","name":"U.S. Dollar",
  //        "rate":1.169366900587,"date":"Tue, 28 Sep 2021 11:55:01 GMT","inverseRate":0.85516359279371
  //     },
  //     ...
  //  }
  type ExternalCurrencyDataMap = Map[String, CurrencyData]
  implicit val currencyDataFormat = jsonFormat4(CurrencyData)
  implicit val currencyDataMapFormat = mapFormat[String, CurrencyData]

  var currencyData: Option[List[CurrencyData]] = None

  def GetData(): List[CurrencyData] = {
    currencyData.get
  }

  def ParseExternalData(externalData: ExternalCurrencyDataMap): List[CurrencyData] =
    externalData.map {
      case (_, v) => v
    }.toList

  def LoadData(): Either[Throwable, _] = {
    val request = HttpRequest(uri = "https://www.floatrates.com/daily/eur.json")
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val externalCurrencyDataMapFuture: Future[ExternalCurrencyDataMap] =
      responseFuture.flatMap(res => Unmarshal(res.entity).to[ExternalCurrencyDataMap])

    Try(Await.result(externalCurrencyDataMapFuture, Duration(5, SECONDS))) match {
      case Success(value) =>
        currencyData = Some(ParseExternalData(value))
        Right()
      case Failure(e) =>
        Left(e)
    }
  }
}