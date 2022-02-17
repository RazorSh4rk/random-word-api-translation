import scalaj.http.Http
import play.api.libs.json.Json
import scala.io.Source
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import java.io._

case class Body (
  val q: String,
  val source: String,
  val target: String,
  val format: String
)

case class Response (
  val translatedText: String
)

object Main extends App {
  implicit val bodyWrites = Json.writes[Body]
  implicit val responseReads = Json.reads[Response]

  val tasks: ListBuffer[Future[(String, String)]] = ListBuffer()
  val translated: ListBuffer[String] = ListBuffer()

  val toLang = if(args.length > 0) args(0) else "es"

  lazy val words = Json.parse(Source.fromFile("words.json")
    .getLines().mkString).as[List[String]]

  words.foreach(word => {
    val task: Future[(String, String)] = Future {
      val payload = Body(
        q = word,
        source = "en",
        target = toLang,
        format = "text"
      )
      val resp = Http("http://0.0.0.0:5000/translate")
        .postData(Json.toJson(payload).toString)
        .header("content-type", "application/json")
      
      val response = Json
        .fromJson(
          Json.parse(
            resp.asString.body.toString
          )
        ).get

        (word, response.translatedText)
    }

    tasks += task

    task.onComplete({
      case Success(value) => {
        if(value._1 != value._2) {
          translated += value._2
          println(value, translated.length)
        }
      }
      case Failure(exception) => println(exception)
    })
  })

  new Thread(() => {
    while(true){
      Thread.sleep(10000)
      println(s"saving ${translated.length}...")
      val pw = new PrintWriter(new File(s"$toLang.json"))
      pw.write(Json.toJson(translated.toList).toString)
      pw.close
    }
  }).run
}