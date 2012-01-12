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

package com.philbeaudoin.quebec.client.renderer;

import com.philbeaudoin.quebec.client.scene.Rectangle;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.client.scene.Text;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.client.utils.PawnStack;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.PlayerState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a player state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin
 */
class PlayerStateRenderer {
  private static final String COLOR[][] = {
      {"#666", "#999"},
      {"#E0E0E0", "#F8F8F8"},
      {"#ffce87", "#ffe4bc"},
      {"#48e11f", "#e1f19f"},
      {"#d7c0e4", "#e1d6ee"}
  };

  private final SceneNodeList playerZone;
  private final CubeGrid cubeGrid = new CubeGrid(9, 3);
  private final double width;
  private final double height;
  private final SceneNodeList passiveCubes;
  private final SceneNodeList activeCubes;
  private final SceneNodeList pawns;
  private final ScoreRenderer scoreRenderer;

  /**
   * Creates an object to hold the rendered state of a player zone. This will only be valid once
   * {@link #init} is called.
   * @param width The width of the player zone.
   * @param height The height of the player zone.
   * @param transformation
   */
  PlayerStateRenderer(double width, double height, Transformation transformation,
      ScoreRenderer scoreRenderer) {
    playerZone = new SceneNodeList(transformation);
    this.width = width;
    this.height = height;
    passiveCubes = new SceneNodeList(
        new ConstantTransformation(new Vector2d(width * 0.16, height * 0.6)));
    activeCubes = new SceneNodeList(
        new ConstantTransformation(new Vector2d(width * 0.4, height * 0.6)));
    pawns = new SceneNodeList(
        new ConstantTransformation(new Vector2d(width * 0.61, height * 0.55)));
    this.scoreRenderer = scoreRenderer;
  }

  /**
   * Renders the player state.
   * @param playerState The desired player state.
   * @param spriteResources The resources.
   * @param root The global root scene node.
   * @param boardRoot The root scene node of the board.
   */
  public void render(PlayerState playerState, SpriteResources spriteResources, SceneNodeList root,
      SceneNodeList boardRoot) {
    PlayerColor color = playerState.getPlayer().getColor();
    int paletteIndex = color.ordinal() - 1;

    playerZone.clear();
    String contourColor = playerState.isCurrentPlayer() ? "#000" : null;
    Rectangle mat = new Rectangle(0, 0, width, height,
        COLOR[paletteIndex][0], COLOR[paletteIndex][1], contourColor, 6);
    playerZone.add(mat);
    Rectangle activeZone = new Rectangle(0.3 * width, 0.28 * height, 0.5 * width, 0.9 * height,
        COLOR[paletteIndex][1], COLOR[paletteIndex][0], "#000", 1);
    playerZone.add(activeZone);
    Text playerName = new Text(playerState.getPlayer().getName(),
        new ConstantTransformation(new Vector2d(0.022 * width, 0.18 * height)));
    playerZone.add(playerName);
    Text playerScore = new Text(Integer.toString(playerState.getScore()),
        new ConstantTransformation(new Vector2d(0.85 * width, 0.18 * height)));
    playerZone.add(playerScore);

    playerZone.add(passiveCubes);
    playerZone.add(activeCubes);
    playerZone.add(pawns);

    for (int i = 0; i < playerState.getNbActiveCubes(); ++i) {
      int column = i % 9;
      int line = i / 9;
      Sprite cube = new Sprite(spriteResources.getCube(color),
          new ConstantTransformation(cubeGrid.getPosition(column, line)));
      activeCubes.add(cube);
    }

    for (int i = 0; i < playerState.getNbPassiveCubes(); ++i) {
      int column = i % 9;
      int line = i / 9;
      Sprite cube = new Sprite(spriteResources.getCube(color),
          new ConstantTransformation(cubeGrid.getPosition(column, line)));
      passiveCubes.add(cube);
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
            new ConstantTransformation(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
      if (playerState.isHoldingNeutralArchitect()) {
        Sprite pawn = new Sprite(spriteResources.getPawn(PlayerColor.NEUTRAL),
            new ConstantTransformation(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
    }

    if (playerState.getLeaderCard() != null) {
      Sprite card = new Sprite(spriteResources.getLeader(
          playerState.getLeaderCard().getInfluenceType()),
          new ConstantTransformation(new Vector2d(width * 0.8, height * 0.58)));
      playerZone.add(card);
    }

    root.add(playerZone);

    scoreRenderer.renderPlayer(playerState, spriteResources, boardRoot);
  }
}
