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

import com.efemoney.ussdtoolbox.service.api.Country
import com.efemoney.ussdtoolbox.service.api.Field
import com.efemoney.ussdtoolbox.service.api.Keyed
import com.efemoney.ussdtoolbox.service.dsl.Countries
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CountrySerializer : KSerializer<Country> {
  override val descriptor = PrimitiveSerialDescriptor("Country", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: Country) = encoder.encodeString(value.code)
  override fun deserialize(decoder: Decoder): Country = Countries.findByCountryCode(decoder.decodeString())!!
}

object ColorSerializer : KSerializer<Int> {
  override val descriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)
  override fun serialize(encoder: Encoder, value: Int) = encoder.encodeString(value.toColorString())
  override fun deserialize(decoder: Decoder) = decoder.decodeString().toColor()
}

abstract class ContainerSerializer<T : Keyed, C : MapBackedContainer<T>>(
  elementTypeSerializer: KSerializer<T>,
  private val createContainer: () -> C
) : KSerializer<C> {

  private val listSerializer = ListSerializer(elementTypeSerializer)

  override val descriptor = listSerializer.descriptor

  override fun serialize(encoder: Encoder, value: C) =
    listSerializer.serialize(encoder, value.values.toList())

  override fun deserialize(decoder: Decoder) = createContainer().apply {
    listSerializer.deserialize(decoder).forEach {
      put(it.key, it)
    }
  }
}

object ActionContainerSerializer :
  ContainerSerializer<ActionImpl, ActionContainerImpl>(ActionImpl.serializer(), ::ActionContainerImpl)

object FieldContainerSerializer :
  ContainerSerializer<Field<Any>, FieldContainerImpl>(PolymorphicSerializer(Field::class).cast(), ::FieldContainerImpl)

object TemplateContainerSerializer :
  ContainerSerializer<TemplateImpl, TemplateContainerImpl>(TemplateImpl.serializer(), ::TemplateContainerImpl)