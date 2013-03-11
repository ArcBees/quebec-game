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

package com.philbeaudoin.quebec.server.oauth;

import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;

/**
 * Manages the google oauth flow.
 * 
 * @author beaudoin
 */
public interface OAuthManager {

  /**
   * Saves the Google OAuth 2.0 client secret in the datastore.
   * @param clientSecret The client secret.
   * @throws OperationNotAllowedException if the user has not enough privileges to set the client
   *     secret.
   */
  void saveClientSecret(String clientSecret) throws OperationNotAllowedException;

  /**
   * Complete the authentication process with the Google OAuth 2.0 service.
   * @param code The code
   * @return User information on the authenticated user.
   * @throws ActionException if authentication failed.
   */
  UserInfoEntity authenticate(String code) throws ActionException;

}
