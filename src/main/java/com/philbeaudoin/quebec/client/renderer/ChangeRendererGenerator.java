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

import com.philbeaudoin.quebec.shared.game.statechange.AcceptGameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeFlipTile;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeIncreaseStarToken;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeInstantaneousDecorator;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveArchitect;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeMoveLeader;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeNextPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangePrepareAction;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangePrepareNextCentury;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeQueuePossibleActions;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeReinit;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeScorePoints;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeSetPlayer;
import com.philbeaudoin.quebec.shared.game.statechange.GameStateChangeVisitor;

/**
 * Use this class to generate the {@link ChangeRenderer} corresponding to a given
 * {@link GameStateChange}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererGenerator implements GameStateChangeVisitor<ChangeRenderer> {

  private final RendererFactories factories;
  private final SceneCubeDestinationGenerator sceneCubeDestinationGenerator;
  private final SceneArchitectDestinationGenerator sceneArchitectDestinationGenerator;
  private final SceneLeaderDestinationGenerator sceneLeaderDestinationGenerator;

  @Inject
  ChangeRendererGenerator(RendererFactories rendererFactory,
      SceneCubeDestinationGenerator sceneCubeDestinationGenerator,
      SceneArchitectDestinationGenerator sceneArchitectDestinationGenerator,
      SceneLeaderDestinationGenerator sceneLeaderDestinationGenerator) {
    this.factories = rendererFactory;
    this.sceneCubeDestinationGenerator = sceneCubeDestinationGenerator;
    this.sceneArchitectDestinationGenerator = sceneArchitectDestinationGenerator;
    this.sceneLeaderDestinationGenerator = sceneLeaderDestinationGenerator;
  }

  @Override
  public ChangeRenderer visit(GameStateChangeComposite host) {
    final ChangeRendererComposite result = new ChangeRendererComposite();
    host.callOnEach(new AcceptGameStateChange() {
      @Override
      public void execute(GameStateChange gameStateChange) {
        result.add(gameStateChange.accept(ChangeRendererGenerator.this));
      }});
    return result;
  }

  @Override
  public ChangeRenderer visit(GameStateChangeMoveCubes host) {
    return factories.createChangeRendererMoveCubes(host.getNbCubes(),
        host.getFrom().accept(sceneCubeDestinationGenerator),
        host.getTo().accept(sceneCubeDestinationGenerator));
  }

  @Override
  public ChangeRenderer visit(GameStateChangeFlipTile host) {
    return factories.createChangeRendererFlipTile(host);
  }

  @Override
  public ChangeRenderer visit(GameStateChangeIncreaseStarToken host) {
    return factories.createChangeRendererIncreaseStarToken(host);
  }

  @Override
  public ChangeRenderer visit(GameStateChangeMoveArchitect host) {
    return factories.createChangeRendererMoveArchitect(
        host.getFrom().accept(sceneArchitectDestinationGenerator),
        host.getTo().accept(sceneArchitectDestinationGenerator));
  }

  @Override
  public ChangeRenderer visit(GameStateChangeNextPlayer host) {
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangeMoveLeader host) {
    return factories.createChangeRendererMoveLeader(
        host.getFrom().accept(sceneLeaderDestinationGenerator),
        host.getTo().accept(sceneLeaderDestinationGenerator));
  }

  @Override
  public ChangeRenderer visit(GameStateChangePrepareAction host) {
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangeQueuePossibleActions host) {
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangeScorePoints host) {
    // TODO(beaudoin): We should have an animation here.
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangeSetPlayer host) {
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangePrepareNextCentury host) {
    return new ChangeRendererNull();
  }

  @Override
  public ChangeRenderer visit(GameStateChangeInstantaneousDecorator host) {
    return new ChangeRendererInstantaneousDecorator(host.getDecorated().accept(this));
  }

  @Override
  public ChangeRenderer visit(GameStateChangeReinit host) {
    return new ChangeRendererNull();
  }
}
