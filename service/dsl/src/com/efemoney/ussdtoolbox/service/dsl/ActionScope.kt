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

import com.efemoney.ussdtoolbox.service.api.BooleanField
import com.efemoney.ussdtoolbox.service.api.Field
import com.efemoney.ussdtoolbox.service.api.NumberField
import com.efemoney.ussdtoolbox.service.api.TextField
import com.efemoney.ussdtoolbox.service.dsl.marker.ServiceDsl
import kotlin.reflect.KProperty

@ServiceDsl
interface ActionScope {

  @ServiceDsl
  operator fun TextField.Companion.invoke(configure: TextFieldBuilderScope.() -> Unit): TextFieldBuilderScope

  @ServiceDsl
  operator fun NumberField.Companion.invoke(configure: NumberFieldBuilderScope.() -> Unit): NumberFieldBuilderScope

  @ServiceDsl
  operator fun BooleanField.Companion.invoke(configure: BooleanFieldBuilderScope.() -> Unit): BooleanFieldBuilderScope

  @ServiceDsl
  operator fun <T : Any, F : Field<T>> FieldBuilderScope<T, F>.provideDelegate(
    any: Any?,
    property: KProperty<*>
  ): Field<T>

  @ServiceDsl
  operator fun <T : Any> Field<T>.getValue(any: Any?, property: KProperty<*>): T

  @ServiceDsl
  fun template(runner: TemplateCollationScope.() -> String)
}
