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

package com.philbeaudoin.quebec.shared.message;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * The base class for messages that can contain an arbitrary number of colors, influence zones,
 * and a count.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BaseMessageWithCount extends BaseMessage {
  private int count;
  BaseMessageWithCount(int count, PlayerColor[] colors, InfluenceType[] zones) {
    super(colors, zones);
    this.count = count;
  }
  BaseMessageWithCount(int count, PlayerColor... colors) {
    this(count, colors, null);
  }
  BaseMessageWithCount(int count, InfluenceType... zones) {
    this(count, null, zones);
  }
  BaseMessageWithCount(int count) {
    this(count, null, null);
  }
  /**
   * For serialization only.
   */
  protected BaseMessageWithCount() {
  }
  public int getCount() {
    return count;
  }
}
