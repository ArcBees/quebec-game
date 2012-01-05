/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.sprites;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.client.utils.PawnStack;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * A scene node that contains all the elements representing the state of a given player.
 *
 * @author beaudoin
 */
public class PlayerZone extends SceneNodeImpl {
  private static final String COLOR[][] = {
      {"#666", "#999"},
      {"#E0E0E0", "#F8F8F8"},
      {"#ffce87", "#ffe4bc"},
      {"#48e11f", "#e1f19f"},
      {"#d7c0e4", "#e1d6ee"}
  };

  private final SceneNodeList sceneNodes = new SceneNodeList();
  private final CubeGrid cubeGrid = new CubeGrid(9, 3);
  private final PlayerState playerState;
  private final double sizeX;
  private final double sizeY;
  private final SceneNodeList passive;
  private final SceneNodeList active;
  private final SceneNodeList pawns;

  public PlayerZone(PlayerState playerState, double sizeX,
      double sizeY, Transformation transformation) {
    super(transformation);
    this.playerState = playerState;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    passive = new SceneNodeList(
        new Transformation(new Vector2d(sizeX * 0.16, sizeY * 0.6)));
    sceneNodes.add(passive);
    active = new SceneNodeList(
        new Transformation(new Vector2d(sizeX * 0.4, sizeY * 0.6)));
    sceneNodes.add(active);
    pawns = new SceneNodeList(
        new Transformation(new Vector2d(sizeX * 0.64, sizeY * 0.6)));
    sceneNodes.add(pawns);
  }

  public void generateContent(SpriteResources spriteResources) {
    PlayerColor color = playerState.getPlayer().getColor();
    for (int i = 0; i < playerState.getNbActiveCubes(); ++i) {
      int column = i % 9;
      int line = i / 9;
      Sprite cube = new Sprite(spriteResources.getCube(color),
          new Transformation(cubeGrid.getPosition(column, line)));
      active.add(cube);
    }

    for (int i = 0; i < playerState.getNbPassiveCubes(); ++i) {
      int column = i % 9;
      int line = i / 9;
      Sprite cube = new Sprite(spriteResources.getCube(color),
          new Transformation(cubeGrid.getPosition(column, line)));
      passive.add(cube);
    }

    int nbPawns = 0;
    if (playerState.isHoldingArchitect()) {
      nbPawns++;
    }
    if (playerState.isHoldingNeutralArchitect()) {
      nbPawns++;
    }
    if (nbPawns > 0) {
      PawnStack pawnStack = new PawnStack(nbPawns);
      int index = 0;
      if (playerState.isHoldingArchitect()) {
        Sprite pawn = new Sprite(spriteResources.getPawn(color),
            new Transformation(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
      if (playerState.isHoldingNeutralArchitect()) {
        Sprite pawn = new Sprite(spriteResources.getPawn(PlayerColor.NEUTRAL),
            new Transformation(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
    }

    if (playerState.getLeaderCard() != null) {
      Sprite card = new Sprite(spriteResources.getLeader(
          playerState.getLeaderCard().getInfluenceType()),
          new Transformation(new Vector2d(sizeX * 0.8, sizeY * 0.5)));
      sceneNodes.add(card);
    }
  }

  @Override
  public void render(Context2d context) {
    PlayerColor color = playerState.getPlayer().getColor();
    context.save();
    try {
      getTransformation().applies(context);
      CanvasGradient gradient = context.createLinearGradient(0, 0, 0, sizeY);
      int index = color.ordinal() - 1;
      gradient.addColorStop(0, COLOR[index][0]);
      gradient.addColorStop(1, COLOR[index][1]);
      context.setFillStyle(gradient);
      context.fillRect(0, 0, sizeX, sizeY);
      double x0 = 0.3 * sizeX, x1 = 0.5 * sizeX, y0 = 0.28 * sizeY, y1 = 0.9 * sizeY;
      gradient = context.createLinearGradient(0, y0, 0, y1);
      gradient.addColorStop(0, COLOR[index][1]);
      gradient.addColorStop(1, COLOR[index][0]);
      context.setFillStyle(gradient);
      context.fillRect(x0, y0, x1 - x0, y1 - y0);
      context.strokeRect(x0, y0, x1 - x0, y1 - y0);

      context.setFillStyle("#000");
      context.save();
      try {
        context.scale(0.001, 0.001);
        context.setFont("25px arial");
        context.fillText(playerState.getPlayer().getName(), 12, 30);
      } finally {
        context.restore();
      }
      sceneNodes.render(context);
    } finally {
      context.restore();
    }
  }
}
