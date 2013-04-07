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
import com.philbeaudoin.quebec.shared.location.ArchitectDestination;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
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

  private Tile destinationTile;
  private boolean neutralArchitect;
  private int cubesToActivate;
  private GameStateChange followup;
  private boolean automatic;

  /**
   * Create an action to move the architect.
   * @param destinationTile The tile to move the architect to, or null to move it out of the board.
   * @param neutralArchitect True to indicate that it's the neutral architect moving.
   * @param cubesToActivate the number of cubes to move from passive to active.
   */
  public ActionMoveArchitect(Tile destinationTile, boolean neutralArchitect, int cubesToActivate) {
    this(destinationTile, neutralArchitect, cubesToActivate, null);
  }

  /**
   * Create an action to move the architect, overriding the action action performed.
   * @param destinationTile The tile to move the architect to, or null to move it out of the board.
   * @param neutralArchitect True to indicate that it's the neutral architect moving.
   * @param cubesToActivate the number of cubes to move from passive to active.
   * @param followup The action to execute following this one, use null to followup with the default
   *     action.
   */
  public ActionMoveArchitect(Tile destinationTile, boolean neutralArchitect, int cubesToActivate,
      GameStateChange followup) {
    this(destinationTile, neutralArchitect, cubesToActivate, followup, false);
  }

  /**
   * Create an action to move the architect, overriding the action action performed.
   * @param destinationTile The tile to move the architect to, or null to move it out of the board.
   * @param neutralArchitect True to indicate that it's the neutral architect moving.
   * @param cubesToActivate the number of cubes to move from passive to active.
   * @param followup The action to execute following this one, use null to followup with the default
   *     action.
   * @param automatic True if the action should be executed automatically, false if not.
   */
  public ActionMoveArchitect(Tile destinationTile, boolean neutralArchitect, int cubesToActivate,
      GameStateChange followup, boolean automatic) {
    this.destinationTile = destinationTile;
    this.neutralArchitect = neutralArchitect;
    this.cubesToActivate = cubesToActivate;
    this.followup = followup;
    this.automatic = automatic;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionMoveArchitect() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();
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
      assert cubesToActivate <= playerState.getNbPassiveCubes();
      result.add(new GameStateChangeMoveCubes(cubesToActivate,
          new CubeDestinationPlayer(activePlayer, false),
          new CubeDestinationPlayer(activePlayer, true)));

      if (followup != null) {
        result.add(followup);
      } else {
        // Move to next player.
        result.add(new GameStateChangeNextPlayer());
      }
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
      if (followup != null) {
        result.add(followup);
      } else {
        // Followup with scoring.
        PossibleActions scoring = new PossibleActions(new Message.Text("scoringPhaseBegins"));
        scoring.add(new ActionPerformScoringPhase());
        result.add(new GameStateChangeQueuePossibleActions(scoring));
      }
    }

    return finalResult;
  }

  @Override
  public boolean isAutomatic() {
    return automatic;
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

  /**
   * Returns the number of cubes activated after the architect is moved.
   * @return The number of activated cubes.
   */
  public int getCubesToActivate() {
    return cubesToActivate;
  }
}
