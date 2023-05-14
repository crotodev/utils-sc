package io.github.crotodev.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuite
import java.time.{ LocalDate, LocalDateTime, ZoneId, ZonedDateTime }

class DateTimeUtilsSpec extends AnyFunSuite with Matchers {

  // We create an instance of an anonymous class that extends DateTimeUtils to test the trait's functions
  object DateTimeUtilsImpl extends DateTimeUtils

  test("parseDate should return a LocalDate for a correctly formatted date") {
    val dateStr = "2023-05-10"
    DateTimeUtilsImpl.parseDate(dateStr) shouldBe Right(LocalDate.of(2023, 5, 10))
  }

  test("parseDate should return a failure for an incorrectly formatted date") {
    val dateStr = "incorrect-date"
    DateTimeUtilsImpl.parseDate(dateStr).isLeft shouldBe true
  }

  test(
    "parseDateTime should return a LocalDateTime for a correctly formatted datetime"
  ) {
    val dateTimeStr = "2023-05-10T15:30:00.000Z"
    DateTimeUtilsImpl.parseDateTime(dateTimeStr) shouldBe Right(
      LocalDateTime.of(2023, 5, 10, 15, 30)
    )
  }

  test("parseDateTime should return a failure for an incorrectly formatted datetime") {
    val dateTimeStr = "incorrect-datetime"
    DateTimeUtilsImpl.parseDateTime(dateTimeStr).isLeft shouldBe true
  }

  test(
    "localDateTimeToZonedDateTime should convert a LocalDateTime to a ZonedDateTime in the correct timezone"
  ) {
    val dateTime = LocalDateTime.of(2023, 5, 10, 15, 30)
    val timeZone = "Asia/Kolkata"
    val zonedDateTime =
      DateTimeUtilsImpl.localDateTimeToZonedDateTime(dateTime, timeZone)
    zonedDateTime shouldBe ZonedDateTime.of(dateTime, ZoneId.of(timeZone))
  }

}
