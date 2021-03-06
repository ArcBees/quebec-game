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

package com.philbeaudoin.quebec.shared.game;

import java.util.List;

import com.philbeaudoin.quebec.shared.game.action.GameAction;
import com.philbeaudoin.quebec.shared.game.action.PossibleActions;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.player.Player;

/**
 * A controller to manipulate the state of a game.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameController {

  /**
   * Clears the game state and resets the entire game to the initial state.
   * @param gameState The state to reset and initialize.
   * @param players The list of players in that game.
   */
  void initGame(GameState gameState, List<Player> players);

  /**
   * Configure the possible actions given the current game state.
   * @param gameState The state on which to configure possible actions.
   */
  void configurePossibleActions(GameState gameState);

  /**
   * Retrieve the possible architect movement actions.
   * @param gameState The state to reset and initialize.
   * @param possibleActions The possible actions into which to add architect movement actions.
   */
  void getPossibleMoveArchitectActions(GameState gameState,
      PossibleActions possibleActions);

  /**
   * Prepare the game state for the next century. Leader cards are expected to be returned already.
   * @param gameState The current game state.
   */
  void prepareNextCentury(GameState gameState);

  /**
   * Performs the action and perform any behavior that is expected of this controller. For example,
   * it may render the animation for the action or persist the resulting state.
   * @param gameState The game state before the action is performed.
   * @param gameAction The game state after the action has been performed.
   */
  void performAction(GameState gameState, GameAction gameAction);

  /**
   * Set the instantaneous and perform any behavior that is expected of this controller. For
   * example, it may render the state and any interaction.
   * @param gameState The game state before the action is performed.
   * @param gameAction The game state after the action has been performed.
   */
  void setGameState(GameState gameState);

}