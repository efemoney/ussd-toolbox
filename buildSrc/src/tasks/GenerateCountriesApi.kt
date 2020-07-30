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
import com.squareup.kotlinpoet.jvm.jvmStatic
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setValue
import tasks.countries.LanguageCode
import tasks.internal.*
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

open class GenerateCountriesApi @Inject constructor(objects: ObjectFactory) : DefaultTask() {

  private val generatedFileComment = generatedBy("GenerateCountriesApi")
  private val generatedFileSuppressAnnotation = generatedFilesSuppressAnnotation()

  private val classNames: ClassNames

  private val names: Provider<ClassNames2>

  @get:InputFile
  var countriesJsonFile by objects.fileProperty()

  @get:Input
  var outputPackage by objects.property<String>().apply {
    classNames = ClassNames(this)
    names = map(::ClassNames2)
  }

  @get:OutputDirectory
  var outputDir by objects.directoryProperty()

  private val countryFields: Map<String, TypeName> by lazy {
    mapOf(
      "name" to classNames.string,
      "nativeName" to classNames.string,
      "countryCode" to classNames.countryCode,
      "translations" to classNames.mapOf639ToString,
      "altSpellings" to classNames.listOfStrings,
      "languages" to classNames.listOfLanguages
    )
  }

  private val languageFields: Map<String, TypeName> by lazy {
    mapOf(
      "name" to classNames.string,
      "nativeName" to classNames.string,
      "languageCode" to classNames.languageCode
    )
  }

  private val fieldNameMappings = mapOf("countryCode" to "alpha2Code", "languageCode" to "iso639_1")

  @TaskAction
  internal fun generateCountries() {

    val countries = CountriesJsonParser.parse(countriesJsonFile.asFile)

    val languages = countries.asSequence()
      .flatMap { it.languages.asSequence() }
      .filter { it.iso639_1.isNotBlank() }
      .toSet()

    writeInterfacesToFile()
    writeLanguagesImplToFile(languages)
    writeCountriesImplToFile(countries)
  }

  private fun writeInterfacesToFile() {
    FileSpec.builder(outputPackage, "models")
      .addComment(generatedFileComment)
      .addTypeAlias(TypeAliasSpec.builder("LanguageCode", String::class).build())
      .addTypeAlias(TypeAliasSpec.builder("CountryCode", String::class).build())
      .addType(interfaceTypeSpec(classNames.language, languageFields))
      .addType(interfaceTypeSpec(classNames.country, countryFields))
      .build()
      .writeTo(outputDir.asFile)
  }

