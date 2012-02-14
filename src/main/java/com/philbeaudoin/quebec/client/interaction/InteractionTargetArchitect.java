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

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNodeAnimation;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.ActionMoveArchitect;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.TileState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * This is a target for interactions occurring on a given architect pawn.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class InteractionTargetArchitect implements InteractionTarget {

  private final GameStateRenderer gameStateRenderer;
  private final PlayerColor architectColor;
  private final SceneNodeAnimation pawnAnimation;
  private final Trigger trigger;
  private final Sprite pawn;

  // TODO(beaudoin): Lot of code duplicated to with InteractionMoveArchitect to identify architect
  //     transformation. Extract.
  @Inject
  InteractionTargetArchitect(SceneNodeAnimation.Factory sceneNodeAnimationFactory,
      SpriteResources spriteResources, @Assisted GameState gameState,
      @Assisted GameStateRenderer gameStateRenderer, @Assisted ActionMoveArchitect action) {
    this.gameStateRenderer = gameStateRenderer;
    PlayerColor playerColor = gameState.getCurrentPlayer().getPlayer().getColor();
    architectColor = action.isNeutralArchitect() ? PlayerColor.NEUTRAL : playerColor;
    TileState origin = gameState.findTileUnderArchitect(architectColor);

    Transform fromTransform;
    if (origin == null) {
      // Architect starts from the player zone.
      fromTransform = gameStateRenderer.getArchitectOnPlayerTransform(
          playerColor, action.isNeutralArchitect());
    } else {
      // Architect starts from its current tile.
      fromTransform = gameStateRenderer.getArchitectOnTileTransform(origin.getTile());
    }
    Transform toTransform = new ConstantTransform(
        fromTransform.getTranslation(0),
        fromTransform.getScaling(0) * 1.18,
        fromTransform.getRotation(0));

    pawn = new Sprite(spriteResources.getPawn(architectColor), fromTransform);
    pawnAnimation = sceneNodeAnimationFactory.create(gameStateRenderer, fromTransform,
        toTransform, pawn);

    trigger = new RectangleTrigger(fromTransform.getTranslation(0), 0.019, 0.031);
  }

  @Override
  public void highlight() {
    gameStateRenderer.forceGlassScreen();
    gameStateRenderer.addToAnimationGraph(pawn);
  }

  @Override
  public void onMouseEnter(double time) {
    pawnAnimation.startAnim(time);
  }

  @Override
  public void onMouseLeave(double time) {
    pawnAnimation.stopAnim(time);
  }

  @Override
  public Trigger getTrigger() {
    return trigger;
  }
}
