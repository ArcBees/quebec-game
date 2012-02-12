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
 * A target element that can be selected during an interaction.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface InteractionTarget {

  /**
   * Highlights the target.
   */
  void highlight();

  /**
   * Call this when the mouse enters the target.
   * @param time The time at which the mouse entered the target.
   */
  void onMouseEnter(double time);

  /**
   * Call this when the mouse exits the target.
   * @param time The time at which the mouse exited the target.
   */
  void onMouseLeave(double time);

  /**
   * Returns the trigger associated with this interaction target.
   * @return The trigger.
   */
  Trigger getTrigger();
}
