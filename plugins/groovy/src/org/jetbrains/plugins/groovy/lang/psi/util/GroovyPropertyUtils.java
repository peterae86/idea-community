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
package org.jetbrains.plugins.groovy.lang.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.modifiers.GrModifier;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrAccessorMethod;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ilyas
 */
public class GroovyPropertyUtils {
  public static final String IS_PREFIX = "is";
  public static final String GET_PREFIX = "get";
  public static final String SET_PREFIX = "set";

  private GroovyPropertyUtils() {
  }

  @Nullable
  public static PsiMethod findSetterForField(PsiField field) {
    final PsiClass containingClass = field.getContainingClass();
    final Project project = field.getProject();
    final String propertyName = PropertyUtil.suggestPropertyName(project, field);
    final boolean isStatic = field.hasModifierProperty(PsiModifier.STATIC);
    return findPropertySetter(containingClass, propertyName, isStatic, true);
  }

  @Nullable
  public static PsiMethod findGetterForField(PsiField field) {
    final PsiClass containingClass = field.getContainingClass();
    final Project project = field.getProject();
    final String propertyName = PropertyUtil.suggestPropertyName(project, field);
    final boolean isStatic = field.hasModifierProperty(PsiModifier.STATIC);
    return PropertyUtil.findPropertyGetter(containingClass, propertyName, isStatic, true);
  }

  @Nullable
  public static PsiMethod findPropertySetter(PsiClass aClass, String propertyName, boolean isStatic, boolean checkSuperClasses) {
    if (aClass == null) return null;
    PsiMethod[] methods;
    if (checkSuperClasses) {
      methods = aClass.getAllMethods();
    }
    else {
      methods = aClass.getMethods();
    }

    for (PsiMethod method : methods) {
      if (method.hasModifierProperty(PsiModifier.STATIC) != isStatic) continue;

      if (isSimplePropertySetter(method)) {
        if (propertyName.equals(getPropertyNameBySetter(method))) {
          return method;
        }
      }
    }

    return null;
  }

  /**
   * @deprecated use PropertyUtil.getAllProperties() instead.
   */
  @Deprecated()
  public static List<PsiMethod> getAllPropertyGetters(@NotNull PsiClass aClass, @Nullable Boolean isStatic, boolean checkSuperClasses) {
    PsiMethod[] methods;
    if (checkSuperClasses) {
      methods = aClass.getAllMethods();
    }
    else {
      methods = aClass.getMethods();
    }

    List<PsiMethod> res = new ArrayList<PsiMethod>(methods.length);

    for (PsiMethod method : methods) {
      if (isStatic != null && method.hasModifierProperty(PsiModifier.STATIC) != isStatic) continue;

      if (isSimplePropertyGetter(method)) {
        res.add(method);
      }
    }

    return res;
  }

  @Nullable
  public static PsiMethod findPropertyGetter(@Nullable PsiClass aClass,
                                             String propertyName,
                                             @Nullable Boolean isStatic,
                                             boolean checkSuperClasses) {
    if (aClass == null) return null;
    PsiMethod[] methods;
    if (checkSuperClasses) {
      methods = aClass.getAllMethods();
    }
    else {
      methods = aClass.getMethods();
    }

    for (PsiMethod method : methods) {
      if (isStatic != null && method.hasModifierProperty(PsiModifier.STATIC) != isStatic) continue;

      if (isSimplePropertyGetter(method)) {
        if (propertyName.equals(getPropertyNameByGetter(method))) {
          return method;
        }
      }
    }

    return null;
  }

  public static boolean isSimplePropertyAccessor(PsiMethod method) {
    return isSimplePropertyGetter(method) || isSimplePropertySetter(method);
  }//do not check return type

  public static boolean isSimplePropertyGetter(PsiMethod method) {
    return isSimplePropertyGetter(method, null);
  }//do not check return type

  public static boolean isSimplePropertyGetter(PsiMethod method, String propertyName) {
    return isSimplePropertyGetter(method, propertyName, false);
  }

  public static boolean isSimplePropertyGetter(PsiMethod method,
                                               String propertyName,
                                               boolean isUsedInCategory) {
    if (method == null || method.isConstructor()) return false;
    if (isUsedInCategory) {
      if (method.getParameterList().getParametersCount() != 1) return false;
    } else {
      if (method.getParameterList().getParametersCount() != 0) return false;
    }
    if (!isGetterName(method.getName())) return false;
    if (method.getName().startsWith(IS_PREFIX) && !PsiType.BOOLEAN.equals(method.getReturnType())) {
      return false;
    }
    return (propertyName == null || propertyName.equals(getPropertyNameByGetter(method))) && method.getReturnType() != PsiType.VOID;
  }

