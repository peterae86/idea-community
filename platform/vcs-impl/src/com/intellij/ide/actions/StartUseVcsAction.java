package com.intellij.ide.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vcs.VcsDirectoryMapping;

import java.util.Arrays;

public class StartUseVcsAction extends AnAction implements DumbAware {
  @Override
  public void update(final AnActionEvent e) {
    final VcsDataWrapper data = new VcsDataWrapper(e);
    final boolean enabled = data.enabled();

    final Presentation presentation = e.getPresentation();
    presentation.setEnabled(enabled);
    presentation.setVisible(enabled);
    if (enabled) {
      presentation.setText(VcsBundle.message("action.enable.version.control.integration.text"));
    }
  }

  public void actionPerformed(final AnActionEvent e) {
    final VcsDataWrapper data = new VcsDataWrapper(e);
    final boolean enabled = data.enabled();
    if (! enabled) {
      return;
    }

    final StartUseVcsDialog dialog = new StartUseVcsDialog(data);
    dialog.show();
    if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
      final String vcsName = dialog.getVcs();
      if (vcsName.length() > 0) {
        final ProjectLevelVcsManager manager = data.getManager();
        manager.setDirectoryMappings(Arrays.asList(new VcsDirectoryMapping("", vcsName)));
      }
    }
  }

}