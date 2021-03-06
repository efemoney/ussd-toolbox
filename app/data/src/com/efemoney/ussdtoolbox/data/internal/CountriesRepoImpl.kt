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

package com.efemoney.ussdtoolbox.data.internal

import com.efemoney.ussdtoolbox.data.CountriesRepo
import com.efemoney.ussdtoolbox.service.api.Country
import javax.inject.Inject

internal class CountriesRepoImpl @Inject constructor() : CountriesRepo {

  override suspend fun selectCountry(selected: Country) {
    TODO()
  }

  override suspend fun selectedCountry(): Country {
    TODO()
  }

  override suspend fun countries(): List<Country> {
    TODO()
  }
}