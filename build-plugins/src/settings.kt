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

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import org.gradle.api.Plugin
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.file.FileOperations
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.management.internal.autoapply.AutoAppliedGradleEnterprisePlugin
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

class UssdToolboxSettings @Inject constructor(
  private val files: FileOperations,
  private val processes: ExecOperations
) : Plugin<Settings> {

  private val ProjectDescriptor.allChildren: Sequence<ProjectDescriptor>
    get() = children.asSequence().flatMap { sequenceOf(it) + it.allChildren }

  override fun apply(target: Settings) = with(target) {
    rootProject.name = "ussd-toolbox"
    rootProject.buildFileName = "root.gradle.kts"

    enableFeaturePreview("VERSION_ORDERING_V2")
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

    apply(plugin = AutoAppliedGradleEnterprisePlugin.ID.id)

    configure<GradleEnterpriseExtension> {
      buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        buildScanPublished {
          if (System.getenv("CI") != "true") processes.exec { commandLine("open", buildScanUri) }
        }
      }
    }

    gradle.settingsEvaluated {
      rootProject.allChildren.forEach {

        val path = it.path
        val splitPath = path.removePrefix(":").replace(':', '-').split('-')

        // Given path :tooling:idea-plugin, after splitting becomes [tooling, idea, plugin]
        // Generates sequence: [tooling, idea, plugin], [tooling, idea-plugin], [tooling-idea-plugin]

        val filePathCandidates = generateSequence(splitPath) {
          if (it.size <= 1) null else it.dropLast(2) + it.takeLast(2).joinToString("-")
        }

        val projectDirFile = filePathCandidates
          .map { files.file(it.joinToString(File.separator)) }
          .first { it.exists() && it.isDirectory }

        val projectBuildFile = filePathCandidates
          .map { "${it.last()}.gradle.kts" }
          .first { projectDirFile.resolve(it).exists() }

        project(path).apply {
          buildFileName = projectBuildFile
          projectDir = projectDirFile
        }
      }
    }
  }
}
