/*
 * Copyright (c) 2000-2005 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package com.intellij.codeInsight.completion.util;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.Icons;
import com.intellij.util.ui.EmptyIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author peter
 */
public class PsiTypeCanonicalLookupElement extends LookupElement {
  private static final Icon EMPTY_ICON = new EmptyIcon(Icons.CLASS_ICON.getIconWidth() * 2, Icons.CLASS_ICON.getIconHeight());

  private final PsiType myType;
  private final String myPresentableText;

  public PsiTypeCanonicalLookupElement(@NotNull final PsiType type) {
    myType = type;
    myPresentableText = myType.getPresentableText();
  }

  @NotNull
  @Override
  public Object getObject() {
    final PsiClass psiClass = getPsiClass();
    if (psiClass != null) {
      return psiClass;
    }
    return super.getObject();
  }

  @Nullable
  public PsiClass getPsiClass() {
    return PsiUtil.resolveClassInType(myType);
  }

  public PsiType getPsiType() {
    return myType;
  }

  @NotNull
  public String getLookupString() {
    return myPresentableText;
  }

  @Override
  public void handleInsert(InsertionContext context) {
    context.getEditor().getDocument().replaceString(context.getStartOffset(), context.getStartOffset() + getLookupString().length(), getPsiType().getCanonicalText());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof PsiTypeCanonicalLookupElement)) return false;

    final PsiTypeCanonicalLookupElement that = (PsiTypeCanonicalLookupElement)o;

    if (!myType.equals(that.myType)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return myType.hashCode();
  }

  @Override
  public void renderElement(LookupElementPresentation presentation) {
    final PsiClass psiClass = getPsiClass();
    if (psiClass != null) {
      presentation.setIcon(presentation.isReal() ? psiClass.getIcon(Iconable.ICON_FLAG_VISIBILITY) : EMPTY_ICON);
      presentation.setTailText(" (" + PsiFormatUtil.getPackageDisplayName(psiClass) + ")", true);
    }
    final PsiType type = getPsiType();
    presentation.setItemText(type.getPresentableText());
    presentation.setItemTextBold(type instanceof PsiPrimitiveType);
  }

}