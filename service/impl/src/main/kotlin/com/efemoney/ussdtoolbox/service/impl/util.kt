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

@file:JvmName("DslUtil")

package com.efemoney.ussdtoolbox.service.impl

internal fun colorError(): Nothing = throw IllegalArgumentException("Invalid color")

internal fun String.padColor(): String { // RGB, ARGB
  return when (val len = length) {
    3, 4 -> StringBuilder(len * 2).also { for (c in this@padColor) it.append(c).append(c) }.toString()
    else -> this
  }
}

/** Copied from Androids [Color.parseColor] */
internal fun String.toColor(): Int {
  if (firstOrNull() != '#') colorError()

  val colorString = substring(1).padColor()
  val color = colorString.toLong(16)

  return when (colorString.length) {
    6 -> color or -0x1000000 // Set the alpha
    8 -> color
    else -> colorError()
  }.toInt()
}

internal fun Int.toColorString(): String {
  return toLong().let {
    val formatString = if (it > 0xFFFFFF) "#%08X" else "#%06X"
    String.format(formatString, 0xFFFFFFFF and it)
  }
}
