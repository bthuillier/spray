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
package directives

import spray.can.Http
import akka.io.IO
import akka.actor.ActorSystem
import spray.http.{ HttpRequest, Uri }

trait ProxyDirectives {

  private def sending(f: RequestContext ⇒ HttpRequest)(implicit system: ActorSystem): Route = {
    val transport = IO(Http)(system)
    ctx ⇒ transport.tell(f(ctx), ctx.responder)
  }

  /**
   * proxy the request to the specified uri
   *
   */
  def proxyTo(uri: Uri)(implicit system: ActorSystem): Route = {
    sending(_.request.copy(
	  uri = uri,
      headers = ctx.request.headers.filterNot(header => header.is("host"))))
  }

  /**
   * proxy the request to the specified uri with the unmatched path
   *
   */
  def proxyToUnmatchedPath(uri: Uri)(implicit system: ActorSystem): Route = {
    sending(ctx ⇒ ctx.request.copy(
	  uri = uri.withPath(uri.path.++(ctx.unmatchedPath)),
      headers = ctx.request.headers.filterNot(header => header.is("host"))))
  }
}

object ProxyDirectives extends ProxyDirectives
