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

package com.philbeaudoin.quebec.client.renderer;

/**
 * Renders a {@link com.philbeaudoin.quebec.shared.game.statechange.GameStateChange GameStateChange} into
 * a scene graph. Classes implementing this interface are usually generated using
 * {@link ChangeRendererGenerator}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface ChangeRenderer {

  /**
   * Adds and removes any component that would be added to the scene graph itself by
   * {@link #generateAnim}. This does not add anything that {@link #generateAnim} adds to the
   * animation graph. Use this to simulate an "instantaneous" animation.
   * @param renderer The renderer containing the scene graph.
   */
  void applyAnimChanges(GameStateRenderer renderer);

  /**
   * Removes any component added to the main scene graph by this change renderer.
   * @param renderer The renderer containing the scene graph.
   */
  void undoAdditions(GameStateRenderer renderer);

  /**
   * Generates an animation corresponding to a change.
   * @param renderer The renderer containing the scene graph. It will be modified during the call.
   *     you should call {@link #undoAdditions(GameStateRenderer)} to remove the newly added
   *     components.
   * @param startingTime The starting time of the animations to add.
   */
  void generateAnim(GameStateRenderer renderer,  double startingTime);
}
