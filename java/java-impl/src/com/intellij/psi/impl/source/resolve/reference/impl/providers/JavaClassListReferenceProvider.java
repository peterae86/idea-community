package com.intellij.psi.impl.source.resolve.reference.impl.providers;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ik
 * Date: 05.06.2003
 * Time: 20:27:59
 * To change this template use Options | File Templates.
 */
public class JavaClassListReferenceProvider extends JavaClassReferenceProvider {

  public JavaClassListReferenceProvider(final Project project) {
    super(GlobalSearchScope.allScope(project), project);
    setOption(ADVANCED_RESOLVE, Boolean.TRUE);
  }

  @NotNull
  public PsiReference[] getReferencesByString(String str, PsiElement position, int offsetInPosition){
    if (position instanceof XmlTag && ((XmlTag)position).getValue().getTextElements().length == 0) {
      return PsiReference.EMPTY_ARRAY; 
    }

    if (str.length() < 2) {
      return PsiReference.EMPTY_ARRAY;
    }
    NotNullLazyValue<Set<String>> topLevelPackages = new NotNullLazyValue<Set<String>>() {
      @NotNull
      @Override
      protected Set<String> compute() {
        final Set<String> knownTopLevelPackages = new HashSet<String>();
        final List<PsiElement> defaultPackages = getDefaultPackages();
        for (final PsiElement pack : defaultPackages) {
          if (pack instanceof PsiPackage) {
            knownTopLevelPackages.add(((PsiPackage)pack).getName());
          }
        }
        return knownTopLevelPackages;
      }
    };
    final List<PsiReference> results = new ArrayList<PsiReference>();

    for(int dot = str.indexOf('.'); dot > 0; dot = str.indexOf('.', dot + 1)) {
      int start = dot;
      while (start > 0 && Character.isLetterOrDigit(str.charAt(start - 1))) start--;
      if (dot == start) {
        continue;
      }
      String candidate = str.substring(start, dot);
      if (topLevelPackages.getValue().contains(candidate)) {
        int end = dot;
        while (end < str.length() - 1) {
          end++;
          char ch = str.charAt(end);
          if (ch != '.' && !Character.isJavaIdentifierPart(ch)) {
            break;
          }
        }
        String s = str.substring(start, end + 1);
        results.addAll(Arrays.asList(new JavaClassReferenceSet(s, position, offsetInPosition + start, false, this){
          public boolean isSoft(){
            return true;
          }
        }.getAllReferences()));
        ProgressManager.getInstance().checkCanceled();
      }
    }
    return results.toArray(new PsiReference[results.size()]);
  }
}