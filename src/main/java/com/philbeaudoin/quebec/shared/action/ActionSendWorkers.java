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
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangePrepareAction;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The action of sending worker cubes to a spot on a tile.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionSendWorkers implements PossibleActions, GameActionOnTile {

  private final Tile destinationTile;

  public ActionSendWorkers(Tile destinationTile) {
    this.destinationTile = destinationTile;
  }

  @Override
  public int getNbActions() {
    return 1;
  }

  @Override
  public GameStateChange execute(int actionIndex, GameState gameState) {
    assert actionIndex == 0;
    return execute(gameState);
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getPlayer().getColor();

    TileState tileState = gameState.getTileState(destinationTile);
    int destinationSpot = -1;
    for (int spot = 0; spot < 3; ++spot) {
      if (tileState.getColorInSpot(spot) == PlayerColor.NONE) {
        destinationSpot = spot;
        break;
      }
    }
    assert destinationSpot != -1;
    assert playerState.getNbActiveCubes() >= tileState.getCubesPerSpot();

    // Move the cubes.
    result.add(new GameStateChangeMoveCubes(tileState.getCubesPerSpot(),
        new CubeDestinationPlayer(activePlayer, true),
        new CubeDestinationTile(destinationTile, activePlayer, destinationSpot)));

    // Check if the action should be executed.
    if (!playerState.ownsArchitect(tileState.getArchitect()) ||
        playerState.getLeaderCard() == LeaderCard.RELIGIOUS) {
      Vector2d tileLocation = tileState.getLocation();
      BoardAction action = Board.actionForTileLocation(tileLocation.getColumn(),
          tileLocation.getLine());
      result.add(new GameStateChangePrepareAction(action));
    } else {
      // Move to next player.
      result.add(new GameStateChangeNextPlayer());
    }

    return result;
  }

  @Override
  public void accept(PossibleActionsVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Tile getDestinationTile() {
    return destinationTile;
  }
}
