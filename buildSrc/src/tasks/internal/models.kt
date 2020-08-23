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

package tasks.internal

import kotlinx.serialization.Serializable

@Serializable
internal data class CountryDto(
  val name: String,
  val nativeName: String,
  val alpha2Code: String,
  val altSpellings: List<String>,
  val translations: Map<String, String>,
  val languages: List<LanguageDto>
)

@Serializable
internal data class LanguageDto(
  val name: String,
  val nativeName: String,
  val iso639_1: String = "",
  val iso639_2: String = ""
)