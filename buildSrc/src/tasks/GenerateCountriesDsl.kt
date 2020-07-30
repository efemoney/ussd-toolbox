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

@file:Suppress("NAME_SHADOWING", "UNCHECKED_CAST", "UnstableApiUsage", "HasPlatformType")

package tasks

import com.squareup.kotlinpoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import tasks.internal.CountriesJsonParser
import tasks.internal.generatedBy
import tasks.internal.generatedFilesSuppressAnnotation
import javax.inject.Inject

open class GenerateCountriesDsl @Inject constructor(objects: ObjectFactory) : DefaultTask() {

  private val generatedFileComment = generatedBy("GenerateCountriesDsl")
  private val generatedFileSuppressAnnotation = generatedFilesSuppressAnnotation()

  @get:InputFile
  var countriesJsonFile by objects.fileProperty()

  @get:Input
  var outputPackage by objects.property<String>()

  @get:OutputDirectory
  var outputDir by objects.directoryProperty()

  @TaskAction
  internal fun generateCountriesDsl() {

    val countries = CountriesJsonParser.parse(countriesJsonFile.asFile)

    val countryClassName = ClassName("com.efemoney.ussdtoolbox.service.api", "Country")
    val countriesClassName = ClassName("com.efemoney.ussdtoolbox.service.api", "Countries")

    FileSpec.builder(outputPackage, "countriesDsl")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnnotation)
      .apply {
        countries.map {
          PropertySpec.builder(it.name.replace(".", ""), countryClassName)
            .getter(
              FunSpec.getterBuilder()
                .addModifiers(KModifier.INLINE)
                .addCode("return %T.%L", countriesClassName, it.alpha2Code)
                .build()
            )
            .build()
        }.forEach { addProperty(it) }
      }
      .build()
      .writeTo(outputDir.asFile)
  }
}