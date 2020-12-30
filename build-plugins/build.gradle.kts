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

import org.gradle.plugin.management.internal.autoapply.AutoAppliedGradleEnterprisePlugin as GEnterprise

plugins {
  `kotlin-dsl-base`
  `java-gradle-plugin`
}

repositories {
  gradlePluginPortal {
    content { includeGroup("com.gradle") }
  }
  mavenCentral()
}
sourceSets.main.get().java.setSrcDirs(listOf("src"))
dependencies.implementation("${GEnterprise.GROUP}:${GEnterprise.NAME}:${GEnterprise.VERSION}")

gradlePlugin {
  plugins.create("ussd-toolbox-settings") {
    id = "ussd-toolbox-settings"
    implementationClass = "UssdToolboxSettings"
  }
}
