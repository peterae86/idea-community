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
package com.intellij.openapi.editor.event;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

import java.util.EventObject;

public class SelectionEvent extends EventObject {
  private final int myOldSelectionStart;
  private final int myOldSelectionEnd;
  private final int myNewSelectionStart;
  private final int myNewSelectionEnd;

  public SelectionEvent(Editor editor,
                        int oldSelectionStart, int oldSelectionEnd,
                        int newSelectionStart, int newSelectionEnd) {
    super(editor);

    myOldSelectionStart = oldSelectionStart;
    myOldSelectionEnd = oldSelectionEnd;
    myNewSelectionStart = newSelectionStart;
    myNewSelectionEnd = newSelectionEnd;
  }

  public Editor getEditor() {
    return (Editor) getSource();
  }

  public TextRange getOldRange() {
    return new TextRange(myOldSelectionStart, myOldSelectionEnd);
  }

  public TextRange getNewRange() {
    return new TextRange(myNewSelectionStart, myNewSelectionEnd);
  }
}