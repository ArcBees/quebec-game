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

package com.philbeaudoin.quebec.shared.state;

import java.util.List;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Utility class for various types of {@link GameController}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameControllerHelper {

  // Number of cubes per player for a 2, 3, 4, 5 player game.
  private static final int CUBES_FOR_N_PLAYERS[] = { 25, 25, 22, 20 };

  /**
   * Resets the game state.
   * @param gameState The game state to reset.
   * @param players The list of players.
   * @param shuffle True to perform a real random shuffle of the tiles, false to use a canned
   *     shuffle.
   */
  static void resetGameState(GameState gameState, List<Player> players, boolean shuffle) {
    gameState.setCentury(0);

    int nbPlayers = players.size();
    assert nbPlayers >= 2 && nbPlayers <= 5;
    int cubesPerPlayer = CUBES_FOR_N_PLAYERS[nbPlayers - 2];
    List<PlayerState> playerStates = gameState.getPlayerStates();
    playerStates.clear();
    for (Player player : players) {
      PlayerState playerState = new PlayerState(player);
      playerState.setHoldingArchitect(true);
      playerState.setNbPassiveCubes(cubesPerPlayer - 3);
      playerState.setNbActiveCubes(3);
      playerStates.add(playerState);
    }
    playerStates.get(0).setCurrentPlayer(true);

    List<TileState> tileStates = gameState.getTileStates();
    tileStates.clear();
    TileDeck tileDeck = new TileDeck(shuffle);
    for (int column = 0; column < 18; ++column) {
      for (int line = 0; line < 8; ++line) {
        BoardAction boardAction = Board.actionForTileLocation(column, line);
        if (boardAction != null) {
          Tile tile = tileDeck.draw(boardAction.getInfluenceType());
          TileState tileState = new TileState(tile, new Vector2d(column, line));
          tileState.setArchitect(PlayerColor.NONE);
          tileStates.add(tileState);
        }
      }
    }

    List<LeaderCard> availableLeaderCards = gameState.getAvailableLeaderCards();
    availableLeaderCards.clear();
    LeaderCard[] leaderCards = LeaderCard.FOUR_FIVE_PLAYERS;
    if (nbPlayers == 3) {
      leaderCards = LeaderCard.THREE_PLAYERS;
    }
    for (LeaderCard leaderCard : leaderCards) {
      availableLeaderCards.add(leaderCard);
    }

    for (InfluenceType influenceType : InfluenceType.values()) {
      for (PlayerColor playerColor : PlayerColor.NORMAL) {
        gameState.setPlayerCubesInInfluenceZone(influenceType, playerColor, 0);
      }
    }
  }
}
