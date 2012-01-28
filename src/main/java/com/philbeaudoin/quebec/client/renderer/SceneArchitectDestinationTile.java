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

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * An architect destination within a scene graph corresponding to a given tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneArchitectDestinationTile implements SceneArchitectDestination {

  private final ArchitectDestinationTile architectDestinationTile;

  @Inject
  public SceneArchitectDestinationTile(
      @Assisted ArchitectDestinationTile architectDestinationTile) {
    this.architectDestinationTile = architectDestinationTile;
  }

  @Override
  public PlayerColor getArchitectColor() {
    return architectDestinationTile.getArchitectColor();
  }

  @Override
  public Transform removeFrom(GameStateRenderer renderer) {
    return renderer.removeArchitectFromTile(architectDestinationTile.getTile(),
        getArchitectColor());
  }

  @Override
  public Transform addTo(GameStateRenderer renderer) {
    return renderer.addArchitectToTile(architectDestinationTile.getTile(), getArchitectColor());
  }
}
