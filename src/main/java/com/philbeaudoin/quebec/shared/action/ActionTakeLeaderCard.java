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
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.statechange.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveLeader;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationPlayer;

/**
 * The action of taking a leader card from an influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionTakeLeaderCard implements GameAction, HasLeaderCard {

  private final LeaderCard leaderCard;

  public ActionTakeLeaderCard(LeaderCard leaderCard) {
    this.leaderCard = leaderCard;
  }

  @Override
  public GameStateChange execute(GameState gameState) {
    GameStateChangeComposite result = new GameStateChangeComposite();
    PlayerState playerState = gameState.getCurrentPlayer();
    PlayerColor activePlayer = playerState.getPlayer().getColor();

    assert playerState.getLeaderCard() == null;

    // Move the leader card.
    result.add(new GameStateChangeMoveLeader(new LeaderDestinationBoard(leaderCard),
        new LeaderDestinationPlayer(leaderCard, activePlayer)));

    // If it's the gray leader, send three cubes to the citadel.
    int nbPassiveCubes = playerState.getNbPassiveCubes();
    if (leaderCard == LeaderCard.CITADEL) {
      int nbPassiveCubesMoved = 0;
      nbPassiveCubesMoved = Math.min(3, nbPassiveCubes);
      if (nbPassiveCubesMoved > 0) {
        result.add(new GameStateChangeMoveCubes(nbPassiveCubesMoved,
            new CubeDestinationPlayer(activePlayer, false),
            new CubeDestinationInfluenceZone(InfluenceType.CITADEL, activePlayer)));
      }
      int nbActiveCubes = playerState.getNbActiveCubes();
      if (nbPassiveCubesMoved < 3 && nbActiveCubes > 0) {
        // TODO: Ask how many active cubes to move? If so, move gray leader action after cube
        //     activation block and proceed in two steps.
        int nbActiveCubesMoved = Math.min(3 - nbPassiveCubesMoved, nbActiveCubes);
        result.add(new GameStateChangeMoveCubes(nbActiveCubesMoved,
            new CubeDestinationPlayer(activePlayer, false),
            new CubeDestinationInfluenceZone(InfluenceType.CITADEL, activePlayer)));
      }
      nbPassiveCubes -= nbPassiveCubesMoved;
    }

    // Activate cubes if other players already have cards.
    int nbCubesToActivate = Math.min(nbPassiveCubes, gameState.nbPlayerWithLeaders());
    if (nbCubesToActivate > 0) {
      result.add(new GameStateChangeMoveCubes(nbCubesToActivate,
          new CubeDestinationPlayer(activePlayer, false),
          new CubeDestinationPlayer(activePlayer, true)));
    }

    // If it's the yellow leader, move the neutral architect.
    if (leaderCard == LeaderCard.ECONOMIC) {
      result.add(new GameStateChangeMoveArchitect(
          new ArchitectDestinationOffboardNeutral(),
          new ArchitectDestinationPlayer(activePlayer, true)));
    }

    // Move to next player.
    result.add(new GameStateChangeNextPlayer());

    return result;
  }

  @Override
  public void accept(GameActionVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public LeaderCard getLeaderCard() {
    return leaderCard;
  }
}
