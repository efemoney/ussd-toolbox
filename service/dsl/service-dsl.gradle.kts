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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tasks.DownloadCountries
import tasks.GenerateCountriesDsl

plugins {
  kotlin("jvm")
}

val generatedFileLocation = buildDir("generated/source/countries")

kotlin.sourceSets {
  get("main").kotlin.srcDir(generatedFileLocation)
}

tasks {

  register<GenerateCountriesDsl>("countriesDsl") {
    val downloadCountries: DownloadCountries by rootProject.tasks

    outputDir = generatedFileLocation
    outputPackage = "com.efemoney.ussdtoolbox.service.dsl"

    dependsOn(downloadCountries)
    countriesJsonFile = downloadCountries.countriesJsonFile
  }

  named<KotlinCompile>("compileKotlin") {
    dependsOn("countriesDsl")
  }
}

dependencies {
  api(project(":service-api"))

  implementation(Deps.kotlin.stdlib.jdk8)
}
