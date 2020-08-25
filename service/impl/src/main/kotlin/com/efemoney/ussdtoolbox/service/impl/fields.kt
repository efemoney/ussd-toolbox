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

import com.efemoney.ussdtoolbox.service.api.BooleanField
import com.efemoney.ussdtoolbox.service.api.NumberField
import com.efemoney.ussdtoolbox.service.api.TextField
import com.efemoney.ussdtoolbox.service.dsl.BooleanFieldBuilderScope
import com.efemoney.ussdtoolbox.service.dsl.NumberFieldBuilderScope
import com.efemoney.ussdtoolbox.service.dsl.TextFieldBuilderScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("text")
data class TextFieldImpl(
  override val key: String,
  override val label: String,
  override val description: String,
  override val hint: String
) : TextField()

@Serializable
@SerialName("number")
data class NumberFieldImpl(
  override val key: String,
  override val label: String,
  override val description: String,
  override val hint: String,
  val decimal: Boolean
) : NumberField()

@Serializable
@SerialName("boolean")
data class BooleanFieldImpl(
  override val key: String,
  override val label: String,
  override val description: String
) : BooleanField()

data class TextFieldBuilderScopeImpl(
  override var label: String = "",
  override var description: String = "",
  override var hint: String = ""
) : TextFieldBuilderScope() {

  override fun build(key: String): TextField = TextFieldImpl(key, label, description, hint)
}

data class NumberFieldBuilderScopeImpl(
  override var label: String = "",
  override var description: String = "",
  override var hint: String = "",
  override var decimal: Boolean = false
) : NumberFieldBuilderScope() {

  override fun build(key: String): NumberField = NumberFieldImpl(key, label, description, hint, decimal)
}

data class BooleanFieldBuilderScopeImpl(
  override var label: String = "",
  override var description: String = "",
  override var default: Boolean = false
) : BooleanFieldBuilderScope() {

  override fun build(key: String): BooleanField = BooleanFieldImpl(key, label, description)
}