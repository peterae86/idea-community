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

package com.intellij.codeInsight.completion.simple;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.editorActions.EnterHandler;
import com.intellij.codeInsight.editorActions.enter.EnterAfterUnmatchedBraceHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.ide.DataManager;

/**
 * @author peter
 */
public abstract class BracesTailType extends TailType {

  protected abstract boolean isSpaceBeforeLBrace(CodeStyleSettings styleSettings, Editor editor, final int tailOffset);

  public int processTail(final Editor editor, int tailOffset) {
    CodeStyleSettings styleSettings = CodeStyleSettingsManager.getSettings(editor.getProject());
    if (isSpaceBeforeLBrace(styleSettings, editor, tailOffset)) {
      tailOffset = insertChar(editor, tailOffset, ' ');
    }
    tailOffset = insertChar(editor, tailOffset, '{');
    if (EnterAfterUnmatchedBraceHandler.isAfterUnmatchedLBrace(editor, tailOffset, StdFileTypes.JAVA)) {
      new EnterHandler(EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER)).executeWriteAction(editor,
                                                                                                                            DataManager.getInstance().getDataContext(editor.getContentComponent()));
      return editor.getCaretModel().getOffset();
    }
    return tailOffset;
  }

}