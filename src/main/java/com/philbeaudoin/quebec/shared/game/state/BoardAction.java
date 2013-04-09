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

package com.philbeaudoin.quebec.shared.game.state;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Information for one of the board actions available in the game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public abstract class BoardAction implements IsSerializable {
  private Vector2d location;
  private InfluenceType influenceType;
  private int cubesPerSpot;
  private ActionType actionType;

  /**
   * Create information for one of the actions in the game.
   *
   * @param column The column of the action spot on the board.
   * @param line The line of the action spot on the board.
   * @param influenceType The type (color) of the influence for this action.
   * @param cubesPerSpot The number of cubes for each spot on a building associated with this
   *     action.
   * @param actionType The type of action of this board action.
   */
  protected BoardAction(int column, int line, InfluenceType influenceType, int cubesPerSpot,
      ActionType actionType) {
    this.location = new Vector2d(column, line);
    this.influenceType = influenceType;
    this.cubesPerSpot = cubesPerSpot;
    this.actionType = actionType;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private BoardAction() {
  }

  /**
   * @return The location of the action spot on the board, an integer vector.
   */
  public Vector2d getLocation() {
    return location;
  }

  /**
   * @return The type (color) of the influence for this action.
   */
  public InfluenceType getInfluenceType() {
    return influenceType;
  }

  /**
   * @return The number of cubes for each spot on a building associated with this action.
   */
  public int getCubesPerSpot() {
    return cubesPerSpot;
  }

  /**
   * @return The type of this board action.
   */
  public ActionType getActionType() {
    return actionType;
  }

  /**
   * Return the list of all possible game actions that can be executed as a result of performing
   * this board action, or null if no action can be executed.
   * @param gameController current game controller.
   * @param gameState The current game state.
   * @param triggeringTile The tile that was used to trigger this board action.
   * @return The list of possible actions, or null.
   */
  public abstract PossibleActions getPossibleActions(GameController gameController,
      GameState gameState, Tile triggeringTile);

  /**
   * Returns a text describing this board action.
   * @return The description message.
   */
  public abstract Message getDescription();
}