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

import com.efemoney.ussdtoolbox.service.ServiceScript
import com.efemoney.ussdtoolbox.service.api.Field
import com.efemoney.ussdtoolbox.service.api.Service
import com.efemoney.ussdtoolbox.service.api.TextField
import com.efemoney.ussdtoolbox.service.impl.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultValue.Error
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

fun main(args: Array<String>) = object : CliktCommand(name = "ussdService") {

  private val scriptingHost = BasicJvmScriptingHost()

  private val files by argument()
    .file(mustExist = true, canBeDir = false)
    .convert { it.toScriptSource() }
    .multiple(true)

  override fun run() = files.forEach { script ->

    val scriptTarget = ServiceScopeImpl(
      ServiceImpl(id = script.name!!)
    )

    val result = scriptingHost
      .evalWithTemplate<ServiceScript>(script) { constructorArgs(scriptTarget) }
      .valueOrThrow()
      .returnValue

    if (result is Error) handleEvalFailure(script, result) else handleEvalSuccess(result)
  }

  private fun handleEvalFailure(script: SourceCode, result: Error) {
    // report script errors together with stack trace
    println(
      "Exception while evaluating ${script.locationId?.let { "'$it'" } ?: "script"}: " +
        result.error.stackTraceToString()
    )
  }

  private fun handleEvalSuccess(result: ResultValue) {
    println(result.scriptInstance as Service)

    val service = ((result.scriptInstance as? ServiceScript)?.scope as? ServiceScopeImpl)?.service
      ?: throw PrintMessage("Something wrong happened. The script is ... not a script 😢")

    println(json.encodeToString(service))
  }

}.main(args)

