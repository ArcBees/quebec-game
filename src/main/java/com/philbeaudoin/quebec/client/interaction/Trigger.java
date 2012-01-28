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

package com.philbeaudoin.quebec.client.interaction;

/**
 * A class that can be used to check whether a given mouse location should trigger something,
 * usually an interaction.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface Trigger {
  /**
   * Indicates whether this trigger should be active at a given mouse location.
   * @param x The x location at which to check the trigger.
   * @param y The y location at which to check the trigger.
   */
  boolean triggerAt(double x, double y);
}
