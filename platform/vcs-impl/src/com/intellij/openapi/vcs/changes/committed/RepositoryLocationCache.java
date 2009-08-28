package com.intellij.openapi.vcs.changes.committed;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.CommittedChangesProvider;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.RepositoryLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RepositoryLocationCache {
  private final Project myProject;
  private final Map<Pair<String, String>, RepositoryLocation> myMap;

  public RepositoryLocationCache(final Project project) {
    myProject = project;
    myMap = new HashMap<Pair<String, String>, RepositoryLocation>();
  }

  public RepositoryLocation getLocation(final AbstractVcs vcs, final FilePath filePath) {
    final Pair<String, String> key = new Pair<String, String>(vcs.getName(), filePath.getIOFile().getAbsolutePath());
    RepositoryLocation location = myMap.get(key);
    if (location != null) {
      return location;
    }
    location = getUnderProgress(vcs, filePath);
    myMap.put(key, location);
    return location;
  }

  private RepositoryLocation getUnderProgress(final AbstractVcs vcs, final FilePath filePath) {
    final MyLoader loader = new MyLoader(vcs, filePath);
    if (ApplicationManager.getApplication().isDispatchThread()) {
      ProgressManager.getInstance().runProcessWithProgressSynchronously(loader, "Discovering location of " + filePath.getPresentableUrl(),
                                                                        true, myProject);
    } else {
      loader.run();
    }
    return loader.getLocation();
  }

  private class MyLoader implements Runnable {
    private final AbstractVcs myVcs;
    private final FilePath myFilePath;
    private RepositoryLocation myLocation;

    private MyLoader(@NotNull final AbstractVcs vcs, @NotNull FilePath filePath) {
      myVcs = vcs;
      myFilePath = filePath;
    }

    public void run() {
      final CommittedChangesProvider committedChangesProvider = myVcs.getCommittedChangesProvider();
      if (committedChangesProvider != null) {
        myLocation = committedChangesProvider.getLocationFor(myFilePath);
      }
    }

    public RepositoryLocation getLocation() {
      return myLocation;
    }
  }
}