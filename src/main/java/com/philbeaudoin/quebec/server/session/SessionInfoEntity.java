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

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.session.SessionInfo;
import com.philbeaudoin.quebec.shared.user.UserInfo;

@Entity
@Cache
public class SessionInfoEntity implements SessionInfo {
  @Id String id;
  boolean admin;
  @Load Ref<UserInfoEntity> userInfoEntity;

  public SessionInfoEntity() {
    this.admin = false;
  }

  public SessionInfoEntity(String id) {
    this.id = id;
  }

  @Override
  public boolean isAdmin() {
    return admin;
  }

  @Override
  public UserInfo getUserInfo() {
    return getUserInfoEntity();
  }

  /**
   * Keeps the id but resets everything else to default values.
   */
  public void clear() {
    admin = false;
    userInfoEntity = null;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public void setUserInfoEntity(UserInfoEntity userInfoEntity) {
    this.userInfoEntity = Ref.create(userInfoEntity);
  }

  public UserInfoEntity getUserInfoEntity() {
    return userInfoEntity != null ? userInfoEntity.get() : null;
  }

}