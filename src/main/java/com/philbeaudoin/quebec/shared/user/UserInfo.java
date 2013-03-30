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

package com.philbeaudoin.quebec.shared.user;

/**
 * All the data corresponding to a user.
 * 
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface UserInfo {
  /**
   * @return the unique user ID, or -1 if it is not set yet.
   */
  long getId();

  /**
   * @return the Google user ID.
   */
  String getGoogleId();

  /**
   * @return the user's email.
   */
  String getEmail();

  /**
   * @return the user's full name.
   */
  String getName();
}
