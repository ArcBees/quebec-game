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

/**
 * Interface for a class that can visit a {@link GameAction}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface GameActionVisitor {
  /**
   * Sets the {@link PossibleActions} that is using this visitor.
   * @param possibleActions The possible actions using this visitor.
   */
  void setPossibleActions(PossibleActions possibleActions);

  /**
   * Visits a {@link ActionSendWorkers}.
   * @param host The visited class.
   */
  void visit(ActionSendWorkers host);

  /**
   * Visits a {@link ActionTakeLeaderCard}.
   * @param host The visited class.
   */
  void visit(ActionTakeLeaderCard host);

  /**
   * Visits a {@link ActionSendCubesToZone}.
   * @param host The visited class.
   */
  void visit(ActionSendCubesToZone host);

  /**
   * Visits a {@link ActionSelectBoardAction}.
   * @param host The visited class.
   */
  void visit(ActionSelectBoardAction host);

  /**
   * Visits a {@link ActionScorePoints}.
   * @param host The visited class.
   */
  void visit(ActionScorePoints host);

  /**
   * Visits a {@link ActionMoveArchitect}.
   * @param host The visited class.
   */
  void visit(ActionMoveArchitect host);

  /**
   * Visits a {@link ActionExplicit}.
   * @param host The visited class.
   */
  void visit(ActionExplicit host);

  /**
   * Visits a {@link ActionExplicitHighlightActiveTiles}.
   * @param host The visited class.
   */
  void visit(ActionExplicitHighlightActiveTiles host);

  /**
   * Visits a {@link ActionExplicitHighlightArchitectTiles}.
   * @param host The visited class.
   */
  void visit(ActionExplicitHighlightArchitectTiles host);

  /**
   * Visits a {@link ActionExplicitHighlightBoardActions}.
   * @param host The visited class.
   */
  void visit(ActionExplicitHighlightBoardActions host);

  /**
   * Visits a {@link ActionActivateCubes}.
   * @param host The visited class.
   */
  void visit(ActionActivateCubes host);

  /**
   * Visits a {@link ActionIncreaseStar}.
   * @param host The visited class.
   */
  void visit(ActionIncreaseStar host);

  /**
   * Visits a {@link ActionMoveCubes}.
   * @param host The visited class.
   */
  void visit(ActionMoveCubes host);

  /**
   * Visits a {@link ActionEmptyTileToZone}.
   * @param host The visited class.
   */
  void visit(ActionEmptyTileToZone host);

  /**
   * Visits a {@link ActionPerformScoringPhase}.
   * @param host The visited class.
   */
  void visit(ActionPerformScoringPhase host);
}
