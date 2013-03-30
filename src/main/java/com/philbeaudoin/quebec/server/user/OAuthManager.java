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

import java.io.IOException;

import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;

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
   * Complete the authentication process with the Google OAuth 2.0 service. The returned user is
   * not stored in the datastore.
   * @param code The google authentication code
   * @return User information on the authenticated user, it is not yet stored in the datastore.
   * @throws ActionException if authentication failed.
   */
  UserInfoEntity authenticate(String code) throws ActionException;

  /**
   * Takes an old and a new Google token response and merge their information. Keeps all the
   * information from the newest response, save maybe for the refresh token. If the refresh token
   * is null in the newest response and non-null in the older response, then the one from the older
   * response is kept.
   * @param oldestString Older token response, that may have a valid refresh token. Can be null.
   * @param newest Newer token response, that may have a null refresh token.
   * @return The best possible Google token response.
   * @throws IOException If the json cannot be converted.
   */
  String mergeTokenResponses(String oldestString, String newestString) throws IOException;

}
