/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package com.intellij.psi.formatter;

import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;

/**
 * Misc. code wrapping functions
 * @author rvishnyakov
 */
public class WrappingUtil {

  private WrappingUtil() {
  }

  public static boolean shouldWrap(int setting) {
    return setting != CodeStyleSettings.DO_NOT_WRAP;
  }

  public static Wrap createWrap(int setting) {
    return Wrap.createWrap(getWrapType(setting), true);
  }

  public static WrapType getWrapType(int setting) {
    switch (setting) {
      case CodeStyleSettings.WRAP_ALWAYS:
        return WrapType.ALWAYS;
      case CodeStyleSettings.WRAP_AS_NEEDED:
        return WrapType.NORMAL;
      case CodeStyleSettings.DO_NOT_WRAP:
        return WrapType.NONE;
      default:
        return WrapType.CHOP_DOWN_IF_LONG;
    }
  }

  public static WrapType getArrayInitializerWrapType(CommonCodeStyleSettings settings,
                                                     ASTNode node,
                                                     IElementType lBraceType,
                                                     IElementType rBraceType) {
    WrapType wrapType = WrapType.NONE;
    if (shouldWrap(settings.ARRAY_INITIALIZER_WRAP)) {
      if (node.getElementType() == rBraceType) {
        wrapType = settings.ARRAY_INITIALIZER_RBRACE_ON_NEXT_LINE ? Wrap.ALWAYS : Wrap.NONE;
      }
      else if (FormatterUtil.isPrecededBy(node, lBraceType)) {
        wrapType = settings.ARRAY_INITIALIZER_LBRACE_ON_NEXT_LINE ? Wrap.ALWAYS : Wrap.NONE;
      }
      else {
        wrapType = WrappingUtil.getWrapType(settings.ARRAY_INITIALIZER_WRAP);
      }
    }
    return wrapType;
  }
}
