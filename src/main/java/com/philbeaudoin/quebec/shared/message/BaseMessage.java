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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;

/**
 * The base class for messages that can contain an arbitrary number of colors or influence zones.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
class BaseMessage implements IsSerializable {
  private PlayerColor[] colors;
  private InfluenceType[] zones;
  BaseMessage(PlayerColor[] colors, InfluenceType[] zones) {
    this.colors = colors;
    this.zones = zones;
  }
  BaseMessage(PlayerColor... colors) {
    this(colors, null);
  }
  BaseMessage(InfluenceType... zones) {
    this(null, zones);
  }
  /**
   * For serialization only.
   */
  protected BaseMessage() {
  }
  public PlayerColor getColor(int i) {
    assert colors != null && i < colors.length;
    return colors[i];
  }
  public InfluenceType getZone(int i) {
    assert zones != null && i < zones.length;
    return zones[i];
  }
  static <T> T[] array(T... elems) {
    return elems;
  }
}