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

package tasks.countries

typealias LanguageCode = String

typealias CountryCode = String

interface Language {

  val code: LanguageCode

  val name: String

  val nativeName: String
}

interface Country {

  val code: CountryCode

  val name: String

  val nativeName: String

  val nameTranslations: Map<LanguageCode, String>

  val altSpellings: List<String>

  val languages: List<Language>
}
