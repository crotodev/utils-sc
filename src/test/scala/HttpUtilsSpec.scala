package io.github.crotodev.utils

import akka.actor.ActorSystem
import akka.http.scaladsl.{ HttpExt, HttpsConnectionContext }
import akka.http.scaladsl.model._
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.unmarshalling.Unmarshaller
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.language.postfixOps

class HttpUtilsSpec
    extends AnyFlatSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterAll {

  implicit val system: ActorSystem = ActorSystem("test-system")
  implicit val um: Unmarshaller[ResponseEntity, String] =
    Unmarshaller.strict[ResponseEntity, String](_.toString)

  "HttpUtils.get" should "send a GET request and return response" in {

    val mockHttp = mock[HttpExt]
    val response = HttpResponse(entity = HttpEntity("response"))
    when(
      mockHttp.singleRequest(
        any[HttpRequest],
        any[HttpsConnectionContext],
        any[ConnectionPoolSettings]
      )
    ).thenReturn(Future.successful(response))
    val result = Await.result(
      HttpUtils.get[String]("http://test.com", timeout = 1 second)(um, system),
      1 second
    )
    result shouldBe "response"
  }

  "HttpUtils.post" should "send a POST request and return response" in {
    val mockHttp = mock[HttpExt]
    val response = HttpResponse(entity = HttpEntity("response"))
    when(
      mockHttp.singleRequest(
        any[HttpRequest],
        any[HttpsConnectionContext],
        any[ConnectionPoolSettings]
      )
    ).thenReturn(Future.successful(response))

    val result = Await.result(
      HttpUtils.post[String]("http://test.com", timeout = 1 second)(um, system),
      1 second
    )
    result shouldBe "response"
  }
}
