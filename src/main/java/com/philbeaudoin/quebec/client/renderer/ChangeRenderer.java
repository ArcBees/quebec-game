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

package com.philbeaudoin.quebec.client.renderer;

import com.philbeaudoin.quebec.client.scene.SceneNodeList;

/**
 * Renders a {@link com.philbeaudoin.quebec.shared.GameStateChange GameStateChange} into a scene
 * graph. Classes implementing this interface are usually generated using
 * {@link ChangeRendererGenerator}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface ChangeRenderer {
  /**
   * Applies only the removals of a change in-place in the specified scene graph. Follow this call
   * by {@link #applyAdditions} to apply a complete state change.
   * @param renderer The renderer containing the scene graph on which to apply removals.
   */
  void applyRemovals(GameStateRenderer renderer);

  /**
   * Applies only the removals of a change in-place in the specified scene graph. Precede this call
   * by {@link #applyRemovals} to apply a complete state change.
   * @param renderer The renderer containing the scene graph on which to apply additions.
   */
  void applyAdditions(GameStateRenderer renderer);

  /**
   * Undoes only the removals of a change in-place in the specified scene graph. Precede this call
   * by {@link #undoAdditions} to undo a complete state change.
   * @param renderer The renderer containing the scene graph on which to undo removals.
   */
  void undoRemovals(GameStateRenderer renderer);

  /**
   * Applies only the removals of a change in-place in the specified scene graph. Follow this call
   * by {@link #undoRemovals} to undo a complete state change.
   * @param renderer The renderer containing the scene graph on which to undo additions.
   */
  void undoAdditions(GameStateRenderer renderer);

  /**
   * Generates an animation corresponding to a change.
   * @param renderer The renderer containing the scene graph. It will be modifiedduring the call as
   *     if {@link #applyRemovals} and {@link #applyAdditions} had been called.
   * @param animRoot The root of the scene graph to which to add animations.
   * @param startingTime The starting time of the animations to add.
   */
  void generateAnim(GameStateRenderer renderer, SceneNodeList animRoot, double startingTime);
}
