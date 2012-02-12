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
 * This is the basic implementation of an interaction associated with a specific trigger.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class InteractionImpl implements Interaction {

  private boolean inside;
  private final InteractionTarget target;

  public InteractionImpl(InteractionTarget target) {
    this.target = target;
  }

  @Override
  public void onMouseMove(double x, double y, double time) {
    if (target.getTrigger().triggerAt(x, y)) {
      doMouseMove(x, y, time);
      if (!inside) {
        doMouseEnter(x, y, time);
        inside = true;
      }
    } else if (inside) {
      inside = false;
      doMouseLeave(x, y, time);
    }
  }

  @Override
  public void highlight() {
    target.highlight();
  }

  /**
   * Access the trigger of this interaction.
   * @return The trigger of the interaction.
   */
  protected Trigger getTrigger() {
    return target.getTrigger();
  }

  /**
   * Performs any graphical additions that should be performed when the mouse is moving inside the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseMove(double x, double y, double time) {
  }

  /**
   * Performs any graphical additions that should be performed when the mouse enters the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseEnter(double x, double y, double time) {
    target.onMouseEnter(time);
  }

  /**
   * Performs any graphical additions that should be performed when the mouse leaves the
   * interaction area.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  protected void doMouseLeave(double x, double y, double time) {
    target.onMouseLeave(time);
  }
}