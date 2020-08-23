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
import com.efemoney.ussdtoolbox.service.api.Service
import com.efemoney.ussdtoolbox.service.dsl.ActionContainerScope
import com.efemoney.ussdtoolbox.service.dsl.ServiceProviderScope
import com.efemoney.ussdtoolbox.service.dsl.ServiceScope

class ServiceScopeImpl(val service: ServiceImpl) : ServiceScope, Service by service {

  lateinit var country: Country

  override fun provider(configure: ServiceProviderScope.() -> Unit) =
    configure(ServiceProviderScopeImpl(this))

  override fun actions(configure: ActionContainerScope.() -> Unit) =
    configure(ActionContainerScopeImpl(service.actions, country))

  override fun toString() = "ServiceScope($service)"
}

internal class ServiceProviderScopeImpl(private val delegate: ServiceScopeImpl) : ServiceProviderScope {

  override var name: String
    get() = delegate.name
    set(value) {
      delegate.name = value
    }

  override var logoUrl: String?
    get() = delegate.logoUrl
    set(value) {
      delegate.logoUrl = value
    }

  override var logoHasText: Boolean
    get() = delegate.logoHasText
    set(value) { delegate.logoHasText = value }

  override var brandColor: String
    get() = delegate.brandColor.toColorString()
    set(value) { delegate.brandColor = value.toColor()}

  override var brandAccentColor: String
    get() = delegate.brandAccentColor.toColorString()
    set(value) { delegate.brandAccentColor = value.toColor() }

  override var country: Country
    get() = delegate.country
    set(value) { delegate.country = value }
}
