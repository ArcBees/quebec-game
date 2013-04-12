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

package com.philbeaudoin.quebec.shared.game.action;

import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.state.Board;
import com.philbeaudoin.quebec.shared.game.state.BoardAction;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.game.state.LeaderCard;
import com.philbeaudoin.quebec.shared.game.state.Tile;
import com.philbeaudoin.quebec.shared.game.state.TileState;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeInstantaneousDecorator;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangePrepareAction;
import com.philbeaudoin.quebec.shared.location.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The action of sending worker cubes to a spot on a tile. When coming from the passive reserve,
 * the associated board action is never executed.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@SuppressWarnings("serial")
public class ActionSendWorkers implements GameActionOnTile {

  private boolean fromActive;
  private Tile destinationTile;
  private GameStateChange followup;

  public ActionSendWorkers(boolean fromActive, Tile destinationTile) {
    this(fromActive, destinationTile, null);
  }

  public ActionSendWorkers(boolean fromActive, Tile destinationTile,
      GameStateChange followup) {
    this.fromActive = fromActive;
    this.destinationTile = destinationTile;
    this.followup = followup;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private ActionSendWorkers() {
  }

  @Override
  public GameStateChange execute(GameController gameController, GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getColor();

    TileState tileState = gameState.findTileState(destinationTile);
    int destinationSpot = -1;
    for (int spot = 0; spot < 3; ++spot) {
      if (tileState.getColorInSpot(spot) == PlayerColor.NONE) {
        destinationSpot = spot;
        break;
      }
    }
    assert destinationSpot != -1;

    int nbCubes = tileState.getCubesPerSpot();
    assert nbCubes <= playerState.getNbTotalCubes();

    CubeDestinationTile destination = new CubeDestinationTile(destinationTile, activePlayer,
        destinationSpot);
    if (!fromActive && playerState.getNbPassiveCubes() > 0) {
      int cubesMissing = Math.max(0, nbCubes - playerState.getNbPassiveCubes());
      if (cubesMissing > 0) {
        // Not enough cubes in the passive reserve, move some from the active reserve. We do this
        // instead of sending cubes simultaneously from both reserves because the system does not
        // allow filling a spot in this way.
        result.add(new GameStateChangeInstantaneousDecorator(
            new GameStateChangeMoveCubes(cubesMissing,
              new CubeDestinationPlayer(activePlayer, true),
              new CubeDestinationPlayer(activePlayer, false))));
      }
      // Send as much cube as we can from the passive reserve.
      result.add(new GameStateChangeMoveCubes(nbCubes,
          new CubeDestinationPlayer(activePlayer, false), destination));
    } else {
      // Move all the cubes from the active reserve.
      assert nbCubes <= playerState.getNbActiveCubes();
      result.add(new GameStateChangeMoveCubes(nbCubes,
          new CubeDestinationPlayer(activePlayer, true), destination));
    }

    // Check if the action should be executed.
    if (followup != null) {
      result.add(followup);
    } else if (canExecuteBoardAction(playerState, tileState)) {
      Vector2d tileLocation = tileState.getLocation();
      BoardAction action = Board.actionForTileLocation(tileLocation.getColumn(),
          tileLocation.getLine());
      result.add(new GameStateChangePrepareAction(action, destinationTile));
    } else {
      // Move to next player.
      result.add(new GameStateChangeNextPlayer());
    }

    return result;
  }

  @Override
  public boolean isAutomatic() {
    return false;
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
   * Checks if, given a game state, the player should execute the board action.
   * @param gameState The game state.
   * @return True if the player can execute the board action.
   */
  public boolean canExecuteBoardAction(GameState gameState) {
    return canExecuteBoardAction(gameState.getCurrentPlayer(),
        gameState.findTileState(destinationTile));
  }

  private boolean canExecuteBoardAction(PlayerState playerState, TileState tileState) {
    return fromActive &&
        (!playerState.ownsArchitect(tileState.getArchitect()) ||
        playerState.getLeaderCard() == LeaderCard.RELIGIOUS);
  }

  /**
   * Returns whether the cubes come from the active or the passive reserve, with overflow coming
   * from the active reserve.
   * @return True if they come from the active reserve.
   */
  public boolean areCubesFromActive() {
    return fromActive;
  }
}
