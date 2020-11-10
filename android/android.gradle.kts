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

@file:Suppress("UnstableApiUsage")

plugins {
  android()
  kotlin("android")
  kotlin("kapt")
  navigationSafeArgs
}

android {

  (buildTypes) {

    "debug" {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"
    }

    "release" {
      postprocessing {
        isObfuscate = true
        isOptimizeCode = true
        isRemoveUnusedCode = true
        isRemoveUnusedResources = true
      }
    }
  }

  lintOptions {
    enable(
      "NewApi",
      "DuplicateStrings"
    )

    xmlReport = isCi
    textReport = isCi
    htmlReport = !isCi

    lintConfig = rootProject.file("lint.xml")

    textOutput("stdout")
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(project(":app:data"))
  implementation(project(":app:design"))

  implementation(Deps.timber)

  implementation(Deps.kotlin.stdlib.jdk8)
  implementation(Deps.kotlinx.coroutines.core)
  implementation(Deps.kotlinx.coroutines.android)

  kapt(Deps.dagger.compiler)
  implementation(Deps.jsr250)
  implementation(Deps.dagger)

  implementation(Deps.androidx.coreKtx)
  implementation(Deps.androidx.fragmentKtx)
  implementation(Deps.androidx.activityKtx)
  implementation(Deps.androidx.lifecycle.livedataCoreKtx)
  implementation(Deps.androidx.lifecycle.livedataKtx)
  implementation(Deps.androidx.lifecycle.viewmodelKtx)
  implementation(Deps.androidx.navigation.uiKtx)
  implementation(Deps.androidx.navigation.fragmentKtx)

  implementation(Deps.androidx.appcompat)
  implementation(Deps.androidx.recyclerView)
  implementation(Deps.androidx.constraintLayout)
  implementation(Deps.androidx.coordinatorLayout)

  implementation(Deps.material.android)
}
