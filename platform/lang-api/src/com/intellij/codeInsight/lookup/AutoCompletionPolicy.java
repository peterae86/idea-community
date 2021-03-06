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
package com.intellij.codeInsight.lookup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * What to do if there's only one element in completion lookup? Should IDEA show lookup or just insert this element? Call
 * {@link #applyPolicy(LookupElement)} to decorate {@link com.intellij.codeInsight.lookup.LookupElement} with correct policy.
 *
 * Use this only in simple cases, use {@link com.intellij.codeInsight.completion.CompletionContributor#handleAutoCompletionPossibility(com.intellij.codeInsight.completion.AutoCompletionContext)}
 * for finer tuning.
 *
 * @author peter
 */
public enum AutoCompletionPolicy {
  /**
   * Self-explaining
   */
  NEVER_AUTOCOMPLETE,

  /**
   * If 'auto-complete if only one choice' is configured in settings, the item will be inserted, otherwise - no.
   */
  SETTINGS_DEPENDENT,

  /**
   * If caret is positioned inside an identifier, and 'auto-complete if only one choice' is configured in settings,
   * a lookup with one item will still open, giving user a chance to overwrite the identifier using Tab key
   */
  GIVE_CHANCE_TO_OVERWRITE,

  /**
   * Self-explaining
   */
  ALWAYS_AUTOCOMPLETE;

  @NotNull
  public LookupElement applyPolicy(@NotNull LookupElement element) {
    return new PolicyDecorator(element, this);
  }

  @Nullable
  public static AutoCompletionPolicy getPolicy(LookupElement element) {
    final PolicyDecorator decorator = element.as(PolicyDecorator.class);
    if (decorator != null) {
      return decorator.myPolicy;
    }
    return null;
  }

  private static class PolicyDecorator extends LookupElementDecorator<LookupElement> {
    private final AutoCompletionPolicy myPolicy;

    public PolicyDecorator(LookupElement element, AutoCompletionPolicy policy) {
      super(element);
      myPolicy = policy;
    }

  }
}
