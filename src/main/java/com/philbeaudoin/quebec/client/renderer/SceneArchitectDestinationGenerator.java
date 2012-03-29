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

package com.philbeaudoin.quebec.client.renderer;

import com.philbeaudoin.quebec.shared.location.ArchitectDestinationOffboardNeutral;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationPlayer;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationTile;
import com.philbeaudoin.quebec.shared.location.ArchitectDestinationVisitor;

/**
 * Use this class to generate the {@link SceneArchitectDestination} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.location.ArchitectDestination ArchitectDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneArchitectDestinationGenerator
    implements ArchitectDestinationVisitor<SceneArchitectDestination> {
  @Override
  public SceneArchitectDestination visit(ArchitectDestinationTile host) {
    return new SceneArchitectDestinationTile(host);
  }

  @Override
  public SceneArchitectDestination visit(ArchitectDestinationPlayer host) {
    return new SceneArchitectDestinationPlayer(host);
  }

  @Override
  public SceneArchitectDestination visit(ArchitectDestinationOffboardNeutral host) {
    return new SceneArchitectDestinationOffboardNeutral();
  }
}
