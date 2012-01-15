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

package com.philbeaudoin.quebec.client.utils;

import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This class makes it possible to determine the position of pawns within a staggered stack.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PawnStack {
  private static final double PATTERN_SIZE_X = 0.01;
  private static final double PATTERN_SIZE_Y = 0.01;

  private final int nbPawns;
  private final int lines;
  private final int columns;

  /**
   * Creates a pawn stack with a specified number of pawns.
   * @param nbPawns The number of pawns in the stack, maximum 5.
   */
  public PawnStack(int nbPawns) {
    assert nbPawns > 0 && nbPawns <= 5;
    this.nbPawns = nbPawns;
    lines = nbPawns / 3 + nbPawns / 4;
    columns = (nbPawns == 1) ? 1 : 3;
  }

  /**
   * Get the position of the pawn at a given index. The order is the following:
   * <pre>
   * 0 1
   *  2
   * 3 4
   * </pre>
   * @param index Index of the pawn, zero-based.
   * @return The position at that line and column
   */
  public Vector2d getPosition(int index) {
    assert index >= 0 && index < nbPawns;
    int line = (index + 1) / 3 + (index + 1) / 4;
    int column = (index % 3) * 2 % 3;
    double y = (line - (lines - 1) / 2.0) * PATTERN_SIZE_Y;
    double x = (column - (columns - 1) / 2.0) * PATTERN_SIZE_X;
    return new Vector2d(x, y);
  }
}
