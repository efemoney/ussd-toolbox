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

package com.efemoney.ussdtoolbox.service.internal

import com.efemoney.ussdtoolbox.service.api.Res
import com.efemoney.ussdtoolbox.service.api.Text
import com.efemoney.ussdtoolbox.service.api.TextResources
import com.efemoney.ussdtoolbox.service.dsl.isValidResourceId
import com.squareup.kotlinpoet.*
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.api.ScriptDiagnostic.Severity.ERROR
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.toScriptSource

internal object HandleResAnnotations : RefineScriptCompilationConfigurationHandler {

  private val resourcesDir = File(System.getProperty("user.dir")).resolve("resources")

  override fun invoke(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val diagnostics = arrayListOf<ScriptDiagnostic>()

    val resources = context.collectedData?.get(ScriptCollectedData.foundAnnotations)
      ?.filterIsInstance<Res>()
      ?.takeIf { it.isNotEmpty() }
      ?.distinctBy(Res::id)
      ?: return context.compilationConfiguration.asSuccess(diagnostics)

    val script = (context.script as? FileBasedScriptSource)?.file
      ?: return context.compilationConfiguration.asSuccess(diagnostics)

    val resourcesFile = resourcesDir.resolve("${script.nameWithoutExtension}.res.${script.extension}")

    diagnostics += generateResourceProperties(resources, resourcesFile)

    return ScriptCompilationConfiguration(context.compilationConfiguration) {
      importScripts.append(resourcesFile.toScriptSource())
    }.asSuccess(diagnostics)
  }

  private fun generateResourceProperties(resources: List<Res>, resourcesFile: File): List<ScriptDiagnostic> {

    val diagnostics = arrayListOf<ScriptDiagnostic>()

    resources.forEach {
      if (!it.id.isValidResourceId())
        diagnostics += ScriptDiagnostic(0, "Res id \"${it.id}\" is invalid.", ERROR)
    }

    resourcesFile.writer().use { fileWriter ->
      runCatching {
        FileSpec.builder("", resourcesFile.nameWithoutExtension)
          .addComment("Do not edit!")
          .apply { resources.map(::propertySpecForResource).forEach(::addProperty) }
          .build()
          .writeTo(fileWriter)
      }.exceptionOrNull()?.asDiagnostics()?.let { diagnostics += it }
    }

    return diagnostics
  }

  private fun propertySpecForResource(res: Res): PropertySpec {

    return PropertySpec.builder(res.id, Text::class)
      .receiver(TextResources::class)
      .getter(FunSpec.getterBuilder().apply {

        val block = res.entries.map { CodeBlock.of("%S to %S", it.languageCode, it.translation) }
          .joinToCode(separator = ",\n", prefix = "mapOf(\n", suffix = "\n)")

        addCode("return %T(%S, %L)", Text.Resource::class, res.id, block)

      }.build())
      .build()
  }
}
