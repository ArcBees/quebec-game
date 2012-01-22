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

import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A cube destination within a scene graph corresponding to the active or passive cubes of a given
 * player color.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneCubeDestinationPlayer implements SceneCubeDestination {

  private final CubeDestinationPlayer cubeDestinationPlayer;

  @Inject
  public SceneCubeDestinationPlayer(
      @Assisted CubeDestinationPlayer cubeDestinationPlayer) {
    this.cubeDestinationPlayer = cubeDestinationPlayer;
  }

  @Override
  public PlayerColor getPlayerColor() {
    return cubeDestinationPlayer.getPlayerColor();
  }

  @Override
  public List<Transform> removeFrom(int nbCubes, GameStateRenderer renderer) {
    return renderer.removeCubesFromPlayer(getPlayerColor(), cubeDestinationPlayer.isActive(),
        nbCubes);
  }

  @Override
  public List<Transform> addTo(int nbCubes, GameStateRenderer renderer) {
    return renderer.addCubesToPlayer(getPlayerColor(), cubeDestinationPlayer.isActive(), nbCubes);
  }
}
