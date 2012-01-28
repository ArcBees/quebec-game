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

import com.philbeaudoin.quebec.shared.statechange.AcceptGameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.statechange.GameStateChangeVisitor;

/**
 * Use this class to generate the {@link ChangeRenderer} corresponding to a given
 * {@link GameStateChange}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererGenerator implements GameStateChangeVisitor {

  private final RendererFactories factories;
  private ChangeRenderer changeRenderer;

  @Inject
  public ChangeRendererGenerator(RendererFactories rendererFactory) {
    this.factories = rendererFactory;
  }

  /**
   * Returns the {@link ChangeRenderer} generated by this factory.
   * @return The change renderer generated by this factory, or {@code null} if the factory has not
   *     visited anything yet.
   */
  public ChangeRenderer getChangeRenderer() {
    return changeRenderer;
  }

  @Override
  public void visit(GameStateChangeComposite host) {
    final ChangeRendererComposite result = factories.createChangeRendererComposite();

    host.callOnEach(new AcceptGameStateChange() {
      @Override
      public void execute(GameStateChange gameStateChange) {
        ChangeRendererGenerator generator = factories.createChangeRendererGenerator();
        gameStateChange.accept(generator);
        result.add(generator.getChangeRenderer());
      }});

    changeRenderer = result;
  }

  @Override
  public void visit(GameStateChangeMoveCubes host) {
    SceneCubeDestinationGenerator generatorFrom =
        factories.createSceneCubeDestinationGenerator();
    host.getFrom().accept(generatorFrom);
    SceneCubeDestinationGenerator generatorTo =
        factories.createSceneCubeDestinationGenerator();
    host.getTo().accept(generatorTo);
    changeRenderer = factories.createChangeRendererMoveCubes(host.getNbCubes(),
        generatorFrom.getSceneCubeDestination(),
        generatorTo.getSceneCubeDestination());
  }

  @Override
  public void visit(GameStateChangeFlipTile host) {
    // Nothing to do.
  }

  @Override
  public void visit(GameStateChangeMoveArchitect host) {
    SceneArchitectDestinationGenerator generatorFrom =
        factories.createSceneArchitectDestinationGenerator();
    host.getFrom().accept(generatorFrom);
    SceneArchitectDestinationGenerator generatorTo =
        factories.createSceneArchitectDestinationGenerator();
    host.getTo().accept(generatorTo);
    changeRenderer = factories.createChangeRendererMoveArchitect(
        generatorFrom.getSceneArchitectDestination(),
        generatorTo.getSceneArchitectDestination());
  }

  @Override
  public void visit(GameStateChangeNextPlayer host) {
    changeRenderer = factories.createChangeRendererNull();
  }
}
