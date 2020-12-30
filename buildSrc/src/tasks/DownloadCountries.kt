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

package tasks

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.sink
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class DownloadCountries @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {

  @OutputFile
  val countriesJsonFile = objectFactory.fileProperty()

  @TaskAction
  internal fun downloadCountries() {
    OkHttpClient()
      .newCall(
        Request.Builder()
          .get()
          .url("https://restcountries.eu/rest/v2/all?fields=name;nativeName;alpha2Code;altSpellings;translations;languages")
          .build()
      )
      .execute()
      .body
      ?.source()
      ?.readAll(countriesJsonFile.asFile.get().sink())
  }
}