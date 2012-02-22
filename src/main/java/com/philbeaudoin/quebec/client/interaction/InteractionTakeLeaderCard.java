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

package com.philbeaudoin.quebec.client.interaction;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.MessageRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is an interaction with the game board for the action of taking a leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTakeLeaderCard extends InteractionWithAction {

  private final SceneNodeList arrows;

  @Inject
  public InteractionTakeLeaderCard(Scheduler scheduler, InteractionFactories interactionFactories,
      MessageRenderer messageRenderer,  @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted ActionTakeLeaderCard action) {
    super(scheduler, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetLeaderCard(gameStateRenderer, action),
        createActionMessage(gameState, messageRenderer), action.execute(gameState));

    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    arrows = new SceneNodeList();

    // Arrow to move card.
    Transform cardFrom = gameStateRenderer.getLeaderCardOnBoardTransform(action.getLeaderCard());
    Transform cardTo = gameStateRenderer.getLeaderCardOnPlayerTransform(playerColor);
    arrows.add(new Arrow(cardFrom.getTranslation(0), cardTo.getTranslation(0)));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.nbPlayerWithLeaders() > 0 &&
        gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      arrows.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(arrows);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    arrows.setParent(null);
  }

  private static MessageRenderer createActionMessage(GameState gameState,
      MessageRenderer messageRenderer) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    new Message.TakeThisLeaderCard(gameState.nbPlayerWithLeaders(),
        playerColor).accept(messageRenderer);
    return messageRenderer;
  }
}
