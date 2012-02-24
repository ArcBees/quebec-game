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

package com.philbeaudoin.quebec.shared.action;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestination;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;

/**
 * The action of moving a specific architect to a given tile or out of the board.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionMoveArchitect implements GameActionOnTile {

  private final Tile destinationTile;
  private final boolean neutralArchitect;

  /**
   * Create an action to move the architect.
   * @param destinationTile The tile to move the architect to, or null to move it out of the board.
   * @param neutralArchitect True to indicate that it's the neutral architect moving.
   */
  public ActionMoveArchitect(Tile destinationTile, boolean neutralArchitect) {
    this.destinationTile = destinationTile;
    this.neutralArchitect = neutralArchitect;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getPlayer().getColor();
    PlayerColor architectColor = neutralArchitect ? PlayerColor.NEUTRAL : activePlayer;

    ArchitectDestination architectOrigin;
    // Complete the building if needed.
    boolean needToCompleteBuilding = neutralArchitect ? !playerState.isHoldingNeutralArchitect() :
        !playerState.isHoldingArchitect();
    GameStateChange finalResult = result;
    boolean architectOriginIsPlayerBoard = false;
    if (needToCompleteBuilding) {
      TileState originState = gameState.findTileUnderArchitect(architectColor);
      finalResult = MoveArchitectHelper.completeBuilding(gameState, playerState, originState,
          result);
      architectOrigin = new ArchitectDestinationTile(originState.getTile(), architectColor);
    } else {
      architectOriginIsPlayerBoard = true;
      architectOrigin = new ArchitectDestinationPlayer(activePlayer, neutralArchitect);
    }

    if (destinationTile != null) {
      // Move the architect to a new tile.
      result.add(new GameStateChangeMoveArchitect(architectOrigin,
          new ArchitectDestinationTile(destinationTile, architectColor)));

      // Activate 3 cubes (or less if the player doesn't have enough).
      int nbCubesToActivate = Math.min(3, playerState.getNbPassiveCubes());
      result.add(new GameStateChangeMoveCubes(nbCubesToActivate,
          new CubeDestinationPlayer(activePlayer, false),
          new CubeDestinationPlayer(activePlayer, true)));

      // Move to next player.
      result.add(new GameStateChangeNextPlayer());
    } else {
      // Move the architect out of the board, trigger scoring.
      if (neutralArchitect) {
        // Move it entirely out of the board.
        result.add(new GameStateChangeMoveArchitect(architectOrigin,
            new ArchitectDestinationOffboardNeutral()));
        // Move to the next player, since he'll be the one starting the next century.
        result.add(new GameStateChangeNextPlayer(false));
      } else if (!architectOriginIsPlayerBoard) {
        result.add(new GameStateChangeMoveArchitect(architectOrigin,
            new ArchitectDestinationPlayer(activePlayer, neutralArchitect)));
      }
      PossibleActions scoring = new PossibleActions(new Message.ScoringPhaseBegins());
      scoring.add(new ActionPerformScoringPhase());
      result.add(new GameStateChangeQueuePossibleActions(scoring));
    }

    return finalResult;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Tile getDestinationTile() {
    return destinationTile;
  }

  /**
   * Checks if the action moves the neutral architect or the player's architect.
   * @return True if the action moves the neutral architect.
   */
  public boolean isNeutralArchitect() {
    return neutralArchitect;
  }
}
