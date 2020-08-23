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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmStatic
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import tasks.internal.CountriesJsonParser
import tasks.internal.CountryDto
import tasks.internal.LanguageDto
import javax.inject.Inject

open class GenerateCountriesDsl @Inject constructor(objects: ObjectFactory) : DefaultTask() {

  @InputFile
  val countriesJsonFile = objects.fileProperty()

  @OutputDirectory
  val outputDir = objects.directoryProperty()

  @TaskAction
  internal fun generateCountriesDsl() {

    val apiPkg = "com.efemoney.ussdtoolbox.service.api"
    val dslPkg = "com.efemoney.ussdtoolbox.service.dsl"
    val implPkg = "com.efemoney.ussdtoolbox.service.dsl.impl"
    val classNames = ClassNames(apiPkg, dslPkg, implPkg)

    val generatedFileComment = generatedBy("GenerateCountriesDsl")
    val generatedFileSuppressAnn = generatedFilesSuppressAnnotation()

    val countries = CountriesJsonParser.parse(countriesJsonFile.asFile.get())
      .sortedBy(CountryDto::alpha2Code)

    val languages = countries.flatMap(CountryDto::languages).filter { it.iso639_1.isNotBlank() }
      .toSet().sortedBy(LanguageDto::iso639_1)

    val outDir = outputDir.asFile.get()

    FileSpec.builder(dslPkg, "Languages")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnn)
      .addType(languageImplSpec(languages, classNames))
      .build()
      .writeTo(outDir)

    FileSpec.builder(dslPkg, "Countries")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnn)
      .addType(countryImplSpec(countries, classNames))
      .build()
      .writeTo(outDir)

    FileSpec.builder(dslPkg, "countriesDsl")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnn)
      .apply {
        countries.mapWith {
          PropertySpec.builder(name.replace(".", ""), classNames.countryApi)
            .getter(
              FunSpec.getterBuilder()
                .addModifiers(KModifier.INLINE)
                .addCode("return %T.%L", ClassName(dslPkg, "Countries"), alpha2Code)
                .build()
            )
            .build()
        }.forEach { addProperty(it) }
      }.build()
      .writeTo(outDir)
  }

  private fun languageImplSpec(languages: List<LanguageDto>, classNames: ClassNames): TypeSpec {
    return TypeSpec.objectBuilder("Languages")
      .addProperties(
        languages.mapWith {
          PropertySpec.builder(iso639_1, classNames.languageImpl)
            .initializer(
              "%T(code = %S, name = %S, nativeName = %S)",
              classNames.languageImpl,
              iso639_1,
              name,
              nativeName
            )
            .build()
        }
      )
      .addProperty(
        PropertySpec.builder("all", MAP.parameterizedBy(classNames.languageCode, classNames.languageApi))
          .initializer(
            languages
              .mapWith { CodeBlock.of("%S·to·%N", iso639_1, iso639_1) }
              .joinToCode(separator = ", ", prefix = "mapOf(\n", suffix = "\n)")
          )
          .build()
      )
      .addFunction(
        FunSpec.builder("findByLanguageCode")
          .addAnnotation(JvmStatic::class)
          .addParameter("languageCode", classNames.languageCode)
          .returns(classNames.languageApi.copy(nullable = true))
          .addCode("return all[languageCode]")
          .build()
      )
      .build()
  }

  private fun countryImplSpec(countries: List<CountryDto>, classNames: ClassNames): TypeSpec {
    return TypeSpec.objectBuilder("Countries")
      .addProperties(
        countries.mapWith {
          PropertySpec.builder(alpha2Code, classNames.countryApi)
            .initializer(
              """%T(
                |  code = %S,
                |  name = %S, 
                |  nativeName = %S, 
                |  nameTranslations = %L,
                |  altSpellings = %L,
                |  languages = %L
                |)""".trimMargin(),
              classNames.countryImpl,
              alpha2Code,
              name,
              nativeName,
              translations
                .filter { it.value != name } // remove translations that match the name
                .map { CodeBlock.of("%S·to·%S", it.key, it.value) }
                .filter(CodeBlock::isNotEmpty)
                .joinToCode(",\n", prefix = "mapOf(", suffix = ")"),
              altSpellings
                .map { CodeBlock.of("%S", it) }
                .filter(CodeBlock::isNotEmpty)
                .joinToCode(",\n", prefix = "listOf(", suffix = ")"),
              languages
                .filter { it.iso639_1.isNotBlank() }
                .mapWith { CodeBlock.of("%T.%N", classNames.languagesDsl, iso639_1) }
                .filter(CodeBlock::isNotEmpty)
                .joinToCode(",\n", prefix = "listOf(", suffix = ")")
            )
            .build()
        }
      )
      .addProperty(
        PropertySpec.builder("all", MAP.parameterizedBy(classNames.countryCode, classNames.countryApi))
          .initializer(
            countries
              .mapWith { CodeBlock.of("%S·to·%N", alpha2Code, alpha2Code) }
              .joinToCode(separator = ", ", prefix = "mapOf(\n", suffix = "\n)")
          )
          .build()
      )
      .addFunction(
        FunSpec.builder("findByCountryCode")
          .addParameter("countryCode", classNames.countryCode)
          .returns(classNames.countryApi.copy(nullable = true))
          .addCode("return all[countryCode]")
          .jvmStatic()
          .build()
      )
      .build()
  }
}

internal class ClassNames(apiPkg: String, dslPkg: String, dslImplPkg: String) {
  val countryCode = ClassName(apiPkg, "CountryCode")
  val languageCode = ClassName(apiPkg, "LanguageCode")
  val countryApi = ClassName(apiPkg, "Country")
  val languageApi = ClassName(apiPkg, "Language")
  val countryImpl = ClassName(dslImplPkg, "CountryImpl")
  val languageImpl = ClassName(dslImplPkg, "LanguageImpl")
  val languagesDsl = ClassName(dslPkg, "Languages")
}

internal fun generatedBy(taskName: String) = "Do not edit. Code generated by $taskName task as part of build."

internal fun generatedFilesSuppressAnnotation() =
  AnnotationSpec.builder(Suppress::class).useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
    .addMember(
      "%S, %S, %S, %S, %S",
      "EnumEntryName",
      "NonAsciiCharacters",
      "SpellCheckingInspection",
      "RemoveRedundantBackticks",
      "MemberVisibilityCanBePrivate"
    )
    .build()

internal inline fun <T, R> Iterable<T>.mapWith(transform: T.() -> R): List<R> = map { with(it, transform) }