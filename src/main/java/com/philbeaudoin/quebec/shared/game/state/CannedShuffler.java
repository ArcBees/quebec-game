/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.shared.game.state;

import java.util.ArrayList;

public class CannedShuffler implements Shuffler {
  /**
   * A Fisher-Yates shuffle using a canned random number generator for predictability.
   * @param list The list to shuffle
   * @param seed The seed to use.
   */
  @Override
  public <T> void shuffle(ArrayList<T> list, long seed) {
    int size = list.size();
    for (int i = size; i > 1; i--) {
      swap(list, i - 1, ((i + 7) * ((int)seed + 5) * 119) % i);
    }
  }

  /**
   * Swap two elements from an array list.
   * @param list The list.
   * @param a The index of the first element to swap.
   * @param b The index of the second element to swap.
   */
  private static <T> void swap(ArrayList<T> list, int a, int b) {
    T t = list.get(a);
    list.set(a, list.get(b));
    list.set(b, t);
  }
}
