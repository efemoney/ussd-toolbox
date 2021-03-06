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

rootProject.buildFileName = "buildSrc.gradle.kts"

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
  resolutionStrategy.eachPlugin {
    if (requested.id.id == "symbol-processing") // Support ksp plugin
      useModule("com.google.devtools.ksp:symbol-processing:1.4.20-dev-experimental-20201222")
  }
}

dependencyResolutionManagement {
  repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap") {
      content { includeVersionByRegex("org.jetbrains.kotlin", ".*", "(.*eap.*|.*-M\\d+.*)") }
    }
    maven("https://kotlin.bintray.com/kotlinx/") {
      content { includeModuleByRegex(".*kotlinx.*", ".*kotlinx.*") }
    }
    google()
    jcenter()
  }
}