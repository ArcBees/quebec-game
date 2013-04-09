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

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionExplicit;
import com.philbeaudoin.quebec.shared.game.action.ActionMoveCubes;
import com.philbeaudoin.quebec.shared.game.action.ActionSendCubesToZone;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;

/**
 * Board action: purple, 3 cubes to activate, send one cube to any zone and move two cubes from one
 * influence zone to another.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardActionPurpleOneToAnyMoveTwo extends BoardAction {
  private static final int NB_CUBES_TO_MOVE = 2;

  public BoardActionPurpleOneToAnyMoveTwo() {
    super(10, 7, InfluenceType.RELIGIOUS, 3, ActionType.PURPLE_ONE_TO_ANY_MOVE_TWO);
  }

  public PossibleActions getPossibleActions(GameController gameController, GameState gameState, Tile triggeringTile) {
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor playerColor = playerState.getColor();

    if (playerState.getNbTotalCubes() > 0) {
      PossibleActions sendAnywhere = new PossibleActions(
          new Message.SendPassiveCubesToAnyZoneOrCitadel(1, playerColor));
      sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.RELIGIOUS,
          computeFollowupMoveActions(gameState, InfluenceType.RELIGIOUS)));
      sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.POLITIC,
          computeFollowupMoveActions(gameState, InfluenceType.POLITIC)));
      sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.ECONOMIC,
          computeFollowupMoveActions(gameState, InfluenceType.ECONOMIC)));
      sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.CULTURAL,
          computeFollowupMoveActions(gameState, InfluenceType.CULTURAL)));
      sendAnywhere.add(new ActionSendCubesToZone(1, false, InfluenceType.CITADEL,
          computeFollowupMoveActions(gameState, InfluenceType.CITADEL)));
      sendAnywhere.add(new ActionExplicit(new Message.Text("skip"),
          computeFollowupMoveActions(gameState, null)));
      return sendAnywhere;
    } else {
      return computeMoveActions(gameState, null);
    }
  }

  /**
   * Generates the followup move actions that can be executed after the user has moved a cube to the
   * given influence zone.
   * @param gameState The current game state.
   * @param zoneIntoWhichToAddACube The zone into which the user just send a cube, of null if none.
   * @return The followup move actions, or a change to next player if there is no move action.
   */
  private GameStateChange computeFollowupMoveActions(GameState gameState,
      InfluenceType zoneIntoWhichToAddACube) {
    PossibleActions moveActions = computeMoveActions(gameState, zoneIntoWhichToAddACube);
    return moveActions == null ? new GameStateChangeNextPlayer() :
      new GameStateChangeQueuePossibleActions(moveActions);
  }

  /**
   * Generates the move actions that can be executed after the user has moved a cube to the given
   * influence zone.
   * @param gameState The current game state.
   * @param zoneIntoWhichToAddACube The zone into which the user just send a cube, of null if none.
   * @return The possible move actions.
   */
  private PossibleActions computeMoveActions(GameState gameState,
      InfluenceType zoneIntoWhichToAddACube) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getColor();

    ArrayList<InfluenceType> origins = new ArrayList<InfluenceType>();
    for (InfluenceType origin : InfluenceType.values()) {
      if (getNbCubesToMoveAfterAdd(gameState, playerColor, origin, zoneIntoWhichToAddACube) > 0) {
        origins.add(origin);
      }
    }

    PossibleActions moveActions = null;
    if (origins.size() > 1) {
      // First select origin.
      moveActions = new PossibleActions(new Message.MoveOneOrTwoCubesSelectOrigin(playerColor));
    } else if (origins.size() == 1) {
      // Directly select destination.
      moveActions = new PossibleActions(new Message.MoveCubesSelectDestination(
          getNbCubesToMoveAfterAdd(gameState, playerColor, origins.get(0), zoneIntoWhichToAddACube),
          playerColor, origins.get(0)));
    } else {
      // Nothing to move.
      return null;
    }
    for (InfluenceType origin : origins) {
      for (InfluenceType destination : InfluenceType.values()) {
        if (destination != origin) {
          moveActions.add(new ActionMoveCubes(
              getNbCubesToMoveAfterAdd(gameState, playerColor, origin, zoneIntoWhichToAddACube),
              origin, destination));
        }
      }
    }
    moveActions.add(ActionExplicit.createSkipAction());
    return moveActions;
  }

  private int getNbCubesToMoveAfterAdd(GameState gameState, PlayerColor playerColor,
      InfluenceType zone, InfluenceType zoneIntoWhichToAddACube) {
    int nbCubesInZone = gameState.getPlayerCubesInInfluenceZone(zone, playerColor) +
        (zone == zoneIntoWhichToAddACube ? 1 : 0);
    return Math.min(nbCubesInZone, NB_CUBES_TO_MOVE);
  }

  @Override
  public Message getDescription() {
    return new Message.MultilineText("actionPurple4", 0.7);
  }
}
