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

import com.philbeaudoin.quebec.shared.state.GameController;

/**
 * This interface indicates a possible interaction the user can have with the game board. It allows
 * for effects when the mouse moves over an object or when a click is detected.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface Interaction {
  /**
   * Called when all the components required by this interaction should be highlighted.
   */
  void highlight();

  /**
   * Indicates that the mouse has moved and that the interaction should be executed if it is
   * triggered.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  void onMouseMove(double x, double y, double time);

  /**
   * Indicates that the mouse has been clicked and that the interaction should be executed if it is
   * triggered. The game state will be modified.
   * @param gameController The current game controller.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  void onMouseClick(GameController gameController, double x, double y, double time);
}
