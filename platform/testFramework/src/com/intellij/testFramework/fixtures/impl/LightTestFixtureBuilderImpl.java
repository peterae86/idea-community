/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package com.intellij.testFramework.fixtures.impl;

import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;

/**
 * @author mike
*/
class LightTestFixtureBuilderImpl<F extends IdeaProjectTestFixture> implements TestFixtureBuilder<F> {

  private final F myFixture;

  public LightTestFixtureBuilderImpl(F fixture) {
    myFixture = fixture;
  }

  public F getFixture() {
    return myFixture;
  }

  public <M extends ModuleFixtureBuilder> M addModule(final Class<M> builderClass) {
    throw new UnsupportedOperationException("addModule is not allowed in : " + getClass());
  }
}