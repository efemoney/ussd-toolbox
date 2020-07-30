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
  kotlin("jvm")
  kotlin("plugin.serialization")
  `kotlin-dsl`
}

sourceSets.main {
  java.srcDir("src")
  // java.srcDir("simple") // Todo investigate why precompiled script plugins fail
}

repositories {
  maven("https://dl.bintray.com/kotlin/kotlin-eap") {
    content {
      includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*eap.*")
      includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*-M\\d+.*")
    }
  }
  maven("https://kotlin.bintray.com/kotlinx/") {
    content {
      includeModuleByRegex(".*kotlinx.*", ".*kotlinx.*")
    }
  }
  google()
  jcenter()
}

dependencies {
  // buildSrc dependencies
  implementation(kotlin("stdlib-jdk8", "1.4.0-rc"))
  implementation(kotlin("reflect", "1.4.0-rc"))
  implementation("com.squareup:kotlinpoet:1.6.0")
  implementation("com.squareup.okhttp3:okhttp:4.8.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

  // project plugin dependencies
  implementation(kotlin("gradle-plugin", "1.4.0-rc"))
  implementation(kotlin("serialization", "1.4.0-rc"))
  implementation("com.android.tools.build:gradle:4.2.0-alpha06")
  implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0")
}