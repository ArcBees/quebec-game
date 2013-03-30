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

package com.philbeaudoin.quebec.server.user;

import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;

/**
 * Manages user creation and loading from the datastore.
 * @author beaudoin
 */
public interface UserManager {

  /**
   * Uses the provided google authentication code to validate the user through OAuth and then sign
   * him into the session. Signs-out any currently signed-in user. Creates the user in the datastore
   * if it does not already exists.
   * @param code The google authentication code.
   * @return The SessionInfoEntity of the session into which the user is now signed-in.
   * @throws ActionException if authentication failed.
   */
  SessionInfoEntity signIntoSessionWithGoogleAuthenticationCode(String code) throws ActionException;

  // TODO(beaudoin): Get rid of this!
  SessionInfoEntity signIntoSessionWithDummy();

}
