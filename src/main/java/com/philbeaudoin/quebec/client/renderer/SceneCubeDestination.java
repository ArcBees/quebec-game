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

import java.util.List;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A valid place to take or send cubes within a scene graph. The destination implies a color of
 * cubes. The render-side equivalent of
 * {@link com.philbeaudoin.quebec.shared.statechange.CubeDestination CubeDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface SceneCubeDestination {

  /**
   * @return The color of the player associated with that destination.
   */
  PlayerColor getPlayerColor();

  /**
   * Remove cubes from that specific destination in the provided scene graph.
   * @param nbCubes The number of cubes to remove.
   * @param renderer The renderer containing the scene graph from which to remove cubes.
   * @return The global transforms of the cubes removed from the scene graph.
   */
  List<Transform> removeFrom(int nbCubes, GameStateRenderer renderer);

  /**
   * Add cubes to this specific destination in the provided scene graph.
   * @param nbCubes The number of cubes to add.
   * @param renderer The renderer containing the scene graph onto which to add cubes.
   * @return The global transforms of the cubes added to the scene graph.
   */
  List<Transform> addTo(int nbCubes, GameStateRenderer renderer);
}
