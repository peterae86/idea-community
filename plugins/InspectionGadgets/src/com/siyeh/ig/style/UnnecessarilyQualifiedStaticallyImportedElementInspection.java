/*
 * Copyright 2010 Bas Leijdekkers
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
package com.siyeh.ig.style;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.psiutils.ImportUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UnnecessarilyQualifiedStaticallyImportedElementInspection
        extends BaseInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "unnecessarily.qualified.statically.imported.element.display.name");
    }

    @NotNull
    @Override
    protected String buildErrorString(Object... infos) {
        final PsiMember member = (PsiMember) infos[0];
        return InspectionGadgetsBundle.message(
                "unnecessarily.qualified.statically.imported.element.problem.descriptor",
                member.getName());
    }

    @Override
    protected InspectionGadgetsFix buildFix(Object... infos) {
        return new UnnecessarilyQualifiedStaticallyImportedElementFix();
    }

    private static class UnnecessarilyQualifiedStaticallyImportedElementFix
            extends InspectionGadgetsFix {

        @NotNull
        public String getName() {
            return InspectionGadgetsBundle.message(
                    "unnecessarily.qualified.statically.imported.element.quickfix");
        }

        @Override
        protected void doFix(Project project, ProblemDescriptor descriptor)
                throws IncorrectOperationException {
            final PsiElement element = descriptor.getPsiElement();
            element.delete();
        }
    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        return new UnnecessarilyQualifiedStaticallyImportedElementVisitor();
    }

    private static class UnnecessarilyQualifiedStaticallyImportedElementVisitor
            extends BaseInspectionVisitor {

        @Override
        public void visitReferenceExpression(
                PsiReferenceExpression expression) {
            visitReferenceElement(expression);
        }

        @Override
        public void visitReferenceElement(
                PsiJavaCodeReferenceElement reference) {
            super.visitReferenceElement(reference);
            final PsiElement qualifier = reference.getQualifier();
            if (!(qualifier instanceof PsiReferenceExpression)) {
                return;
            }
            final PsiElement parent = reference.getParent();
            if (parent instanceof PsiReferenceExpression) {
                return;
            }
            if (parent instanceof PsiImportStatementBase) {
                return;
            }
            final PsiElement target = reference.resolve();
            if (!(target instanceof PsiMember)) {
                return;
            }
            final PsiMember member = (PsiMember) target;
            final PsiReferenceExpression referenceExpression =
                    (PsiReferenceExpression) qualifier;
            final PsiElement qualifierTarget = referenceExpression.resolve();
            if (!(qualifierTarget instanceof PsiClass)) {
                return;
            }
            if (!ImportUtils.isStaticallyImported(member, reference)) {
                return;
            }
            registerError(qualifier, member);
        }
    }
}
