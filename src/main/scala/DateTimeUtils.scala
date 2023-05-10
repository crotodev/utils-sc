/*
 * Copyright 2023 Christian Rotondo
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

// DateTimeUtils.scala
package io.github.crotodev.utils

import com.joestelmach.natty.{ Parser => NattyParser }

import java.time._
import java.time.format.DateTimeFormatter
import scala.collection.JavaConverters._
import scala.util.Try

/**
 * A trait representing utilities for handling Date and Time.
 */
trait DateTimeUtils {

  protected val defaultStartDate: LocalDate = LocalDate.now().minusYears(5)

  protected val defaultStartDateTime: LocalDateTime =
    LocalDateTime.now().minusYears(5)

  private val nattyParser = new NattyParser()

  private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  /**
   * Parses a string into a LocalDate.
   *
   * @param str The string to parse.
   * @return Either a String in case of a failure or a LocalDate in case of a success.
   */
  def parseDate(str: String): Either[String, LocalDate] =
    Try(LocalDate.parse(str, dateFormatter)).toEither.left
      .flatMap(_ => parseDateWithNatty(str))

  /**
   * Parses a string into a LocalDateTime.
   *
   * @param str The string to parse.
   * @return Either a String in case of a failure or a LocalDateTime in case of a success.
   */
  def parseDateTime(str: String): Either[String, LocalDateTime] =
    Try(LocalDateTime.parse(str, dateTimeFormatter)).toEither.left
      .flatMap(_ => parseDateTimeWithNatty(str))

  private def parseDateWithNatty(str: String): Either[String, LocalDate] = {
    val dateGroups = nattyParser.parse(str).asScala
    if (dateGroups.isEmpty) {
      Left(s"Cannot parse date from the input string: $str")
    } else {
      val dates = dateGroups.head.getDates.asScala
      if (dates.isEmpty) {
        Left(s"Cannot parse date from the input string: $str")
      } else {
        val localDateTime = LocalDateTime
          .from(dates.head.toInstant.atZone(java.time.ZoneId.systemDefault()))
        Right(localDateTime.toLocalDate)
      }
    }
  }

  private def parseDateTimeWithNatty(str: String): Either[String, LocalDateTime] = {
    val dateGroups = nattyParser.parse(str).asScala
    if (dateGroups.isEmpty) {
      Left(s"Cannot parse date-time from the input string: $str")
    } else {
      val dates = dateGroups.head.getDates.asScala
      if (dates.isEmpty) {
        Left(s"Cannot parse date-time from the input string: $str")
      } else {
        Right(
          LocalDateTime
            .from(dates.head.toInstant.atZone(java.time.ZoneId.systemDefault()))
        )
      }
    }
  }

  /**
   * Converts a LocalDateTime to a timestamp.
   *
   * @param dateTime The LocalDateTime to convert.
   * @return The timestamp.
   */
  def localDateTimeToTimestamp(dateTime: LocalDateTime): Long =
    dateTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli

  /**
   * Converts a LocalDateTime to a UTC timestamp.
   *
   * @param dateTime The LocalDateTime to convert.
   * @return The UTC timestamp.
   */
  def localDateTimeToUTCTimestamp(dateTime: LocalDateTime): Long =
    dateTime
      .atZone(ZoneId.systemDefault())
      .withZoneSameInstant(ZoneId.of("UTC"))
      .toInstant
      .toEpochMilli

  /**
   * Converts a UTC timestamp to a LocalDateTime.
   *
   * @param timestamp The timestamp to convert.
   * @return The LocalDateTime.
   */
  def utcTimestampToLocalDateTime(timestamp: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"))

  /**
   * Converts a LocalDateTime to a ZonedDateTime.
   *
   * @param dateTime The LocalDateTime to convert.
   * @param timeZone The time zone to use.
   * @return The ZonedDateTime.
   */
  def localDateTimeToZonedDateTime(
      dateTime: LocalDateTime,
      timeZone: String
  ): ZonedDateTime =
    ZonedDateTime.of(dateTime, ZoneId.of(timeZone))

  def zonedDateTimeToLocalDateTime(zonedDateTime: ZonedDateTime): LocalDateTime =
    zonedDateTime.toLocalDateTime

  /**
   * Sanitizes a LocalDate. If the given date is None, returns a date 5 years ago from now.
   *
   * @param date The date to sanitize.
   * @return The sanitized date.
   */
  def sanitizeDate(date: Option[LocalDate]): LocalDate =
    date match {
      case Some(date) => date
      case None       => LocalDate.now().minusYears(5)
    }

  /**
   * Sanitizes start and end dates. If the given dates are None, returns appropriate default dates.
   *
   * @param startDate The start date to sanitize.
   * @param endDate   The end date to sanitize.
   * @return A tuple of sanitized start and end dates.
   */
  def sanitizeDates(
      startDate: Option[LocalDate],
      endDate: Option[LocalDate]
  ): (LocalDate, LocalDate) =
    (startDate.getOrElse(defaultStartDate), sanitizeDate(endDate))

  /**
   * Sanitizes a LocalDateTime. If the given date is None, returns a date-time 5 years ago from now.
   *
   * @param date The date-time to sanitize.
   * @return The sanitized date-time.
   */
  def sanitizeDateTime(date: Option[LocalDateTime]): LocalDateTime =
    date match {
      case Some(date) => date
      case None       => LocalDateTime.now().minusYears(5)
    }

  /**
   * Sanitizes a LocalDateTime. If the given date is not parseable, returns a date-time 5 years ago from now.
   *
   * @param date The date-time string to sanitize.
   * @return The sanitized date-time.
   */
  def sanitizeDateTime(date: String): LocalDateTime =
    parseDateTime(date) match {
      case Right(date) => date
      case Left(_)     => LocalDateTime.now().minusYears(5)
    }

  /**
   * Sanitizes start and end date-times. If the given date-times are None, returns appropriate default date-times.
   *
   * @param startDateTime The start date-time to sanitize.
   * @param endDateTime  The end date-time to sanitize.
   * @return
   */
  def sanitizeDateTimes(
      startDateTime: Option[LocalDateTime],
      endDateTime: Option[LocalDateTime]
  ): (LocalDateTime, LocalDateTime) =
    (
      startDateTime.getOrElse(defaultStartDateTime),
      sanitizeDateTime(endDateTime)
    )
}
