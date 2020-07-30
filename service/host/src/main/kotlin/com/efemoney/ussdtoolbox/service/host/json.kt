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

package com.efemoney.ussdtoolbox.service.host

import com.efemoney.ussdtoolbox.service.api.Field
import com.efemoney.ussdtoolbox.service.impl.BooleanFieldImpl
import com.efemoney.ussdtoolbox.service.impl.MapAsContainer
import com.efemoney.ussdtoolbox.service.impl.NumberFieldImpl
import com.efemoney.ussdtoolbox.service.impl.TextFieldImpl
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal val json = Json {

  prettyPrint = true

  prettyPrintIndent = "  "

  serializersModule = SerializersModule {

    polymorphic(Field::class) {
      subclass(BooleanFieldImpl.serializer())
      subclass(NumberFieldImpl.serializer())
      subclass(TextFieldImpl.serializer())
    }
  }
}