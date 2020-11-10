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

plugins {
  `kotlin-dsl`
  id("symbol-processing")
}

dependencies {
  // buildSrc dependencies
  implementation("com.squareup.okhttp3:okhttp:4.10.0-RC1")
  implementation("com.squareup:kotlinpoet:1.7.2")
  implementation("com.squareup.moshi:moshi:1.11.0")
  ksp("dev.zacsweers.moshix:moshi-ksp:0.4.0")

  // project plugin dependencies
  implementation(kotlin("gradle-plugin", "1.4.20-RC"))
  implementation(kotlin("serialization", "1.4.20-RC"))
  implementation("com.android.tools.build:gradle:4.2.0-alpha16")
  implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1")
}

tasks.compileKotlin {
  kotlinOptions {
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xopt-in=kotlin.RequiresOptIn",
      "-Xopt-in=kotlin.ExperimentalStdlibApi"
    )
  }
}
