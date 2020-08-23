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

pluginManagement {

  repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap") {
      content {
        includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*eap.*")
        includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*-M\\d+.*")
      }
    }
    gradlePluginPortal()
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "org.jetbrains.kotlin.plugin.serialization" ->
          useModule("org.jetbrains.kotlin:kotlin-serialization:1.4.0")

        "org.jetbrains.kotlin.jvm",
        "org.jetbrains.kotlin.kapt" ->
          useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
      }
    }
  }
}

rootProject.buildFileName = "buildSrc.gradle.kts"