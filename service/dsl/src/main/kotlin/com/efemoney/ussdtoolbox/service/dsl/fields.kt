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

package com.efemoney.ussdtoolbox.service.dsl

import com.efemoney.ussdtoolbox.service.api.*
import com.efemoney.ussdtoolbox.service.dsl.marker.ServiceDsl

interface FieldBuilderScope<T, F : Field<T>> {

  var label: String

  var description: String

  fun build(key: String): F
}

@ServiceDsl
interface InputFieldBuilderScope<T, IF : InputField<T>> : FieldBuilderScope<T, IF> {

  var hint: String
}

@ServiceDsl
interface SelectorFieldBuilderScope<T, SF : SelectorField<T>> : FieldBuilderScope<T, SF> {

  var default: T
}

@ServiceDsl
abstract class TextFieldBuilderScope : InputFieldBuilderScope<String, TextField>

@ServiceDsl
abstract class NumberFieldBuilderScope : InputFieldBuilderScope<Number, NumberField> {

  abstract var decimal: Boolean
}

@ServiceDsl
abstract class BooleanFieldBuilderScope : SelectorFieldBuilderScope<Boolean, BooleanField>