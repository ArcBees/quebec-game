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

package com.philbeaudoin.quebec.shared.action;

import com.gwtplatform.dispatch.shared.ActionImpl;

/**
 * Sends the admin password.
 * @author beaudoin
 */
public class AuthenticateWithAdminPasswordAction extends ActionImpl<SessionInfoResult> {

  private String adminPassword;

  public AuthenticateWithAdminPasswordAction(final String adminPassword) {
    this.adminPassword = adminPassword;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private AuthenticateWithAdminPasswordAction() {
  }

  public String getAdminPassword() {
    return adminPassword;
  }
}
