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

@file:Suppress("UNCHECKED_CAST")

package com.efemoney.ussdtoolbox.service.impl

import com.efemoney.ussdtoolbox.service.api.BooleanField
import com.efemoney.ussdtoolbox.service.api.Field
import com.efemoney.ussdtoolbox.service.api.NumberField
import com.efemoney.ussdtoolbox.service.api.TextField
import com.efemoney.ussdtoolbox.service.dsl.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

internal class ActionScopeImpl(internal val action: ActionImpl) : ActionScope {

  private lateinit var templateCollation: TemplateCollationScopeImpl

  override fun TextField.Companion.invoke(configure: TextFieldBuilderScope.() -> Unit) =
    TextFieldBuilderScopeImpl().also(configure)

  override fun NumberField.Companion.invoke(configure: NumberFieldBuilderScope.() -> Unit) =
    NumberFieldBuilderScopeImpl().also(configure)

  override fun BooleanField.Companion.invoke(configure: BooleanFieldBuilderScope.() -> Unit) =
    BooleanFieldBuilderScopeImpl().also(configure)

  override fun <T : Any, F : Field<T>> FieldBuilderScope<T, F>.provideDelegate(any: Any?, property: KProperty<*>): Field<T> {
    require(property !is KMutableProperty) { "You must define '${property.name}' as val and not var" }
    require(!::templateCollation.isInitialized) { "You must define property '${property.name}' before the template { ... } block" }

    return action.fields.getOrPut(property.name) { @Suppress("DEPRECATION") build(it) as Field<Any> } as Field<T>
  }

  override fun <T : Any> Field<T>.getValue(any: Any?, property: KProperty<*>): T {
    require(property !is KMutableProperty) { "You must define '${property.name}' as val and not var" }
    require(::templateCollation.isInitialized) { "You can only access '${property.name}' within the template { ... } block" }

    return templateCollation.getValue(this)
  }

  override fun template(runner: TemplateCollationScope.() -> String) {
    templateCollation = TemplateCollationScopeImpl(action)
    templateCollation.run(runner)
  }
}