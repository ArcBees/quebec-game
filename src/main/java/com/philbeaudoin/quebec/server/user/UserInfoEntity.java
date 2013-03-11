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

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.philbeaudoin.quebec.shared.user.UserInfo;

@Entity
@Cache
public class UserInfoEntity implements UserInfo {
  @Id Long id;
  String googleId;
  String googleTokenResponse;

  public Long getId() {
    return id;
  }

  @Override
  public String getGoogleId() {
    return googleId;
  }

  private UserInfoEntity() {
  }

  public static UserInfoEntity Create(String googleId) {
    UserInfoEntity result = new UserInfoEntity();
    result.googleId = googleId;
    return result;
  }

  public String getGoogleTokenResponse() {
    return googleTokenResponse;
  }

  public void setGoogleTokenResponse(String googleTokenResponse) {
    this.googleTokenResponse = googleTokenResponse;
  }
}
