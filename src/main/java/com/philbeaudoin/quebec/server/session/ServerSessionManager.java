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

package com.philbeaudoin.quebec.server.session;

import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;

/**
 * Manages information relative to the current session on the server.
 * 
 * @author beaudoin
 */
public interface ServerSessionManager {

  /**
   * Returns the current session info. If the session exists in the datastore it is returned,
   * otherwise a new one is created and stored. 
   * @return The current session's info.
   */
  SessionInfoEntity getSessionInfo();

  /**
   * Retrieves the current session entity given the user provided admin password. If the session
   * exists in the datastore it is returned, otherwise a new one is created and stored. If the admin
   * password matches, that session has the admin flag set to true, otherwise it is reset to false.
   * @param inputPassword The admin password sent by the user.
   * @return The current session's info, with isAdmin set correctly depending on the password.
   */
  SessionInfoEntity getSessionInfoEntityGivenAdminPassword(String inputPassword);

  /**
   * Attaches the specified user information to the current session and save it in the datastore.
   * @param userInfoEntity The user information to attach to this session.
   * @return The current session's info, with the user info attached.
   */
  SessionInfoEntity attachUserInfoToSession(UserInfoEntity userInfoEntity);

  /**
   * Completely sign out the current session.
   * @return A cleared session entity.
   */
  SessionInfoEntity signOut();

  /**
   * Ensures the current session is not considered an admin session.
   * @return The current session's info, with isAdmin guaranteed to be false.
   */
  SessionInfoEntity signOutAdmin();

  /**
   * Changes the current admin password.
   * @param inputPassword The new desired admin password.
   * @throws OperationNotAllowedException if the user has not enough privileges to change the admin
   *     password.
   */
  void saveAdminPassword(String inputPassword) throws OperationNotAllowedException;

  /**
   * Changes the current admin password and the salt. Be careful, this will invalidate all other
   * hashed information in the datastore.
   * @param inputPassword The new desired admin password.
   * @param salt The new desired salt.
   * @throws OperationNotAllowedException if the user has not enough privileges to change the admin
   *     password.
   */
  void saveAdminPasswordAndSalt(String inputPassword, String salt)
      throws OperationNotAllowedException;
}
