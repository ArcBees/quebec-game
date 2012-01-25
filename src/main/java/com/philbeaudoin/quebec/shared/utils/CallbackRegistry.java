/**
 * Copyright 2011 Philippe Beaudoin
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

import java.util.ArrayList;

/**
 * An holder for a group of callbacks that should be triggered together, most likely on a single
 * given event.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class CallbackRegistry {
  private final ArrayList<Callback> callbacks = new ArrayList<Callback>();

  // The fields below are used for deferred unregistration logic.
  private final ArrayList<Callback> deferredUnregistrations = new ArrayList<Callback>();
  private boolean executionInProgress;

  /**
   * Executes all the callbacks contained in this registry.
   */
  public void executeAll() {
    if (executionInProgress) {
      return;
    }
    executionInProgress = true;
    for (Callback callback : callbacks) {
      callback.execute();
    }
    executionInProgress = false;
    for (Callback callback : deferredUnregistrations) {
      unregister(callback);
    }
    deferredUnregistrations.clear();
  }

  /**
   * Registers a given callback with this registry, so that it is executed when
   * {@link #executeAll()} is called.
   * @param callback The callback to register.
   * @return A callback registration object that can be used to unregister the callback.
   */
  public CallbackRegistration register(Callback callback) {
    callbacks.add(callback);
    return new CallbackRegistration(this, callback);
  }

  /**
   * Unregisters a callback from the registry. It is usually more convenient to use the
   * {@link CallbackRegistration} object returned by {@link #register} to do this. Nothing happens
   * if the specified callback is currently not registered. If unregistration happens while
   * callbacks are being executed, then it is deferred until execution has completed.
   * @param callback The callback to unregister.
   */
  public void unregister(Callback callback) {
    if (executionInProgress) {
      deferredUnregistrations.add(callback);
    } else {
      callbacks.remove(callback);
    }
  }

  /**
   * Unregisters all the callbacks contained in the registry. If unregistration happens while
   * callbacks are being executed, then it is deferred until execution has completed.
   */
  public void unregisterAll() {
    if (executionInProgress) {
      deferredUnregistrations.addAll(callbacks);
    } else {
      callbacks.clear();
    }
  }
}
