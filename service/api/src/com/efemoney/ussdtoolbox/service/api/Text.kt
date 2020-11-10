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

package com.efemoney.ussdtoolbox.service.api

sealed class Text {

  abstract operator fun get(lang: LanguageCode): kotlin.String

  data class String(val value: kotlin.String) : Text() {

    override fun get(lang: LanguageCode) = value
  }

  data class Resource(val key: kotlin.String, val mappings: Map<LanguageCode, kotlin.String>) : Text() {

    override fun get(lang: LanguageCode) = mappings[lang] ?: mappings["en"] ?: mappings.values.firstOrNull() ?: ""
  }
}