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

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Entity for global strings stored in the datastore.
 */
@Entity
@Cache
public class GlobalStringEntity {

  public static final long SALT_ID = 1L;
  public static final long ADMIN_PASSWORD_ID = 2L;
  public static final long GOOGLE_OAUTH_CLIENT_SECRET = 3L;

  @Id Long id;
  String string;

  public GlobalStringEntity() {
  }

  public GlobalStringEntity(long id, String string) {
    this.id = new Long(id);
    this.string = new String(string);
  }

  public String getString() {
    return string;
  }
}