  private fun writeLanguagesImplToFile(languages: Languages) {
    FileSpec.builder(outputPackage, "Languages")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnnotation)
      .addType(
        TypeSpec
          .objectBuilder(classNames.languages)
          .addProperties(
            languages.sortedBy(LanguageDto::iso639_1).map {
              PropertySpec.builder(it.iso639_1, classNames.language)
                .initializer("%L", interfaceImplTypeSpec(it, classNames.language, languageFields))
                .build()
            }
          )
          .addProperty(
            PropertySpec.builder("all", classNames.mapOfCodeToLanguage).initializer(
              languages
                .map { CodeBlock.of("%S·to·%N", it.iso639_1, it.iso639_1) }
                .joinToCode(separator = ", ", prefix = "mapOf(\n", suffix = "\n)")
            ).build()
          )
          .addFunction(
            FunSpec.builder("findByLanguageCode")
              .addAnnotation(JvmStatic::class)
              .addParameter("languageCode", classNames.languageCode)
              .returns(classNames.language.copy(nullable = true))
              .addCode("return all[languageCode]")
              .build()
          )
          .build()
      )
      .build()
      .writeTo(outputDir.asFile)
  }

  private fun writeCountriesImplToFile(countries: Countries) {
    FileSpec.builder(outputPackage, "Countries")
      .addComment(generatedFileComment)
      .addAnnotation(generatedFileSuppressAnnotation)
      .addType(
        TypeSpec
          .objectBuilder(classNames.countries)
          .addProperties(
            countries.sortedBy(CountryDto::alpha2Code).map {
              PropertySpec.builder(it.alpha2Code, classNames.country)
                .initializer("%L", interfaceImplTypeSpec(it, classNames.country, countryFields))
                .build()
            }
          )
          .addProperty(
            PropertySpec.builder("all", classNames.mapOfCodeToCountry).initializer(
              countries
                .map { CodeBlock.of("%S·to·%N", it.alpha2Code, it.alpha2Code) }
                .joinToCode(separator = ", ", prefix = "mapOf(\n", suffix = "\n)")
            ).build()
          )
          .addFunction(
            FunSpec.builder("findByCountryCode")
              .addParameter("countryCode", classNames.countryCode)
              .returns(classNames.country.copy(nullable = true))
              .addCode("return all[countryCode]")
              .jvmStatic()
              .build()
          )
          .build()
      )
      .build()
      .writeTo(outputDir.asFile)
  }

  private fun interfaceTypeSpec(type: ClassName, fields: Map<String, TypeName>): TypeSpec {
    return TypeSpec.interfaceBuilder(type).addProperties(
      fields.map { PropertySpec.builder(it.key, it.value).build() }
    ).build()
  }

  private fun interfaceImplTypeSpec(data: Any, type: ClassName, fields: Map<String, TypeName>): TypeSpec {

    return TypeSpec
      .anonymousClassBuilder()
      .addSuperinterface(type)
      .addProperties(
        fields.map { (name, type) ->
          PropertySpec.builder(name, type, KModifier.OVERRIDE)
            .initializer(codeBlockForObjectField(name, type, data))
            .build()
        }
      )
      .addFunction(
        FunSpec.builder("toString")
          .addModifiers(KModifier.OVERRIDE)
          .addCode("return name")
          .build()
      )
      .build()
  }

  private fun codeBlockForObjectField(name: String, typeName: TypeName, data: Any): CodeBlock {
    val name = fieldNameMappings[name] ?: name
    val property = data::class.memberProperties.first { it.name == name }
    val returnKlass = property.returnType.classifier as KClass<*>

    return codeBlockFor(typeName, returnKlass, property.getter, data)
  }

  private fun codeBlockFor(
    typeName: TypeName,
    returnKlass: KClass<*>,
    getter: KFunction<*>,
    scope: Any,
    vararg args: Any?
  ): CodeBlock {

    fun get(): Any? = getter.call(scope, *args)

    return when {

      typeName == classNames.language -> {
        val code = get()?.let {
          it::class.memberProperties.firstOrNull { it.name == "iso639_1" }?.call(it)
        } ?: return CodeBlock.of("")

        if ((code as String).isBlank()) return CodeBlock.of("")

        CodeBlock.of("%T.%N", classNames.languages, code)
      }

      returnKlass.isSubclassOf(Map::class) -> {
        require(
          typeName is ParameterizedTypeName &&
            typeName.rawType == classNames.map &&
            typeName.typeArguments.size == 2
        )

        val mapGet = Map<Any, Any>::get

        var obj = get() as Map<Any, Any>

        // Hack: So that country name translations that are exactly the same
        // as the country name in english are not added in the translations map
        if (typeName.typeArguments[0] == classNames.languageCode) {
          val name = scope::class.memberProperties.first { it.name == "name" }.call(scope)
          obj = obj.filterNot { it.value == name }
        }

        val itemBlocks = obj.map {
          CodeBlock.of(
            "%S·to·%L",
            it.key,
            codeBlockFor(typeName.typeArguments[1], it.value::class, mapGet, obj, it.key)
          )
        }

        itemBlocks
          .filter(CodeBlock::isNotEmpty)
          .joinToCode(",\n", prefix = "mapOf(", suffix = ")")
      }

      returnKlass.isSubclassOf(List::class) -> {
        require(
          typeName is ParameterizedTypeName &&
            typeName.rawType == classNames.list &&
            typeName.typeArguments.size == 1
        )

        val obj = get() as List<Any>

        val itemBlocks = obj.withIndex().map {
          codeBlockFor(typeName.typeArguments[0], it.value::class, List<Any>::get, obj, it.index)
        }

        itemBlocks
          .filter(CodeBlock::isNotEmpty)
          .joinToCode(",\n", prefix = "listOf(", suffix = ")")
      }

      returnKlass == String::class -> CodeBlock.of("%S", get())

      else -> CodeBlock.of("%L", get())
    }
  }
}