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

import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A class containing various static helper methods.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class Helpers {

  /**
   * Create an arrow to indicate moving an architect from its current location to another.
   * @param gameState The game state that will be used to determine the architect location and the
   *     current player color.
   * @param gameStateRenderer The renderer for the current game state.
   * @param action The move architect action for which to render an arrow.
   * @return The arrow depicting the move.
   */
  public static SceneNode createArchitectArrow(GameState gameState,
      GameStateRenderer gameStateRenderer, ActionMoveArchitect action) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    PlayerColor architectColor = action.isNeutralArchitect() ? PlayerColor.NEUTRAL : playerColor;
    TileState origin = gameState.findTileUnderArchitect(architectColor);
    Transform architectFrom;
    if (origin == null) {
      // Architect starts from the player zone.
      architectFrom = gameStateRenderer.getArchitectOnPlayerTransform(
          playerColor, action.isNeutralArchitect());
    } else {
      // Architect starts from its current tile.
      architectFrom = gameStateRenderer.getArchitectSlotOnTileTransform(origin.getTile());
    }

    // Arrow to move architect.
    Transform architectTo = gameStateRenderer.getArchitectSlotOnTileTransform(
        action.getDestinationTile());
    return new Arrow(architectFrom.getTranslation(0), architectTo.getTranslation(0));
  }

}
