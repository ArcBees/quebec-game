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
 * Interface for a class that can visit a {@link Location}.
 * @param <T> The return type of the visit methods.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface LocationVisitor<T> {
  /**
   * Visits a {@link ArchitectDestinationOffboardNeutral}.
   * @param host The visited class.
   */
  T visit(ArchitectDestinationOffboardNeutral host);
  /**
   * Visits a {@link ArchitectDestinationPlayer}.
   * @param host The visited class.
   */
  T visit(ArchitectDestinationPlayer host);
  /**
   * Visits a {@link ArchitectDestinationTile}.
   * @param host The visited class.
   */
  T visit(ArchitectDestinationTile host);
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
  /**
   * Visits a {@link LeaderDestinationBoard}.
   * @param host The visited class.
   */
  T visit(LeaderDestinationBoard host);
  /**
   * Visits a {@link LeaderDestinationPlayer}.
   * @param host The visited class.
   */
  T visit(LeaderDestinationPlayer host);
  /**
   * Visits a {@link LocationRelative}.
   * @param host The visited class.
   */
  T visit(LocationRelative host);
  /**
   * Visits a {@link LocationTopCenter}.
   * @param host The visited class.
   */
  T visit(LocationTopCenter host);
  /**
   * Visits a {@link LocationCenter}.
   * @param host The visited class.
   */
  T visit(LocationCenter host);
  /**
   * Visits a {@link LocationBottomCenter}.
   * @param host The visited class.
   */
  T visit(LocationBottomCenter host);
  /**
   * Visits a {@link LocationPlayerAreas}.
   * @param host The visited class.
   */
  T visit(LocationPlayerAreas host);
  /**
   * Visits a {@link LocationScore}.
   * @param host The visited class.
   */
  T visit(LocationScore host);
  /**
   * Visits a {@link LocationBoardAction}.
   * @param host The visited class.
   */
  T visit(LocationBoardAction host);
}
