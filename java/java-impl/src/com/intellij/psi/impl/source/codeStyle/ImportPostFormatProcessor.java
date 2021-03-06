/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.intellij.psi.impl.source.codeStyle;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.openapi.util.TextRange;

public class ImportPostFormatProcessor implements PostFormatProcessor {
  public PsiElement processElement(PsiElement source, CodeStyleSettings settings) {
    return new ImportsFormatter(settings, source.getContainingFile()).process(source);
  }

  public TextRange processText(PsiFile source, TextRange rangeToReformat, CodeStyleSettings settings) {
    return new ImportsFormatter(settings, source.getContainingFile()).processText(source, rangeToReformat);
  }
}
