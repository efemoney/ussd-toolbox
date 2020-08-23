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

package com.efemoney.ussdtoolbox.service.dsl.impl

import com.efemoney.ussdtoolbox.service.api.Country
import com.efemoney.ussdtoolbox.service.api.CountryCode
import com.efemoney.ussdtoolbox.service.api.LanguageCode

data class CountryImpl(

  override val code: CountryCode,

  override val name: String,

  override val nativeName: String,

  override val nameTranslations: Map<LanguageCode, String>,

  override val altSpellings: List<String>,

  override val languages: List<LanguageImpl>
) : Country