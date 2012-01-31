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

import javax.inject.Inject;

import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationPlayer;
import com.philbeaudoin.quebec.shared.statechange.LeaderDestinationVisitor;

/**
 * Use this class to generate the {@link SceneLeaderDestination} corresponding to a given
 * {@link com.philbeaudoin.quebec.shared.statechange.LeaderDestination LeaderDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class SceneLeaderDestinationGenerator implements LeaderDestinationVisitor {

  private final RendererFactories rendererFactory;
  private SceneLeaderDestination sceneLeaderDestination;

  @Inject
  public SceneLeaderDestinationGenerator(RendererFactories rendererFactory) {
    this.rendererFactory = rendererFactory;
  }

  /**
   * Returns the {@link SceneLeaderDestination} generated by this factory.
   * @return The scene leader destination generated by this factory, or {@code null} if the factory
   *     has not visited anything yet.
   */
  public SceneLeaderDestination getSceneLeaderDestination() {
    return sceneLeaderDestination;
  }

  @Override
  public void visit(LeaderDestinationPlayer host) {
    sceneLeaderDestination = rendererFactory.createSceneLeaderDestinationPlayer(host);
  }

  @Override
  public void visit(LeaderDestinationBoard host) {
    sceneLeaderDestination = rendererFactory.createSceneLeaderDestinationBoard(host);
  }
}
