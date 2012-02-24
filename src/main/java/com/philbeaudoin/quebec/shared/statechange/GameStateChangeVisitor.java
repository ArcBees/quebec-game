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

/**
 * Interface for a class that can visit a {@link GameStateChange}.
 * @param <T> The return type of the visit methods.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameStateChangeVisitor<T> {
  /**
   * Visits a {@link GameStateChangeComposite}.
   * @param host The visited class.
   */
  T visit(GameStateChangeComposite host);
  /**
   * Visits a {@link GameStateChangeMoveCubes}.
   * @param host The visited class.
   */
  T visit(GameStateChangeMoveCubes host);
  /**
   * Visits a {@link GameStateChangeFlipTile}.
   * @param host The visited class.
   */
  T visit(GameStateChangeFlipTile host);
  /**
   * Visits a {@link GameStateChangeIncreaseStarToken}.
   * @param host The visited class.
   */
  T visit(GameStateChangeIncreaseStarToken host);

  /**
   * Visits a {@link GameStateChangeMoveArchitect}.
   * @param host The visited class.
   */
  T visit(GameStateChangeMoveArchitect host);
  /**
   * Visits a {@link GameStateChangeNextPlayer}.
   * @param host The visited class.
   */
  T visit(GameStateChangeNextPlayer host);
  /**
   * Visits a {@link GameStateChangeMoveLeader}.
   * @param host The visited class.
   */
  T visit(GameStateChangeMoveLeader host);
  /**
   * Visits a {@link GameStateChangePrepareAction}.
   * @param host The visited class.
   */
  T visit(GameStateChangePrepareAction host);
  /**
   * Visits a {@link GameStateChangeQueuePossibleActions}.
   * @param host The visited class.
   */
  T visit(GameStateChangeQueuePossibleActions host);
  /**
   * Visits a {@link GameStateChangeScorePoints}.
   * @param host The visited class.
   */
  T visit(GameStateChangeScorePoints host);
  /**
   * Visits a {@link GameStateChangeSetPlayer}.
   * @param host The visited class.
   */
  T visit(GameStateChangeSetPlayer host);
  /**
   * Visits a {@link GameStateChangePrepareNextCentury}.
   * @param host The visited class.
   */
  T visit(GameStateChangePrepareNextCentury host);
  /**
   * Visits a {@link GameStateChangeInstantaneousDecorator}.
   * @param host The visited class.
   */
  T visit(GameStateChangeInstantaneousDecorator host);
  /**
   * Visits a {@link GameStateChangeReinit}.
   * @param host The visited class.
   */
  T visit(GameStateChangeReinit host);
}
