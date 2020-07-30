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

import com.efemoney.ussdtoolbox.service.api.Countries
import com.efemoney.ussdtoolbox.service.api.Country
import com.efemoney.ussdtoolbox.service.api.Service
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ServiceImpl(

  override val id: String,

  override var name: String = id.capitalize(Locale.getDefault()),

  override var logoUrl: String? = null,

  override var logoHasText: Boolean = false,

  override var brandColor: Int = "#FFFFFF".toColor(),

  override var brandAccentColor: Int = "#3F51B5".toColor(),

  override var country: Country = Countries.findByCountryCode(Locale.getDefault().country) ?: Countries.NG,

  override var actions: ActionContainerImpl = ActionContainerImpl(),
) : Service