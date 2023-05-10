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

// HasMetaData.scala
package io.github.crotodev.utils

import java.util.UUID

/**
 * A trait representing an entity with metadata.
 * It aggregates several other traits, each representing a different aspect of the metadata.
 */
trait HasMetaData
    extends HasId
    with HasTags
    with HasAuthor
    with HasModifiedAt
    with HasTitle
    with HasCreatedAt {

  /**
   * Unique identifier for the entity.
   * Generated as the least significant bits of a random UUID.
   */
  val _id: Long = UUID.randomUUID().getLeastSignificantBits & Long.MaxValue

  /**
   * Timestamp of when the entity was created.
   * Generated as the current system time in milliseconds.
   */
  val _createdAt: Long = System.currentTimeMillis()

  /**
   * Tags associated with the entity.
   * Default is an empty Set.
   */
  val _tags: Set[String] = Set.empty

  /**
   * Title of the entity.
   * Default is None.
   */
  val _title: Option[String] = None

  /**
   * Author of the entity.
   * Default is None.
   */
  val _author: Option[String] = None

  /**
   * Timestamp of when the entity was last modified.
   * Default is None.
   */
  val _modifiedAt: Option[Long] = None
}

/**
 * Trait representing an entity that has an identifier.
 */
trait HasId {
  val _id: Long
}

/**
 * Trait representing an entity that has a creation timestamp.
 */
trait HasCreatedAt {

  /**
   * Timestamp of when the entity was created.
   */
  val _createdAt: Long
}

/**
 * Trait representing an entity that has an author.
 */
trait HasAuthor {

  /**
   * Author of the entity.
   */
  val _author: Option[String]
}

/**
 * Trait representing an entity that has a modification timestamp.
 */
trait HasModifiedAt {

  /**
   * Timestamp of when the entity was last modified.
   */
  val _modifiedAt: Option[Long]
}

/**
 * Trait representing an entity that has tags.
 */
trait HasTags {

  /**
   * Tags associated with the entity.
   */
  val _tags: Set[String]
}

/**
 * Trait representing an entity that has a title.
 */
trait HasTitle {

  /**
   * Title of the entity.
   */
  val _title: Option[String]
}
