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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.TaskTriggersConfig
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.utils.addToStdlib.cast
import tasks.DownloadCountries
import org.gradle.api.JavaVersion.VERSION_1_8 as java8

plugins {
  `idea-ext`
  `gradle-versions`
}

allprojects {
  repositories {
    sonatype()
    kotlinEap()
    google()
    jcenter()
    mavenCentral()
  }
}

subprojects {

  group = "com.efemoney.ussdtoolbox"
  version = gitTag

  pluginManager.withAnyPlugin("java", "kotlin", "android", "android-library") {

    dependencies {
      "implementation"(platform(Deps.kotlin.bom))
      "implementation"(platform(Deps.kotlinx.coroutines.bom))
    }
  }

  pluginManager.withAnyPlugin("java") {

    configure<JavaPluginExtension> {
      sourceCompatibility = java8
      targetCompatibility = java8
    }
  }

  pluginManager.withPlugin("kotlin-kapt") {

    configure<KaptExtension> {
      correctErrorTypes = true
      showProcessorTimings = true
      mapDiagnosticLocations = true
      arguments {
        if (hasDependency(Deps.dagger.compiler, inConfiguration = "kapt")) {
          arg("dagger.fastInit", "enabled")
          arg("dagger.experimentalDaggerErrorMessages", "enabled")
        }
      }
    }
  }

  pluginManager.withAnyPlugin("android", "android-library") {

    dependencies {
      "implementation"(platform(Deps.okHttp.bom))
    }

    configure<BaseExtension> {

      compileSdkVersion(30)
      buildToolsVersion("30.0.1")

      defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)

        versionCode = gitCommitCount
        versionName = gitTag

        vectorDrawables.useSupportLibrary = true
      }

      compileOptions {
        // https://developer.android.com/studio/releases/gradle-plugin#j8-library-desugaring
        isCoreLibraryDesugaringEnabled = true

        project.dependencies {
          "coreLibraryDesugaring"("com.android.tools:desugar_jdk_libs:1.0.9")
        }

        sourceCompatibility = java8
        targetCompatibility = java8
      }

      sourceSets.addKotlinDirectories()

      buildFeatures.run {
        buildConfig = false
        viewBinding = false
        aidl = false
        prefab = false
        compose = false
        shaders = false
        resValues = false
        renderScript = false
      }
    }
  }

  pluginManager.withPlugin("android") {

    configure<AppExtension> {

      defaultConfig {
        applicationId = "com.efemoney.ussdtoolbox"
      }

      packagingOptions {
        exclude("META-INF/*.kotlin_module")
      }

      buildTypes.named("debug") {
        matchingFallbacks.add("release")
      }
    }
  }

  pluginManager.withPlugin("android-library") {

    the<LibraryExtension>().ignoreDebugBuildType()
  }

  afterEvaluate {

    tasks.withType<KotlinCompile<*>>().configureEach {
      kotlinOptions {
        verbose = true
        freeCompilerArgs = listOf(
          "-progressive",
          "-Xopt-in=kotlin.RequiresOptIn",
          "-Xopt-in=kotlin.ExperimentalStdlibApi",
          "-XXLanguage:+InlineClasses",
          "-XXLanguage:+NewInference",
          "-XXLanguage:+BooleanElvisBoundSmartCasts",
          "-XXLanguage:+SamConversionPerArgument",
          "-XXLanguage:+NewDataFlowForTryExpressions",
          "-XXLanguage:+SamConversionForKotlinFunctions",
          "-XXLanguage:+FunctionReferenceWithDefaultValueAsOtherType"
        )
      }
      if (this is KotlinJvmCompile) kotlinOptions.jvmTarget = "1.8"
    }

    // Configure intelliJ to run :service-def:assemble after sync
    if (project.name == "service-def") {
      rootProject.idea.project
        .cast<ExtensionAware>()
        .the<ProjectSettings>()
        .cast<ExtensionAware>()
        .the<TaskTriggersConfig>()
        .afterSync(tasks.named("assemble"))
    }
  }
}

tasks.register<Delete>("clean") {
  delete(buildDir)
}

tasks.register<DownloadCountries>("downloadCountries") {
  countriesJsonFile = buildFile("countries.json")
}