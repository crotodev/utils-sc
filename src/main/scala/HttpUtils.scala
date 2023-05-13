/*
 * Copyright 2023 crotodev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// HttpUtils.scala
package io.github.crotodev.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
import akka.pattern.after
import akka.stream.Materializer

import scala.concurrent.{ ExecutionContextExecutor, Future, TimeoutException }
import scala.concurrent.duration._

/**
 * Utility object for making HTTP requests and handling responses using Akka HTTP.
 */
object HttpUtils {

  /**
   * Makes a HTTP request with the specified method, url, headers, and entity. The request will be paused for the specified duration
   * and will timeout after the specified duration.
   *
   * @param method  the HTTP method to be used for the request.
   * @param url     the URL to which the request should be sent.
   * @param headers the headers to be included in the request.
   * @param entity  the entity to be sent with the request.
   * @param pause   the duration for which the request should be paused before being sent.
   * @param timeout the duration after which the request should timeout.
   * @param um      the unmarshaller to convert the response entity to type A.
   * @param system  the actor system in which the request is made.
   * @return the response entity unmarshalled to type A.
   */
  private def makeRequestWithMethod[A](
      method: HttpMethod,
      url: Uri,
      headers: List[HttpHeader],
      entity: RequestEntity = HttpEntity.Empty,
      pause: FiniteDuration,
      timeout: FiniteDuration
  )(
      implicit um: Unmarshaller[ResponseEntity, A],
      system: ActorSystem
  ): Future[A] = {

    implicit val materializer: Materializer   = Materializer(system)
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val httpRequest =
      HttpRequest(method = method, uri = url, headers = headers, entity = entity)

    val responseFuture: Future[HttpResponse] = {
      val requestWithTimeout = Future.firstCompletedOf(
        Seq(
          Http().singleRequest(httpRequest),
          after(timeout, system.scheduler)(
            Future.failed(new TimeoutException("Request timed out"))
          )
        )
      )
      after(pause, system.scheduler)(requestWithTimeout)
    }

    responseFuture.flatMap(response => Unmarshal(response.entity).to[A])
  }

  /**
   *
   * @param url    the URL to which the request should be sent.
   * @param headers the headers to be included in the request.
   * @param pause  the duration for which the request should be paused before being sent.
   * @param timeout the duration after which the request should timeout.
   * @param um    the unmarshaller to convert the response entity to type A.
   * @param system the actor system in which the request is made.
   * @tparam A the type to which the response entity should be unmarshalled.
   * @return the response entity unmarshalled to type A.
   */
  def get[A](
      url: Uri,
      headers: List[HttpHeader] = Nil,
      pause: FiniteDuration = 0.seconds,
      timeout: FiniteDuration = 5.seconds
  )(
      implicit um: Unmarshaller[ResponseEntity, A],
      system: ActorSystem
  ): Future[A] =
    makeRequestWithMethod(
      method = HttpMethods.GET,
      url = url,
      headers = headers,
      pause = pause,
      timeout = timeout
    )

  /**
   * @param url   the URL to which the request should be sent.
   * @param headers the headers to be included in the request.
   * @param pause the duration for which the request should be paused before being sent.
   * @param timeout the duration after which the request should timeout.
   * @param um  the unmarshaller to convert the response entity to type A.
   * @param system the actor system in which the request is made.
   * @tparam A the type to which the response entity should be unmarshalled.
   * @return the response entity unmarshalled to type A.
   */
  def post[A](
      url: Uri,
      headers: List[HttpHeader] = Nil,
      pause: FiniteDuration = 0.seconds,
      timeout: FiniteDuration = 5.seconds
  )(
      implicit um: Unmarshaller[ResponseEntity, A],
      system: ActorSystem
  ): Future[A] =
    makeRequestWithMethod(
      method = HttpMethods.POST,
      url = url,
      headers = headers,
      pause = pause,
      timeout = timeout
    )

  /**
   * @param url the URL to which the request should be sent.
   * @param headers the headers to be included in the request.
   * @param pause the duration for which the request should be paused before being sent.
   * @param timeout the duration after which the request should timeout.
   * @param um the unmarshaller to convert the response entity to type A.
   * @param system the actor system in which the request is made.
   * @tparam A the type to which the response entity should be unmarshalled.
   * @return the response entity unmarshalled to type A.
   */
  def put[A](
      url: Uri,
      headers: List[HttpHeader] = Nil,
      pause: FiniteDuration = 0.seconds,
      timeout: FiniteDuration = 5.seconds
  )(
      implicit um: Unmarshaller[ResponseEntity, A],
      system: ActorSystem
  ): Future[A] =
    makeRequestWithMethod(
      method = HttpMethods.PUT,
      url = url,
      headers = headers,
      pause = pause,
      timeout = timeout
    )

  /**
   * @param url     the URL to which the request should be sent.
   * @param headers the headers to be included in the request.
   * @param pause   the duration for which the request should be paused before being sent.
   * @param timeout the duration after which the request should timeout.
   * @param um      the unmarshaller to convert the response entity to type A.
   * @param system  the actor system in which the request is made.
   * @tparam A the type to which the response entity should be unmarshalled.
   * @return the response entity unmarshalled to type A.
   */
  def delete[A](
      url: Uri,
      headers: List[HttpHeader] = Nil,
      pause: FiniteDuration = 0.seconds,
      timeout: FiniteDuration = 5.seconds
  )(
      implicit um: Unmarshaller[ResponseEntity, A],
      system: ActorSystem
  ): Future[A] =
    makeRequestWithMethod(
      method = HttpMethods.DELETE,
      url = url,
      headers = headers,
      pause = pause,
      timeout = timeout
    )

  /**
   * Parses a map of HTTP headers into a list of HttpHeader.
   *
   * @param headerMap The map of HTTP headers to parse.
   * @return The list of parsed HttpHeader. If a header fails to parse, an error message is printed and the header is omitted.
   */
  def parseHeaders(
      headerMap: Map[String, String]
  ): List[HttpHeader] =
    headerMap.toList.flatMap {
      case (key, value) =>
        HttpHeader.parse(key, value) match {
          case HttpHeader.ParsingResult.Ok(parsedHeader, _) =>
            Some(parsedHeader)
          case HttpHeader.ParsingResult.Error(error) =>
            println(s"Failed to parse header '$key: $value': $error")
            None
        }
    }

  /**
   * Converts a string to a Uri.
   *
   * @param url The string to convert to a Uri.
   * @return The Uri representation of the string.
   */
  implicit def stringToUri(url: String): Uri = Uri(url)

  /**
   * Converts a Uri to a string.
   *
   * @param uri The Uri to convert to a string.
   * @return The string representation of the Uri.
   */
  implicit def uriToString(uri: Uri): String = uri.toString()

  /**
   *  Converts a map of HTTP headers to a list of HttpHeader.
   *
   * @param headerMap The map of HTTP headers to convert to a list of HttpHeader.
   * @return The list of HttpHeader.
   */
  implicit def mapToHeaders(headerMap: Map[String, String]): List[HttpHeader] =
    parseHeaders(headerMap)
}
