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
package com.intellij.cvsSupport2.connections;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NonNls;

/**
 * author: lesya
 */
public class CvsMethod {

  public static final CvsMethod EXT_METHOD = new CvsMethod("ext", true, true, true, false);
  public static final CvsMethod PSERVER_METHOD = new CvsMethod("pserver", true, true, true, true);
  public static final CvsMethod SSH_METHOD = new CvsMethod("ssh", com.intellij.CvsBundle.message("cvs.root.description.ssh.internal.implementation"), true, true, true, true);

  public static final CvsMethod LOCAL_METHOD = new CvsMethod(null, com.intellij.CvsBundle.message("cvs.root.description.local"), false, false, false, false);

  private final String myName;
  private final String myDescription;
  public static CvsMethod[] AVAILABLE_METHODS =
    new CvsMethod[]{PSERVER_METHOD,
                    EXT_METHOD,
                    SSH_METHOD,
                    LOCAL_METHOD};

  private boolean myHasUserValue;
  private boolean myHasHostValue;
  private boolean myHasPortValue;
  private boolean mySupportsProxyConnection;


  public CvsMethod(@NonNls String name,
                   String description,
                   boolean hasUserValue,
                   boolean hasHostValue,
                   boolean hasPortValue,
                   boolean supportsProxy) {
    myName = name;
    myDescription = description;
    myHasUserValue = hasUserValue;
    myHasHostValue = hasHostValue;
    myHasPortValue = hasPortValue;
    mySupportsProxyConnection = supportsProxy;
  }

  public CvsMethod(@NonNls String name,
                   boolean hasUserValue,
                   boolean hasHostValue,
                   boolean hasPortValue,
                   boolean supportsProxy) {
    this(name, name, hasUserValue, hasHostValue, hasPortValue, supportsProxy);
  }


  public String toString() {
    return myDescription;
  }

  public String getName() {
    return myName;
  }

  public String getDisplayName() {
    return myDescription;
  }

  public static CvsMethod getValue(String method) {
    CvsMethod[] availableMethods = AVAILABLE_METHODS;
    for (CvsMethod availableMethod : availableMethods) {
      if (Comparing.equal(availableMethod.getName(), method)) return availableMethod;
    }
    return null;
  }

  public boolean hasUserValue() {
    return myHasUserValue;
  }

  public boolean hasHostValue() {
    return myHasHostValue;
  }

  public boolean hasPortValue() {
    return myHasPortValue;
  }

  public boolean supportsProxyConnection() {
    return mySupportsProxyConnection;
  }
}
