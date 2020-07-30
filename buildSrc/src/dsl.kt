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

@file:Suppress("NOTHING_TO_INLINE", "UnstableApiUsage")

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline fun Project.buildDir(path: String): Directory = layout.buildDirectory.dir(path).get()

inline fun Project.buildFile(path: String): RegularFile = layout.buildDirectory.file(path).get()

inline fun Project.hasDependency(dependencyNotation: Any, inConfiguration: String = "implementation"): Boolean {

  val wanted = dependencies.create(dependencyNotation)

  return configurations[inConfiguration]
    .incoming
    .dependencies
    .matching { it == wanted }
    .isNotEmpty()
}

inline fun PluginManager.withAnyPlugin(vararg plugins: String, action: Action<AppliedPlugin>) =
  plugins.forEach { withPlugin(it, action) }

inline fun RepositoryHandler.kotlinEap() = maven(Repos.kotlinEap) {
  content {
    includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*-eap.*")
    includeVersionByRegex("org.jetbrains.kotlin", ".*", ".*-M\\d+.*")
  }
}

inline fun RepositoryHandler.sonatype() = maven(Repos.sonatype) {
  content {
    includeVersionByRegex(".*", ".*", ".*-SNAPSHOT")
  }
}

inline fun PluginDependenciesSpec.android(type: String = "application"): PluginDependencySpec =
  id("com.android.$type")

inline val PluginDependenciesSpec.navigationSafeArgs: PluginDependencySpec
  get() = id("androidx.navigation.safeargs.kotlin")

inline val PluginDependenciesSpec.`idea-ext`: PluginDependencySpec
  get() = id("org.jetbrains.gradle.plugin.idea-ext") version "0.8.1"

inline val PluginDependenciesSpec.`gradle-versions`: PluginDependencySpec
  get() = id("com.github.ben-manes.versions") version "0.29.0"

inline fun <T : AndroidSourceSet> NamedDomainObjectContainer<T>.addKotlinDirectories() =
  configureEach { java.srcDir("src/$name/kotlin") }

inline fun LibraryExtension.ignoreDebugBuildType() {
  variantFilter {
    ignore = buildType.name == "debug"
  }
}
