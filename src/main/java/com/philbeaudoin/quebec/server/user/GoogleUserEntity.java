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

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

/**
 * Connects a Google id to its {@link UserInfoEntity} for reverse look-up.
 * @author beaudoin
 */
@Entity
@Cache
public class GoogleUserEntity {
  @Id String googleId;
  @Load Ref<UserInfoEntity> userInfoEntity;

  public UserInfoEntity getUserInfoEntity() {
    return userInfoEntity != null ? userInfoEntity.get() : null;
  }

  public String getGoogleId() {
    return googleId;
  }

  public static GoogleUserEntity Create(String googleId, UserInfoEntity userInfoEntity) {
    GoogleUserEntity result = new GoogleUserEntity();
    result.googleId = googleId;
    result.userInfoEntity = Ref.create(userInfoEntity);
    return result;
  }
}
