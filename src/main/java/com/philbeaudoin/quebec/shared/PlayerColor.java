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

package com.philbeaudoin.quebec.shared;

/**
 * The various possible player colors in the game, including an extra one indicating no player
 * color.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public enum PlayerColor {
  NONE,
  BLACK,
  WHITE,
  ORANGE,
  GREEN,
  PINK,
  NEUTRAL;

  public static final PlayerColor[] NORMAL =
      new PlayerColor[]{BLACK, WHITE, ORANGE, GREEN, PINK};

  public static final PlayerColor[] ARCHITECT =
      new PlayerColor[]{BLACK, WHITE, ORANGE, GREEN, PINK, NEUTRAL};

  /**
   * Checks if the color is one of the 5 normal player colors.
   * @return True if it is, false if it's {@code NONE} or {@code NEUTRAL}.
   */
  public boolean isNormalColor() {
    return this != NONE && this != NEUTRAL;
  }

  /**
   * Checks if the color is one of the 5 normal player colors or the neutral architect color.
   * @return True if it is, false if it's {@code NONE}.
   */
  public boolean isArchitectColor() {
    return this != NONE;
  }

  /**
   * Returns the index of a normal color, will fail if the color is NONE or NEUTRAL.
   * @return The index of the normal color.
   */
  public int normalColorIndex() {
    assert isNormalColor();
    return ordinal() - 1;
  }

  /**
   * Returns the index of an architect color, will fail if the color is NONE.
   * @return The index of the architect color.
   */
  public int architectIndex() {
    assert isArchitectColor();
    return ordinal() - 1;
  }
}
