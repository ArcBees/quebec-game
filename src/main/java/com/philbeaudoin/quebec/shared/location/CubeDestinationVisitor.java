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

/**
 * Interface for a class that can visit a {@link CubeDestination}.
 * @param <T> The type returned by the visit method.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface CubeDestinationVisitor<T> {
  /**
   * Visits a {@link CubeDestinationInfluenceZone}.
   * @param host The visited class.
   */
  T visit(CubeDestinationInfluenceZone host);
  /**
   * Visits a {@link CubeDestinationPlayer}.
   * @param host The visited class.
   */
  T visit(CubeDestinationPlayer host);
  /**
   * Visits a {@link CubeDestinationTile}.
   * @param host The visited class.
   */
  T visit(CubeDestinationTile host);
}
