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

import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.TaskTriggersConfig
import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
  id("simple-project-layout")
  kotlin("jvm")
}

// Configure intelliJ to run :service-def:assemble after sync
rootProject.the<IdeaModel>().project
  .cast<ExtensionAware>()
  .the<ProjectSettings>()
  .cast<ExtensionAware>()
  .the<TaskTriggersConfig>()
  .afterSync(tasks.named("assemble"))

dependencies {
  api(projects.serviceApi)
  api(projects.serviceDsl)
  api(Deps.kotlin.scripting.jvm)

  implementation(Deps.kotlin.stdlib.jdk8)
  implementation(Deps.kotlin.reflect)
  implementation(Deps.kotlinpoet)
}
