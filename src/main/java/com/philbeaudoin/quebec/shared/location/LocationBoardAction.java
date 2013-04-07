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

package com.philbeaudoin.quebec.shared.location;

import com.philbeaudoin.quebec.shared.state.BoardAction;

/**
 * Identifies a location corresponding to an action on the board.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class LocationBoardAction implements Location {
  private BoardAction boardAction;

  public LocationBoardAction(BoardAction boardAction) {
    this.boardAction = boardAction;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private LocationBoardAction() {
  }

  @Override
  public <T> T accept(LocationVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public BoardAction getBoardAction() {
    return boardAction;
  }
}