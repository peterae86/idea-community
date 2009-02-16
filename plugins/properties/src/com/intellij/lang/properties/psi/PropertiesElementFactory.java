/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.lang.properties.psi;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author cdr
 */
public class PropertiesElementFactory {
  @NotNull
  public static Property createProperty(@NotNull Project project, @NonNls @NotNull String name, @NonNls @NotNull String value) {
    String text = escape(name) + "=" + value;
    final PropertiesFile dummyFile = createPropertiesFile(project, text);
    return dummyFile.getProperties().get(0);
  }

  @NotNull
  public static PropertiesFile createPropertiesFile(@NotNull Project project, @NonNls @NotNull String text) {
    @NonNls String filename = "dummy." + StdFileTypes.PROPERTIES.getDefaultExtension();
    return (PropertiesFile)PsiFileFactory.getInstance(PsiManager.getInstance(project).getProject())
      .createFileFromText(filename, StdFileTypes.PROPERTIES, text);
  }

  @NotNull
  private static String escape(@NotNull String name) {
    if (StringUtil.startsWithChar(name, '#')) {
      name = escapeChar(name, '#');
    }
    if (StringUtil.startsWithChar(name, '!')) {
      name = escapeChar(name, '!');
    }
    name = escapeChar(name, '=');
    name = escapeChar(name, ':');
    name = escapeChar(name, ' ');
    name = escapeChar(name, '\t');
    return name;
  }

  @NotNull
  private static String escapeChar(@NotNull String name, char c) {
    int offset = 0;
    while (true) {
      int i = name.indexOf(c, offset);
      if (i == -1) return name;
      if (i == 0 || name.charAt(i - 1) != '\\') {
        name = name.substring(0, i) + '\\' + name.substring(i);
      }
      offset = i + 2;
    }
  }
}