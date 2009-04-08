package com.intellij.mock;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.*;

public class MockEditorEventMulticaster implements EditorEventMulticaster {
  public MockEditorEventMulticaster() {
  }

  public void addDocumentListener(DocumentListener listener) {
  }

  public void addDocumentListener(DocumentListener listener, Disposable parentDisposable) {
      }

  public void removeDocumentListener(DocumentListener listener) {
  }

  public void addEditorMouseListener(EditorMouseListener listener) {
  }

  public void addEditorMouseListener(final EditorMouseListener listener, final Disposable parentDisposable) {
  }

  public void removeEditorMouseListener(EditorMouseListener listener) {
  }

  public void addEditorMouseMotionListener(EditorMouseMotionListener listener) {
  }

  public void addEditorMouseMotionListener(EditorMouseMotionListener listener, Disposable parentDisposable) {
  }

  public void removeEditorMouseMotionListener(EditorMouseMotionListener listener) {
  }

  public void addCaretListener(CaretListener listener) {
  }

  public void addCaretListener(CaretListener listener, Disposable parentDisposable) {
  }

  public void removeCaretListener(CaretListener listener) {
  }

  public void addSelectionListener(SelectionListener listener) {
  }

  public void removeSelectionListener(SelectionListener listener) {
  }

  public void addVisibleAreaListener(VisibleAreaListener listener) {
  }

  public void removeVisibleAreaListener(VisibleAreaListener listener) {
  }

}