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

package com.intellij.codeInsight.template.macro;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author Roman.Chernyatchik
 * @author Konstantin Bulenkov
 */
public class ReplaceUnderscoresWithSpacesMacro extends MacroBase {
  public ReplaceUnderscoresWithSpacesMacro() {
    super("undescoresToSpaces", CodeInsightBundle.message("macro.undescoresToSpaces.string"));
  }

  @Override
  protected Result calculateResult(@NotNull Expression[] params, ExpressionContext context, boolean quick) {
    final String text = getTextResult(params, context);
    if (text != null) {
      return new TextResult(text.replace('_', ' '));
    }
    return null;
  }
}