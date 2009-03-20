/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.intellij.lang.folding;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Used by LanguageFolding class if more than one FoldingBuilder were specified
 * for a particular language.
 *
 * @author Konstantin Bulenkov
 * @see com.intellij.lang.folding.LanguageFolding
 * @since 9.0
 */
class CompositeFoldingBuilder implements FoldingBuilder {
  private static final Key<FoldingBuilder> FOLDING_BUILDER = new Key<FoldingBuilder>("FOLDING_BUILDER");
  private final List<FoldingBuilder> myBuilders;

  CompositeFoldingBuilder(List<FoldingBuilder> builders) {    
    myBuilders = builders;
  }

  public FoldingDescriptor[] buildFoldRegions(ASTNode node, Document document) {
    final List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();

    for (FoldingBuilder builder : myBuilders) {
      for (FoldingDescriptor descriptor : builder.buildFoldRegions(node, document)) {
        descriptor.getElement().putUserData(FOLDING_BUILDER, builder);
        descriptors.add(descriptor);
      }
    }

    return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
  }

  public String getPlaceholderText(ASTNode node) {
    final FoldingBuilder builder = node.getUserData(FOLDING_BUILDER);
    return builder == null ? node.getText() : builder.getPlaceholderText(node);
  }

  public boolean isCollapsedByDefault(ASTNode node) {
    final FoldingBuilder builder = node.getUserData(FOLDING_BUILDER);
    return builder == null ? false : builder.isCollapsedByDefault(node);
  }
}