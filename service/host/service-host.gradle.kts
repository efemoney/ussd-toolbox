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
  application
  kotlin("jvm")
}

application {
  mainClassName = "com.efemoney.ussdtoolbox.service.host.HostKt"
}

tasks.run.configure {
  val scripts = objects.fileTree()
  scripts.from("$rootDir/scripts")
  scripts.include("*.ussd")

  argumentProviders += CommandLineArgumentProvider { scripts.files.map(File::getPath) }
}

dependencies {
  implementation(project(":service-def"))
  implementation(project(":service-impl"))

  implementation(Deps.kotlin.stdlib.jdk8)
  implementation(Deps.kotlin.scripting.jvmHost)
  implementation(Deps.kotlinx.coroutines.core)
  implementation(Deps.kotlinx.serialization.core)

  implementation("com.github.ajalt:clikt:2.8.0")
}


