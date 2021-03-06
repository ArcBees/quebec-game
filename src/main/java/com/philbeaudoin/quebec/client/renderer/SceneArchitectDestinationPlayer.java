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
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * An architect destination within a player zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneArchitectDestinationPlayer implements SceneArchitectDestination {

  private final ArchitectDestinationPlayer architectDestinationPlayer;

  @Inject
  public SceneArchitectDestinationPlayer(
      @Assisted ArchitectDestinationPlayer architectDestinationPlayer) {
    this.architectDestinationPlayer = architectDestinationPlayer;
  }

  @Override
  public PlayerColor getArchitectColor() {
    return architectDestinationPlayer.getArchitectColor();
  }

  @Override
  public Transform removeFrom(GameStateRenderer renderer) {
    return renderer.removeArchitectFromPlayer(architectDestinationPlayer.getPlayerColor(),
        architectDestinationPlayer.isNeutralArchitect());
  }

  @Override
  public Transform addTo(GameStateRenderer renderer) {
    return renderer.addArchitectToPlayer(architectDestinationPlayer.getPlayerColor(),
        architectDestinationPlayer.isNeutralArchitect());
  }
}
