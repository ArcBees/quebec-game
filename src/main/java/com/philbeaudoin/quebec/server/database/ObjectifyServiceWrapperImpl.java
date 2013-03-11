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

package com.philbeaudoin.quebec.server.database;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;
import com.philbeaudoin.quebec.server.user.GoogleUserEntity;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;

public class ObjectifyServiceWrapperImpl implements ObjectifyServiceWrapper {
  static {
    ObjectifyService.register(GlobalStringEntity.class);
    ObjectifyService.register(SessionInfoEntity.class);
    ObjectifyService.register(UserInfoEntity.class);
    ObjectifyService.register(GoogleUserEntity.class);
  }

  @Override
  public Objectify ofy() {
    return ObjectifyService.ofy();
  }
}
