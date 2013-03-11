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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.philbeaudoin.quebec.shared.user.UserInfo;
import com.philbeaudoin.quebec.shared.user.UserInfoDto;

public class SessionInfoDto implements SessionInfo, IsSerializable {
  boolean admin;
  UserInfoDto userInfoDto;

  public SessionInfoDto(SessionInfo sessionInfo) {
    this.admin = sessionInfo.isAdmin();
    this.userInfoDto = new UserInfoDto(sessionInfo.getUserInfo());
  }

  public SessionInfoDto() {
    this.admin = false;
    this.userInfoDto = null;
  }

  @Override
  public boolean isAdmin() {
    return admin;
  }

  @Override
  public UserInfo getUserInfo() {
    return userInfoDto;
  }

}