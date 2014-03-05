package docs.directives

import akka.actor.{Actor, Props}
import spray.can.Http
import spray.http.{HttpResponse, HttpRequest}
import akka.pattern.ask
import akka.io.IO
import spray.util._
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._
import spray.routing.directives.ProxyDirectives

/**
 *
 * User: benjaminthuillier
 * Date: 17/01/14
 * Time: 21:48
 *
 */
class ProxyDirectivesExamplesSpec extends DirectivesSpec with NoTimeConversions with ProxyDirectives {
  val (interface, port) = Utils.temporaryServerHostnameAndPort()
  val connect = Http.Connect(interface, port)

  "example-1" in {

    //a custom http service
    val testService = system.actorOf {
      Props {
        new Actor {
          def receive = {
            case x: Http.Connected        ⇒ sender ! Http.Register(self)
            case x: HttpRequest           ⇒ sender ! HttpResponse(entity = x.uri.path.toString)
            case _: Http.ConnectionClosed ⇒ // ignore
          }
        }
      }
    }
    IO(Http).ask(Http.Bind(testService, interface, port))(3.seconds).await

    // proxy the request to the custom http service
    Get() ~> proxyTo(s"http://$interface:$port/test") ~> check { responseAs[String] === "/test" }
  }

  "example-2" in {

    //a custom http service
    val testService = system.actorOf {
      Props {
        new Actor {
          def receive = {
            case x: Http.Connected        ⇒ sender ! Http.Register(self)
            case x: HttpRequest           ⇒ sender ! HttpResponse(entity = x.uri.path.toString)
            case _: Http.ConnectionClosed ⇒ // ignore
          }
        }
      }
    }
    IO(Http).ask(Http.Bind(testService, interface, port))(3.seconds).await

    // proxy the request to the custom http service with path completed by the unmatched path
    Get("/test") ~> proxyToUnmatchedPath(s"http://$interface:$port") ~> check { responseAs[String] === "/test" }
  }
}
