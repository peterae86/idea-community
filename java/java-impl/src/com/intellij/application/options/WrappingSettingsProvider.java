package com.intellij.application.options;

import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.application.ApplicationBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author yole
 */
public class WrappingSettingsProvider extends CodeStyleSettingsProvider {
  @NotNull
  public Configurable createSettingsPage(final CodeStyleSettings settings, final CodeStyleSettings originalSettings) {
    return new WrappingConfigurable(settings, originalSettings);
  }

  @Override
  public String getConfigurableDisplayName() {
    return ApplicationBundle.message("title.wrapping");
  }
}