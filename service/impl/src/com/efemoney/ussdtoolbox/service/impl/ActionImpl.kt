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
@file:UseSerializers(
  CountrySerializer::class,
  FieldContainerSerializer::class,
  TemplateContainerSerializer::class,
)

package com.efemoney.ussdtoolbox.service.impl

import com.efemoney.ussdtoolbox.service.api.Action
import com.efemoney.ussdtoolbox.service.api.Country
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ActionImpl(

  override val key: String,

  override val name: String,

  override val country: Country,

  override val fields: FieldContainerImpl = FieldContainerImpl(),

  override val templates: TemplateContainerImpl = TemplateContainerImpl()

) : Action
