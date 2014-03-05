/*
 * Copyright © 2011-2013 the spray project <http://spray.io>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.routing

import com.typesafe.config.ConfigFactory
import akka.actor.{ Actor, Props }
import spray.util._
import spray.can.Http
import spray.http.{ HttpResponse, HttpRequest }
import akka.io.IO
import akka.pattern.ask
import org.specs2.time.NoTimeConversions
import scala.concurrent.duration._
import spray.routing.directives.ProxyDirectives

class ProxyDirectivesSpec extends RoutingSpec with NoTimeConversions with ProxyDirectives {
  val testConf = ConfigFactory.parseString("""
    akka {
      event-handlers = ["akka.testkit.TestEventListener"]
      loglevel = WARN
    }""")
  val (interface, port) = Utils.temporaryServerHostnameAndPort()
  val connect = Http.Connect(interface, port)

  step {
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
    IO(Http).ask(Http.Bind(testService, interface, port))(10.seconds).await
  }

  "The 'proxy' directive" should {
    "proxy the query to the requested URI" in {
      Get() ~> proxyTo(s"http://$interface:$port/test") ~> check { responseAs[String] === "/test" }
    }

    "proxy the query to the requested URI with unmatched path" in {
      Get("/test") ~> proxyToUnmatchedPath(s"http://$interface:$port") ~> check { responseAs[String] === "/test" }
    }
  }

}
