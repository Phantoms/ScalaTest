import scala.util.{Failure, Success, Try}

trait AmountUtils {
  def formatAmount(amount: Double): Double = {
    f"${amount}%1.2f".toDouble
  }

  def parseAmount(stringAmount: String): Either[String, Double] = {
    Try(stringAmount.toDouble) match {
      case Success(amount) => amount match {
        case v if v > 0 => Right(v)
        case _ => Left("Amount must be positive number")
      }
      case Failure(_) =>
        Left("Incorrect amount, must be a number")
    }
  }
}

