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

import com.efemoney.ussdtoolbox.service.api.*
import com.efemoney.ussdtoolbox.service.dsl.TemplateCollationScope
import java.util.*

internal class TemplateCollationScopeImpl(private val action: ActionImpl) : TemplateCollationScope() {

  private var run: CollationRun? = null

  internal fun run(runner: TemplateCollationScope.() -> String) {
    val selectors = action.fields.values.asSequence().filterIsInstance<SelectorField<Any>>()

    val selectorFields = selectors.toSet()
    val selectorProduct = selectors.map { it.allowedValues }.toSet().toCartesianProduct()

    for (values in selectorProduct) {

      require(values.count() == selectorFields.count()) {
        "Unknown field added to Action('${action.key}'). Fields must be of type Input or Selector"
      }

      run = CollationRun(selectorFields.zip(values).toMap())
      val result = runner()
      action.templates.getOrPut(result.uuid()) { TemplateImpl(it, result, run!!.involvedFields.toSet()) }
      run = null
    }
  }

  public override fun <T : Any> getValue(field: Field<T>): T {
    require(run != null) { "Collation run happening in illegal state" }
    run!!.run {
      return when (field) {
        is SelectorField -> (fieldValues.getValue(field) as T).also { involvedFields += "${field.key}($it)" }

        is InputField -> field.resolutionValue().also { involvedFields += field.key }

        else -> error("Unknown field type: ${field::class}")
      } as T
    }
  }
}

private class CollationRun(val fieldValues: Map<SelectorField<*>, Any>) {
  val involvedFields = mutableSetOf<String>()
}

private fun String.uuid() = UUID.nameUUIDFromBytes(toByteArray()).toString()

/* https://gist.github.com/erikhuizinga/d2ca2b501864df219fd7f25e4dd000a4 */
private fun Iterable<Set<Any>>.toCartesianProduct() = fold(listOf(listOf<Any>())) { accumulated, set ->
  accumulated.flatMap { list ->
    set.map { list + it }
  }
}

private fun <T : Any> InputField<T>.resolutionValue(): Any = when (this) {
  is NumberField -> WrapNumber(key)
  is TextField -> WrapString(key)
  else -> error("Do not call resolutionValue for non Input fields")
}

private fun WrapString(string: String) = "<$string>"

private class WrapNumber(private val string: String) : Number() {
  override fun toByte(): Byte = error("Do not call")
  override fun toChar(): Char = error("Do not call")
  override fun toDouble(): Double = error("Do not call")
  override fun toFloat(): Float = error("Do not call")
  override fun toInt(): Int = error("Do not call")
  override fun toLong(): Long = error("Do not call")
  override fun toShort(): Short = error("Do not call")
  override fun toString(): String = WrapString(string)
}
