package com.intellij.psi.impl.source.tree.java;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAssertStatement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.impl.source.Constants;
import com.intellij.psi.impl.source.tree.ChildRole;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ChildRoleBase;
import org.jetbrains.annotations.NotNull;

public class PsiAssertStatementImpl extends CompositePsiElement implements PsiAssertStatement, Constants {
  private static final Logger LOG = Logger.getInstance("#com.intellij.psi.impl.source.tree.java.PsiAssertStatementImpl");

  public PsiAssertStatementImpl() {
    super(ASSERT_STATEMENT);
  }

  public PsiExpression getAssertCondition() {
    return (PsiExpression)findChildByRoleAsPsiElement(ChildRole.CONDITION);
  }

  public PsiExpression getAssertDescription() {
    return (PsiExpression)findChildByRoleAsPsiElement(ChildRole.ASSERT_DESCRIPTION);
  }

  public ASTNode findChildByRole(int role) {
    LOG.assertTrue(ChildRole.isUnique(role));
    switch(role){
      default:
        return null;

      case ChildRole.ASSERT_KEYWORD:
        return findChildByType(ASSERT_KEYWORD);

      case ChildRole.CONDITION:
        return findChildByType(EXPRESSION_BIT_SET);

      case ChildRole.COLON:
        return findChildByType(COLON);

      case ChildRole.ASSERT_DESCRIPTION:
        {
          ASTNode colon = findChildByRole(ChildRole.COLON);
          if (colon == null) return null;
          ASTNode child;
          for(child = colon.getTreeNext(); child != null; child = child.getTreeNext()){
            if (EXPRESSION_BIT_SET.contains(child.getElementType())) break;
          }
          return child;
        }

      case ChildRole.CLOSING_SEMICOLON:
        return findChildByType(SEMICOLON);
    }
  }

  public int getChildRole(ASTNode child) {
    LOG.assertTrue(child.getTreeParent() == this);
    IElementType i = child.getElementType();
    if (i == ASSERT_KEYWORD) {
      return ChildRole.ASSERT_KEYWORD;
    }
    else if (i == COLON) {
      return ChildRole.COLON;
    }
    else if (i == SEMICOLON) {
      return ChildRole.CLOSING_SEMICOLON;
    }
    else {
      if (EXPRESSION_BIT_SET.contains(child.getElementType())) {
        int role = getChildRole(child, ChildRole.CONDITION);
        if (role != ChildRoleBase.NONE) return role;
        return ChildRole.ASSERT_DESCRIPTION;
      }
      return ChildRoleBase.NONE;
    }
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JavaElementVisitor) {
      ((JavaElementVisitor)visitor).visitAssertStatement(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  public String toString() {
    return "PsiAssertStatement";
  }
}