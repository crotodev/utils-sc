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

// MetaData.scala
package io.github.crotodev.utils
package metadata

/**
 *  Case class that holds metadata for an object.
 *
 * @param id       The unique identifier of the object.
 * @param createdAt The time the object was created.
 */
case class MetaData(id: String, createdAt: Long) extends Identifiable with Timestamped

/**
 * Companion object for MetaData.
 */
object MetaData {

  /**
   *  Creates a new MetaData object with a random UUID and the current time.
   *
   * @return A new MetaData object with a random UUID and the current time.
   */
  def apply(): MetaData = MetaData(java.util.UUID.randomUUID.toString, System.currentTimeMillis)
}
