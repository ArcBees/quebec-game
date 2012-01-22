/**
 * Copyright 2011 Philippe Beaudoin
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
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestination;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.CubeDestination;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;

/**
 * The action of moving an architect.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PossibleActionsMoveArchitect implements PossibleActions {

  private final Tile destinationTile;
  private final boolean neutralArchitect;

  public PossibleActionsMoveArchitect(Tile destinationTile, boolean neutralArchitect) {
    this.destinationTile = destinationTile;
    this.neutralArchitect = neutralArchitect;
  }
  @Override
  public int getNbActions() {
    return 1;
  }

  @Override
  public GameStateChange execute(int actionIndex, GameState gameState) {
    assert actionIndex == 0;
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor playerColor = playerState.getPlayer().getColor();
    PlayerColor architectColor = neutralArchitect ? PlayerColor.NEUTRAL : playerColor;

    ArchitectDestination architectOrigin;

    // Complete the building if needed.
    boolean needToCompleteBuilding = neutralArchitect ? !playerState.isHoldingNeutralArchitect() :
        !playerState.isHoldingArchitect();
    if (needToCompleteBuilding) {
      TileState originState = gameState.findTileUnderArchitect(architectColor);
      completeBuilding(gameState, playerState, originState, result);
      architectOrigin = new ArchitectDestinationTile(originState.getTile(), architectColor);
    } else {
      architectOrigin = new ArchitectDestinationPlayer(playerColor, neutralArchitect);
    }

    // Move the architect.
    result.add(new GameStateChangeMoveArchitect(architectOrigin,
        new ArchitectDestinationTile(destinationTile, architectColor)));

    // Activate 3 cubes (or less if the player doesn't have enough).
    int nbCubesToActivate = Math.min(3, playerState.getNbPassiveCubes());
    result.add(new GameStateChangeMoveCubes(nbCubesToActivate,
        new CubeDestinationPlayer(playerColor, false),
        new CubeDestinationPlayer(playerColor, true)));

    return result;
  }

  private void completeBuilding(GameState gameState, PlayerState playerState, TileState originState,
      GameStateChangeComposite result) {
    Tile origin = originState.getTile();
    InfluenceType tileInfluence = origin.getInfluenceType();

    // Move cubes out of the tile.
    int nbCubes = originState.getCubesPerSpot();
    int nbFilledSpots = 0;
    for (int spot = 0; spot < 3; ++spot) {
      PlayerColor cubesColor = originState.getColorInSpot(spot);
      if (cubesColor != PlayerColor.NONE) {
        // TODO: If the player has the red leader card we need an out-of-turn action.
        CubeDestination from = new CubeDestinationTile(origin, cubesColor, spot);
        CubeDestination to = new CubeDestinationInfluenceZone(tileInfluence, cubesColor);
        GameStateChangeMoveCubes moveCubes = new GameStateChangeMoveCubes(nbCubes, from, to);
        result.add(moveCubes);
        nbFilledSpots++;
      }
    }

    // Flip the tile and place a star token on it.
    result.add(new GameStateChangeFlipTile(origin, playerState.getPlayer().getColor(),
        nbFilledSpots));

    // TODO: Score points if the user has the purple leader.
  }

  @Override
  public void accept(PossibleActionsVisitor visitor) {
    visitor.visit(this);
  }
}