  public static boolean isSimplePropertySetter(PsiMethod method) {
    return isSimplePropertySetter(method, null);
  }

  public static boolean isSimplePropertySetter(PsiMethod method, String propertyName) {
    return isSimplePropertySetter(method, propertyName, false);
  }

  public static boolean isSimplePropertySetter(PsiMethod method,
                                               String propertyName,
                                               boolean isUsedInCategory) {
    if (method == null || method.isConstructor()) return false;
    if (isUsedInCategory) {
      if (method.getParameterList().getParametersCount() != 2) return false;
    } else {
      if (method.getParameterList().getParametersCount() != 1) return false;
    }

    if (!isSetterName(method.getName())) return false;
    return propertyName == null || propertyName.equals(getPropertyNameBySetter(method));
  }

  @Nullable
  public static String getPropertyNameByGetter(PsiMethod getterMethod) {
    if (getterMethod instanceof GrAccessorMethod) {
      return ((GrAccessorMethod)getterMethod).getProperty().getName();
    }

    @NonNls String methodName = getterMethod.getName();
    final boolean isPropertyBoolean = PsiType.BOOLEAN.equals(getterMethod.getReturnType());
    return getPropertyNameByGetterName(methodName, isPropertyBoolean);
  }

  @Nullable
  public static String getPropertyNameByGetterName(String methodName, boolean canBeBoolean) {
    if (methodName.startsWith(GET_PREFIX) && methodName.length() > 3) {
      return decapitalize(methodName.substring(3));
    }
    if (canBeBoolean && methodName.startsWith(IS_PREFIX) && methodName.length() > 2) {
      return decapitalize(methodName.substring(2));
    }
    return null;
  }

  @Nullable
  public static String getPropertyNameBySetter(PsiMethod setterMethod) {
    if (setterMethod instanceof GrAccessorMethod) {
      return ((GrAccessorMethod)setterMethod).getProperty().getName();
    }

    @NonNls String methodName = setterMethod.getName();
    return getPropertyNameBySetterName(methodName);
  }

  @Nullable
  public static String getPropertyNameBySetterName(String methodName) {
    if (methodName.startsWith(SET_PREFIX) && methodName.length() > 3) {
      return StringUtil.decapitalize(methodName.substring(3));
    }
    else {
      return null;
    }
  }

  @Nullable
  public static String getPropertyNameByAccessorName(String accessorName) {
    if (isGetterName(accessorName)) {
      return getPropertyNameByGetterName(accessorName, true);
    }
    else if (isSetterName(accessorName)) {
      return getPropertyNameBySetterName(accessorName);
    }
    return null;
  }

  @Nullable
  public static String getPropertyName(PsiMethod accessor) {
    if (isSimplePropertyGetter(accessor)) return getPropertyNameByGetter(accessor);
    if (isSimplePropertySetter(accessor)) return getPropertyNameBySetter(accessor);
    return null;
  }

  public static boolean isGetterName(@NotNull String name) {
    int prefixLength;
    if (name.startsWith(GET_PREFIX)) {
      prefixLength = 3;
    }
    else if (name.startsWith(IS_PREFIX)) {
      prefixLength = 2;
    }
    else {
      return false;
    }

    if (name.length() == prefixLength) return false;

    if (isUpperCase(name.charAt(prefixLength))) return true;

    return name.length() > prefixLength + 1 && isUpperCase(name.charAt(prefixLength + 1));
  }

  public static String getGetterNameNonBoolean(@NotNull String name) {
    return getAccessorName(GET_PREFIX, name);
  }

  public static String getGetterNameBoolean(@NotNull String name) {
    return getAccessorName(IS_PREFIX, name);
  }

  public static String getSetterName(@NotNull String name) {
    return getAccessorName("set", name);
  }

  private static String getAccessorName(String prefix, String name) {
    if (name.length() == 0) return prefix;

    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    if (name.length() > 1 && isUpperCase(name.charAt(1))) {
      sb.append(name);
    }
    else {
      sb.append(Character.toUpperCase(name.charAt(0)));
      sb.append(name, 1, name.length());
    }

    return sb.toString();
  }

  /**
   * Returns getter names in priority order
   * @param name property name
   * @return getter names
   */
  public static String[] suggestGettersName(@NotNull String name) {
    return new String[]{getGetterNameBoolean(name), getGetterNameNonBoolean(name)};
  }

