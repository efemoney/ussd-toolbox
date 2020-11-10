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
  `simple-project-layout`
  android("library")
  kotlin("android")
  kotlin("kapt")
}

dependencies {
  api(project(":service-api"))

  kapt(Deps.dagger.compiler)
  implementation(Deps.jsr250)
  implementation(Deps.dagger)

  implementation(Deps.kotlin.stdlib.jdk8)
  implementation(Deps.androidx.appcompat)
  implementation(Deps.material.android)

  kapt(Deps.moshi.kotlinCodegen)
  implementation(Deps.moshi)
  implementation(Deps.moshi.adapters)

  implementation(Deps.okio)
  implementation(Deps.okHttp)
  implementation(Deps.okHttp.logging)
  implementation(Deps.retrofit)
  implementation(Deps.retrofit.converter.moshi)
}
