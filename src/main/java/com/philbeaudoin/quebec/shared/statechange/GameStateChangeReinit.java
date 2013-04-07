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

package com.philbeaudoin.quebec.shared.statechange;

import java.util.ArrayList;

import com.philbeaudoin.quebec.shared.player.Player;
import com.philbeaudoin.quebec.shared.player.PlayerState;
import com.philbeaudoin.quebec.shared.state.GameController;
import com.philbeaudoin.quebec.shared.state.GameState;

/**
 * A change of the game state obtained by entirely reinitializing the game state.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateChangeReinit implements GameStateChange {

  @Override
  public void apply(GameController gameController, GameState gameState) {
    ArrayList<Player> players = new ArrayList<Player>();
    for (PlayerState playerState : gameState.getPlayerStates()) {
      players.add(playerState.getPlayer());
    }
    gameController.initGame(gameState, players);
  }

  @Override
  public <T> T accept(GameStateChangeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
