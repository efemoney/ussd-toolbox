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

import org.gradle.api.Project
import java.io.File
import java.lang.Runtime.getRuntime

val isCi = System.getenv("CI") == "true"

val Project.isIdeBuild get() = !hasProperty("android.injected.invoked.from.ide")

val Project.isReleaseBuild get() = gradle.startParameter.taskNames.any { it.endsWith("Release") }

val Project.gitTag get() = "git describe --tags".exec(rootDir, "dev")

val Project.gitSha get() = "git rev-parse --short HEAD".exec(rootDir, "none")

val Project.gitTimestamp get() = "git log -n 1 --format=%at".exec(rootDir, "0").toInt()

val Project.gitCommitCount: Int get() = 100 + "git rev-list --count HEAD".exec(rootDir, "0").toInt()

fun String.exec(workingDir: File? = null, fallback: String = ""): String {
  return getRuntime().exec(this, null, workingDir).run {
    waitFor()
    runCatching { inputStream.bufferedReader().readText().trim() }
      .getOrElse { fallback }
      .ifEmpty { fallback }
  }
}
