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

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.location.CubeDestination;
import com.philbeaudoin.quebec.shared.location.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.location.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeSetPlayer;

/**
 * A helper class to perform move architect actions.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class MoveArchitectHelper {
  /**
   * Completes the building by sending away all the cubes in it and placing a star token on it.
   * If nobody holds the politic leader card, the {@link GameStateChangeComposite} passed as
   * parameter will be returned. If a player who has cubes on the tile holds the politic leader
   * card, an out-of-turn action will be planned and a new {@link GameStateChange} taking this
   * action into account will be returned.
   * @param gameState The game state.
   * @param playerState The current player state.
   * @param originState The state of the tile that is completed.
   * @param result The {@link GameStateChangeComposite} to which to append state changes.
   * @return Possibly a new {@link GameStateChange} accounting for out-of-turn actions.
   */
  static GameStateChange completeBuilding(GameState gameState, PlayerState playerState,
      TileState originState, GameStateChangeComposite result) {
    PlayerColor playerColor = playerState.getColor();
    Tile origin = originState.getTile();
    InfluenceType tileInfluence = origin.getInfluenceType();

    // Move cubes out of the tile.
    int nbCubes = originState.getCubesPerSpot();
    int nbFilledSpots = 0;
    PlayerColor outOfTurnPlayer = PlayerColor.NONE;
    for (int spot = 0; spot < 3; ++spot) {
      PlayerColor cubesColor = originState.getColorInSpot(spot);
      if (cubesColor.isNormalColor()) {
        if (gameState.getPlayerState(cubesColor).getLeaderCard() == LeaderCard.POLITIC) {
          outOfTurnPlayer = cubesColor;
        } else {
          CubeDestination from = new CubeDestinationTile(origin, cubesColor, spot);
          CubeDestination to = new CubeDestinationInfluenceZone(tileInfluence, cubesColor);
          GameStateChangeMoveCubes moveCubes = new GameStateChangeMoveCubes(nbCubes, from, to);
          result.add(moveCubes);
        }
        nbFilledSpots++;
      }
    }

    // Flip the tile and place a star token on it.
    PlayerColor starTokenColor = nbFilledSpots == 0 ? PlayerColor.NONE : playerColor;
    result.add(new GameStateChangeFlipTile(origin, starTokenColor, nbFilledSpots));

    if (nbFilledSpots > 0 && playerState.getLeaderCard() != null &&
        playerState.getLeaderCard().isCultural()) {
      // Cultural leader score points based on the number of filled spots.
      result.add(new GameStateChangeScorePoints(playerColor, LeaderCard.getPointsForCultural(
          playerState.getLeaderCard(), nbFilledSpots)));
    }

    // Prepend an out-of-turn action if the player with the red leader had cubes.
    if (outOfTurnPlayer != PlayerColor.NONE) {
      // Make sure we switch back to the current player after out-of-turn action.
      result.addToFront(new GameStateChangeSetPlayer(playerColor));

      GameStateChangeComposite newResult = new GameStateChangeComposite();
      newResult.add(new GameStateChangeSetPlayer(outOfTurnPlayer));
      PossibleActions emptyTileActions = new PossibleActions(new Message.SelectWhereToEmptyTile(
          outOfTurnPlayer));
      for (InfluenceType influenceType : InfluenceType.values()) {
        if (influenceType != InfluenceType.CITADEL) {
          emptyTileActions.add(new ActionEmptyTileToZone(origin, influenceType, result));
        }
      }
      newResult.add(new GameStateChangeQueuePossibleActions(emptyTileActions));
      return newResult;
    }
    return result;
  }
}
