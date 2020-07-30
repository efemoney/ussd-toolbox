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

package com.efemoney.ussdtoolbox.service

import com.efemoney.ussdtoolbox.service.api.Res
import com.efemoney.ussdtoolbox.service.dsl.ServiceScope
import com.efemoney.ussdtoolbox.service.internal.HandleResAnnotations
import java.lang.Thread.currentThread
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader

object ServiceScriptCompilationConfig : ScriptCompilationConfiguration({

  jvm {
    updateClasspath(
      classpathFromClassloader(currentThread().contextClassLoader)?.filter {
        "/service/(def|api|dsl|impl)/".toRegex() in it.path ||
          "service-(def|api|dsl|impl)".toRegex() in it.name
      }
    )
  }

  ide {
    acceptedLocations.append(ScriptAcceptedLocation.Everywhere)
  }

  defaultImports(
    "com.efemoney.ussdtoolbox.service.api.*",
    "com.efemoney.ussdtoolbox.service.dsl.*"
  )

  refineConfiguration {
    onAnnotations<Res>(HandleResAnnotations)
  }
})

object ServiceScriptEvaluationConfig : ScriptEvaluationConfiguration({
  enableScriptsInstancesSharing()

  refineConfigurationBeforeEvaluate {
    it.evaluationConfiguration.asSuccess()
  }
})

@KotlinScript(
  displayName = "Ussd Definition Script",
  fileExtension = "ussd",
  compilationConfiguration = ServiceScriptCompilationConfig::class,
  evaluationConfiguration = ServiceScriptEvaluationConfig::class
)
abstract class ServiceScript(val scope: ServiceScope) : ServiceScope by scope {

  override fun toString() = "ServiceScript($scope)"
}