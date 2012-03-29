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
 * Interface for a class that can visit a {@link LeaderDestination}.
 * @param <T> The return type of the visit methods.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface LeaderDestinationVisitor<T> {
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
}
