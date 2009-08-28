package com.intellij.historyIntegrTests.ui;

import com.intellij.history.integration.TestVirtualFile;
import com.intellij.history.integration.ui.actions.LocalHistoryAction;
import com.intellij.history.integration.ui.actions.ShowHistoryAction;
import com.intellij.history.integration.ui.actions.ShowSelectionHistoryAction;
import com.intellij.historyIntegrTests.IntegrationTestCase;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class LocalHistoryActionsTest extends IntegrationTestCase {
  VirtualFile f;
  Editor editor;
  Document document;

  @Override
  protected void setUpInWriteAction() throws Exception {
    super.setUpInWriteAction();
    f = root.createChildData(null, "f.txt");

    document = FileDocumentManager.getInstance().getDocument(f);
    document.setText("foo");

    editor = getEditorFactory().createEditor(document);
  }

  @Override
  protected void tearDown() throws Exception {
    getEditorFactory().releaseEditor(editor);
    super.tearDown();
  }

  private EditorFactory getEditorFactory() {
    return EditorFactory.getInstance();
  }

  public void testShowHistoryAction() throws IOException {
    ShowHistoryAction a = new ShowHistoryAction();
    assertStatus(a, root, true);
    assertStatus(a, f, true);
    assertStatus(a, null, false);

    VirtualFile ignored = root.createChildData(null, "f.xxx");
    VirtualFile notUnderContentRoot = root.createChildData(null, "CVS");

    assertStatus(a, ignored, false);
    assertStatus(a, notUnderContentRoot, false);
  }

  public void testLocalHistoryActionDisabledWithoutProject() throws IOException {
    LocalHistoryAction a = new LocalHistoryAction() {
      public void actionPerformed(AnActionEvent e) {
      }
    };
    assertStatus(a, root, myProject, true);
    assertStatus(a, root, null, false);
  }
  
  public void testShowHistoryActionIsDisabledForMultipleSelection() throws Exception {
    ShowHistoryAction a = new ShowHistoryAction();
    assertStatus(a, new VirtualFile[] {f, new TestVirtualFile("ff")}, myProject, false);
  }

  public void testShowSelectionHistoryActionForSelection() throws Exception {
    editor.getSelectionModel().setSelection(0, 2);

    ShowSelectionHistoryAction a = new ShowSelectionHistoryAction();
    AnActionEvent e = createEventFor(a, new VirtualFile[] {f}, myProject);
    a.update(e);

    assertTrue(e.getPresentation().isEnabled());

    assertEquals("Show History for Selection", e.getPresentation().getText());
  }

  public void testShowSelectionHistoryActionIsDisabledForNonFiles() throws IOException {
    ShowSelectionHistoryAction a = new ShowSelectionHistoryAction();
    assertStatus(a, root, false);
    assertStatus(a, null, false);
  }

  public void testShowSelectionHistoryActionIsDisabledForEmptySelection() throws Exception {
    ShowSelectionHistoryAction a = new ShowSelectionHistoryAction();
    assertStatus(a, f, false);
  }

  private void assertStatus(AnAction a, VirtualFile f, boolean isEnabled) {
    assertStatus(a, f, myProject, isEnabled);
  }

  private void assertStatus(AnAction a, VirtualFile f, Project p, boolean isEnabled) {
    VirtualFile[] files = f == null ? null : new VirtualFile[]{f};
    assertStatus(a, files, p, isEnabled);
  }

  private void assertStatus(AnAction a, VirtualFile[] files, Project p, boolean isEnabled) {
    AnActionEvent e = createEventFor(a, files, p);
    a.update(e);
    assertEquals(isEnabled, e.getPresentation().isEnabled());
  }

  private AnActionEvent createEventFor(AnAction a, final VirtualFile[] files, final Project p) {
    DataContext dc = new DataContext() {
      @Nullable
      public Object getData(String id) {
        if (id.equals(PlatformDataKeys.VIRTUAL_FILE_ARRAY.getName())) return files;
        if (id.equals(PlatformDataKeys.EDITOR.getName())) return editor;
        if (id.equals(PlatformDataKeys.PROJECT.getName())) return p;
        return null;
      }
    };
    return new AnActionEvent(null, dc, "", a.getTemplatePresentation(), null, -1);
  }
}