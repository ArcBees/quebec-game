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
import com.philbeaudoin.quebec.client.renderer.TextBoxRenderer;
import com.philbeaudoin.quebec.client.scene.Arrow;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.game.GameController;
import com.philbeaudoin.quebec.shared.game.action.ActionTakeLeaderCard;
import com.philbeaudoin.quebec.shared.game.state.GameState;
import com.philbeaudoin.quebec.shared.location.LeaderDestinationBoard;
import com.philbeaudoin.quebec.shared.location.LocationRelative;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * This is an interaction with the game board for the action of taking a leader card.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTakeLeaderCard extends InteractionWithAction {

  private final SceneNodeList extras;

  @Inject
  public InteractionTakeLeaderCard(Scheduler scheduler, InteractionFactories interactionFactories,
      TextBoxRenderer textBoxRenderer, @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted ActionTakeLeaderCard action,
      @Assisted GameController gameController) {
    super(scheduler, textBoxRenderer, gameState, gameStateRenderer,
        interactionFactories.createInteractionTargetLeaderCard(gameStateRenderer, action),
        createActionMessage(gameState), action, gameController);

    PlayerColor playerColor = gameState.getCurrentPlayer().getColor();
    extras = new SceneNodeList();

    // Arrow to move card.
    Transform cardFrom = gameStateRenderer.getLeaderCardOnBoardTransform(action.getLeaderCard());
    Transform cardTo = gameStateRenderer.getLeaderCardOnPlayerTransform(playerColor);
    extras.add(new Arrow(cardFrom.getTranslation(0), cardTo.getTranslation(0)));

    // Arrow to move passive cubes to active, if needed.
    if (gameState.nbPlayerWithLeaders() > 0 &&
        gameState.getCurrentPlayer().getNbPassiveCubes() > 0) {
      Transform cubeFrom = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, false);
      Transform cubeTo = gameStateRenderer.getPlayerCubeZoneTransform(playerColor, true);
      extras.add(new Arrow(cubeFrom.getTranslation(0), cubeTo.getTranslation(0)));
    }

    // Text describing the card.
    String methodName = null;
    switch (action.getLeaderCard()) {
    case RELIGIOUS:
      methodName = "religiousLeaderDescription";
      break;
    case POLITIC:
      methodName = "politicLeaderDescription";
      break;
    case ECONOMIC:
      methodName = "economicLeaderDescription";
      break;
    case CULTURAL_TWO_THREE:
      methodName = "cultural23LeaderDescription";
      break;
    case CULTURAL_FOUR_FIVE:
      methodName = "cultural45LeaderDescription";
      break;
    case CITADEL:
      methodName = "citadelLeaderDescription";
      break;
    default:
      assert false;
    }
    LeaderDestinationBoard location = new LeaderDestinationBoard(action.getLeaderCard());
    TextBoxInfo textBoxInfo = new TextBoxInfo(new Message.MultilineText(methodName),
        new LocationRelative(location, new Vector2d(0, 1)), location);
    extras.add(textBoxRenderer.render(textBoxInfo, gameStateRenderer));
  }

  @Override
  protected void doMouseEnter(double x, double y, double time) {
    super.doMouseEnter(x, y, time);
    gameStateRenderer.addToAnimationGraph(extras);
  }

  @Override
  protected void doMouseLeave(double x, double y, double time) {
    super.doMouseLeave(x, y, time);
    extras.setParent(null);
  }

  private static Message createActionMessage(GameState gameState) {
    PlayerColor playerColor = gameState.getCurrentPlayer().getColor();
    return new Message.TakeThisLeaderCard(gameState.nbPlayerWithLeaders(),
        playerColor);
  }
}
