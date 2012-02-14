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
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameStateChangeVisitor {
  /**
   * Visits a {@link GameStateChangeComposite}.
   * @param host The visited class.
   */
  void visit(GameStateChangeComposite host);
  /**
   * Visits a {@link GameStateChangeMoveCubes}.
   * @param host The visited class.
   */
  void visit(GameStateChangeMoveCubes host);
  /**
   * Visits a {@link GameStateChangeFlipTile}.
   * @param host The visited class.
   */
  void visit(GameStateChangeFlipTile host);
  /**
   * Visits a {@link GameStateChangeMoveArchitect}.
   * @param host The visited class.
   */
  void visit(GameStateChangeMoveArchitect host);
  /**
   * Visits a {@link GameStateChangeNextPlayer}.
   * @param host The visited class.
   */
  void visit(GameStateChangeNextPlayer host);
  /**
   * Visits a {@link GameStateChangeMoveLeader}.
   * @param host The visited class.
   */
  void visit(GameStateChangeMoveLeader host);
  /**
   * Visits a {@link GameStateChangePrepareAction}.
   * @param host The visited class.
   */
  void visit(GameStateChangePrepareAction host);
  /**
   * Visits a {@link GameStateChangeQueuePossibleActions}.
   * @param host The visited class.
   */
  void visit(GameStateChangeQueuePossibleActions host);
  /**
   * Visits a {@link GameStateChangeScorePoints}.
   * @param host The visited class.
   */
  void visit(GameStateChangeScorePoints host);
}
