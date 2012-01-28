/**
 * Copyright 2012 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.utils;

/**
 * An object that makes it easy to unregister a given callback.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CallbackRegistration {

  private final CallbackRegistry registry;
  private final Callback callback;

  CallbackRegistration(CallbackRegistry registry, Callback callback) {
    this.registry = registry;
    this.callback = callback;
  }

  /**
   * Unregisters the callback so that it is no longer invoked.
   */
  public void unregister() {
    registry.unregister(callback);
  }
}
