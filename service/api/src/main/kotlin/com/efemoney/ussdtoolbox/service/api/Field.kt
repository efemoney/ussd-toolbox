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

interface Field<ValueT : Any> {

  val key: String

  val label: String

  val description: String

  val defaultValue: ValueT
}


interface InputField<InputT : Any> : Field<InputT> {

  val hint: String
}

interface SelectorField<ValueT : Any> : Field<ValueT> {

  val allowedValues: Set<ValueT>

  val valuesText: Set<String> get() = buildSet { allowedValues.mapTo(this, { it.toString() }) }
}


abstract class TextField : InputField<String> {

  override val defaultValue = ""

  companion object
}

abstract class NumberField : InputField<Number> {

  override val defaultValue = 0

  companion object
}

abstract class BooleanField : SelectorField<Boolean> {

  override val defaultValue = false

  override val allowedValues = setOf(false, true)

  companion object
}

abstract class EnumField<EnumT : Enum<EnumT>>(enumValues: Array<EnumT>) : SelectorField<EnumT> {

  override val defaultValue = enumValues.first()

  override val allowedValues = enumValues.toSet()

  companion object
}
