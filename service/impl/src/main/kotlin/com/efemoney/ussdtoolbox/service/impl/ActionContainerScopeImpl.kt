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
import com.efemoney.ussdtoolbox.service.dsl.ActionContainerScope
import com.efemoney.ussdtoolbox.service.dsl.ActionScope
import com.efemoney.ussdtoolbox.service.dsl.toActionKey

class ActionContainerScopeImpl(
  private val actions: ActionContainerImpl,
  override var country: Country
) : ActionContainerScope {

  override fun String.invoke(configure: ActionScope.() -> Unit) {
    val key = this.toActionKey()
    val action = actions.getOrPut(key) { ActionImpl(key, this, country) }
    ActionScopeImpl(action).configure()
  }
}