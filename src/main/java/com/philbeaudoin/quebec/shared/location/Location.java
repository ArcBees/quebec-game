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

import java.io.Serializable;

/**
 * Identifies a class representing a location on the board. Locations can be visited by
 * {@link LocationVisitor}. This is used, for example, by the rendering engine to
 * convert a location to a 2D position.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface Location extends Serializable {
  /**
   * Accepts a location visitor.
   * @param visitor The visitor.
   */
  <T> T accept(LocationVisitor<T> visitor);
}
