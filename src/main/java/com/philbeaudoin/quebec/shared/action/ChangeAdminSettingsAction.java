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
public class ChangeAdminSettingsAction extends ActionImpl<VoidResult> {

  private String adminPassword;
  private String salt;
  private String googleOAuthClientSecret;

  public ChangeAdminSettingsAction(final String adminPassword, String salt,
      String googleOAuthClientSecret) {
    this.adminPassword = adminPassword;
    this.salt = salt;
    this.googleOAuthClientSecret = googleOAuthClientSecret;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ChangeAdminSettingsAction() {
  }

  public String getAdminPassword() {
    return adminPassword;
  }

  public String getSalt() {
    return salt;
  }

  public String getGoogleOAuthClientSecret() {
    return googleOAuthClientSecret;
  }
}
