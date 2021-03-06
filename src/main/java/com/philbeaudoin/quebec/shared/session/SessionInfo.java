/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.session;

import com.philbeaudoin.quebec.shared.user.UserInfo;

/**
 * All the data corresponding to the current session.
 * 
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface SessionInfo {
  /**
   * Return true if the session user has authenticated with the admin password. Not that this does
   * not mean that {@link #isSignedIn()} would return true. A user can be an admin without having
   * authenticated with an account.
   * @return {@code true} if the user is an admin.
   */
  boolean isAdmin();

  /**
   * @return {@code true} if the user has signed in. Equivalent to checking that
   *     {@link #getUserInfo()} returns non-null.
   */
  boolean isSignedIn();

  /**
   * @return The currently signed in user info, or {@code null} if no user is signed in.
   */
  UserInfo getUserInfo();
}