  public static boolean isPropertyName(String name) {
    if (name.length() == 0) return false;
    if (Character.isUpperCase(name.charAt(0)) && (name.length() == 1 || !Character.isUpperCase(name.charAt(1)))) return false;
    return true;
  }

  public static String[] suggestSettersName(@NotNull String name) {
    return new String[]{getSetterName(name)};
  }

  public static boolean isSetterName(String name) {
    return name != null
           && name.startsWith(SET_PREFIX)
           && name.length() > 3
           && (isUpperCase(name.charAt(3)) || (name.length() > 4 && isUpperCase(name.charAt(3))));
  }

  public static boolean isProperty(@Nullable PsiClass aClass, @Nullable String propertyName, boolean isStatic) {
    if (aClass == null || propertyName == null) return false;
    final PsiField field = aClass.findFieldByName(propertyName, true);
    if (field instanceof GrField && ((GrField)field).isProperty() && field.hasModifierProperty(PsiModifier.STATIC) == isStatic) return true;

    final PsiMethod getter = findPropertyGetter(aClass, propertyName, isStatic, true);
    if (getter != null && getter.hasModifierProperty(PsiModifier.PUBLIC)) return true;

    final PsiMethod setter = findPropertySetter(aClass, propertyName, isStatic, true);
    return setter != null && setter.hasModifierProperty(PsiModifier.PUBLIC);
  }

  public static boolean isProperty(GrField field) {
    final PsiClass clazz = field.getContainingClass();
    return isProperty(clazz, field.getName(), field.hasModifierProperty(PsiModifier.STATIC));
  }

  private static boolean isUpperCase(char c) {
    return Character.toUpperCase(c) == c;
  }

  /*public static boolean canBePropertyName(String name) {
    return !(name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isLowerCase(name.charAt(0)));
  }*/

  public static String capitalize(String s) {
    if (s.length() == 0) return s;
    if (s.length() == 1) return s.toUpperCase();
    if (isUpperCase(s.charAt(1))) return s;
    final char[] chars = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static String decapitalize(String s) {
    return Introspector.decapitalize(s);
  }

  @Nullable
  public static PsiField findFieldForAccessor(PsiMethod accessor, boolean checkSuperClasses) {
    final PsiClass psiClass = accessor.getContainingClass();
    if (psiClass == null) return null;
    PsiField field = null;
    if (!checkSuperClasses) {
      field = psiClass.findFieldByName(getPropertyNameByAccessorName(accessor.getName()), true);
    }
    else {
      final String name = getPropertyNameByAccessorName(accessor.getName());
      assert name != null;
      final PsiField[] allFields = psiClass.getAllFields();
      for (PsiField psiField : allFields) {
        if (name.equals(psiField.getName())) {
          field = psiField;
          break;
        }
      }
    }
    if (field == null) return null;
    if (field.hasModifierProperty(GrModifier.STATIC) == accessor.hasModifierProperty(GrModifier.STATIC)) {
      return field;
    }
    return null;
  }

  @Nullable
  public static String getGetterPrefix(PsiMethod getter) {
    final String name = getter.getName();
    if (name.startsWith(GET_PREFIX)) return GET_PREFIX;
    if (name.startsWith(IS_PREFIX)) return IS_PREFIX;

    return null;
  }

  @Nullable
  public static String getSetterPrefix(PsiMethod setter) {
    if (setter.getName().startsWith(SET_PREFIX)) return SET_PREFIX;
    return null;
  }

  @Nullable
  public static String getAccessorPrefix(PsiMethod method) {
    final String prefix = getGetterPrefix(method);
    if (prefix != null) return prefix;

    return getSetterPrefix(method);
  }

  public static boolean isAccessorFor(PsiMethod accessor, PsiField field) {
    final String accessorName = accessor.getName();
    final String fieldName = field.getName();
    if (!ArrayUtil.contains(accessorName, suggestGettersName(fieldName)) &&
        !ArrayUtil.contains(accessorName, suggestSettersName(fieldName))) {
      return false;
    }
    final PsiClass accessorClass = accessor.getContainingClass();
    final PsiClass fieldClass = field.getContainingClass();
    if (!field.getManager().areElementsEquivalent(accessorClass, fieldClass)) return false;
    return accessor.hasModifierProperty(GrModifier.STATIC) == field.hasModifierProperty(GrModifier.STATIC);
  }
}
