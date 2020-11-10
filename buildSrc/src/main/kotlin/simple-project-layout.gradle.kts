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

import com.android.build.gradle.BaseExtension

fun simpleName(name: String, suffix: String) = if (name == "main") suffix else "$name-$suffix"

pluginManager.withAnyPlugin("android", "android-library") {
  the<BaseExtension>().sourceSets.configureEach {
    java.srcDir(simpleName(name, "src"))
    res.srcDir(simpleName(name, "res"))
    assets.srcDir(simpleName(name, "assets"))
    manifest.srcFile(simpleName(name, "AndroidManifest.xml"))
  }
}

pluginManager.withAnyPlugin("java", "kotlin") {
  the<JavaPluginConvention>().sourceSets.configureEach {
    java.srcDir(simpleName(name, "src"))
    resources.srcDir(simpleName(name, "resources"))
  }
}