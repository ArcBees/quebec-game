/**
 * Copyright 2012 Philippe Beaudoin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philbeaudoin.quebec.shared;

/**
 * The central location of all name tokens for the application. All {@link ProxyPlace} classes get
 * their tokens from here. This class also makes it easy to use name tokens as a resource within
 * UIBinder xml files.
 * <p />
 * This class uses both static variables and static getters. The reason for this is that, if you
 * want to use {@code com.gwtplatform.mvp.client.annotations.NameTokens} in a UiBinder file, you can
 * only access static methods of the class. On the other hand, when you use the
 * {@literal @}{@link com.gwtplatform.mvp.client.annotations.NameToken} annotation, you can only
 * refer to a static variable.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class NameTokens {

  public static final String mainPage = "!main";
  public static String getMainPage() {
    return mainPage;
  }
}
