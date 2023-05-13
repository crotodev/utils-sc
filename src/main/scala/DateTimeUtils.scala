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

  /**
   *  Parses a string into a LocalDate.
   *
   * @param str The string to parse.
   * @return Either a String in case of a failure or a LocalDate in case of a success.
   */
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

  /**
   *  Parses a string into a LocalDateTime.
   *
   * @param str The string to parse.
   * @return Either a String in case of a failure or a LocalDateTime in case of a success.
   */
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

  /**
   *  Converts a ZonedDateTime to a LocalDateTime.
   *
   * @param zonedDateTime The ZonedDateTime to convert.
   * @return The LocalDateTime.
   */
  def zonedDateTimeToLocalDateTime(zonedDateTime: ZonedDateTime): LocalDateTime =
    zonedDateTime.toLocalDateTime

}
