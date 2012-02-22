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

import com.philbeaudoin.quebec.shared.statechange.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationTile;
import com.philbeaudoin.quebec.shared.statechange.CubeDestinationVisitor;

/**
 * Use this class to generate the {@link SceneCubeDestination} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.statechange.CubeDestination CubeDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneCubeDestinationGenerator implements CubeDestinationVisitor<SceneCubeDestination> {

  @Override
  public SceneCubeDestination visit(CubeDestinationInfluenceZone host) {
    return new SceneCubeDestinationInfluenceZone(host);
  }

  @Override
  public SceneCubeDestination visit(CubeDestinationPlayer host) {
    return new SceneCubeDestinationPlayer(host);
  }

  @Override
  public SceneCubeDestination visit(CubeDestinationTile host) {
    return new SceneCubeDestinationTile(host);
  }
}
