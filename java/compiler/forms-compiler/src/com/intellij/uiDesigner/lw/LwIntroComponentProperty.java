/*
 * Copyright 2000-2005 JetBrains s.r.o.
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
package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.UIFormXmlConstants;
import org.jdom.Element;

/**
 * @author yole
 */
public class LwIntroComponentProperty extends LwIntrospectedProperty {
  public LwIntroComponentProperty(final String name, final String propertyClassName) {
    super(name, propertyClassName);
  }

  public Object read(Element element) throws Exception {
    return LwXmlReader.getRequiredString(element, UIFormXmlConstants.ATTRIBUTE_VALUE);
  }
}