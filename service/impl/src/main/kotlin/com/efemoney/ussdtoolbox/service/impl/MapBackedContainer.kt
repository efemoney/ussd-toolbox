/*
 * Copyright 2020 Efeturi Money. All rights reserved.
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

package com.efemoney.ussdtoolbox.service.impl

import com.efemoney.ussdtoolbox.service.api.Container
import kotlinx.serialization.Serializable

@Serializable
abstract class MapBackedContainer<Key, Thing : Any>(
  protected val storage: MutableMap<Key, Thing> = mutableMapOf()
) : MutableMap<Key, Thing> by storage, Container<Key, Thing> {

  override fun get(key: Key): Thing = storage.getValue(key)

  override fun getOrPut(key: Key, orDefault: (Key) -> @UnsafeVariance Thing) =
    storage.getOrPut(key) { orDefault(key) }

  override fun iterator(): Iterator<Thing> = storage.values.iterator()

  override fun hashCode() = storage.hashCode()

  override fun toString() = "MapBackedContainer(storage=$storage)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MapBackedContainer<*, *>) return false
    return storage == other.storage
  }
}