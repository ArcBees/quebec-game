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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.scene.Rectangle;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.client.scene.Text;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.client.utils.PawnStack;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.PlayerState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a player state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class PlayerStateRenderer {
  private static final String COLOR[][] = {
      {"#666", "#999"},
      {"#E0E0E0", "#F8F8F8"},
      {"#ffce87", "#ffe4bc"},
      {"#48e11f", "#e1f19f"},
      {"#d7c0e4", "#e1d6ee"}
  };

  private final SpriteResources spriteResources;
  private final SceneNodeList playerZone;
  private final CubeGrid cubeGrid = new CubeGrid(9, 3);
  private final double width;
  private final double height;
  private final SceneNodeList passiveCubes;
  private final SceneNodeList activeCubes;
  private final SceneNodeList pawns;
  private final ScoreRenderer scoreRenderer;
  private final ArrayList<SceneNode> activeCubeStack = new ArrayList<SceneNode>();
  private final ArrayList<SceneNode> passiveCubeStack = new ArrayList<SceneNode>();

  private PlayerColor playerColor;

  /**
   * Creates an object to hold the rendered state of a player zone. This will only be valid once
   * {@link #init} is called.
   * @param width The width of the player zone.
   * @param height The height of the player zone.
   * @param transform
   */
  @Inject
  PlayerStateRenderer(
      SpriteResources spriteResources,
      @Assisted Vector2d size, @Assisted Transform transform,
      @Assisted ScoreRenderer scoreRenderer) {
    this.spriteResources = spriteResources;
    playerZone = new SceneNodeList(transform);
    this.width = size.getX();
    this.height = size.getY();
    passiveCubes = new SceneNodeList(
        new ConstantTransform(new Vector2d(width * 0.16, height * 0.6)));
    activeCubes = new SceneNodeList(
        new ConstantTransform(new Vector2d(width * 0.4, height * 0.6)));
    pawns = new SceneNodeList(
        new ConstantTransform(new Vector2d(width * 0.61, height * 0.55)));
    this.scoreRenderer = scoreRenderer;
    this.playerColor = PlayerColor.NONE;
  }

  /**
   * Access the player color of this renderer.
   * @return The player color, or NONE if no player has been rendered yet.
   */
  PlayerColor getPlayerColor() {
    return playerColor;
  }

  /**
   * Renders the player state.
   * @param playerState The desired player state.
   * @param root The global root scene node.
   * @param boardRoot The root scene node of the board.
   */
  public void render(PlayerState playerState, SceneNodeList root,
      SceneNodeList boardRoot) {
    playerColor = playerState.getPlayer().getColor();
    int paletteIndex = playerColor.ordinal() - 1;

    playerZone.clear();
    String contourColor = playerState.isCurrentPlayer() ? "#000" : null;
    Rectangle mat = new Rectangle(0, 0, width, height,
        COLOR[paletteIndex][0], COLOR[paletteIndex][1], contourColor, 6);
    playerZone.add(mat);
    Rectangle activeZone = new Rectangle(0.3 * width, 0.28 * height, 0.5 * width, 0.9 * height,
        COLOR[paletteIndex][1], COLOR[paletteIndex][0], "#000", 1);
    playerZone.add(activeZone);
    Text playerName = new Text(playerState.getPlayer().getName(),
        new ConstantTransform(new Vector2d(0.022 * width, 0.18 * height)));
    playerZone.add(playerName);
    Text playerScore = new Text(Integer.toString(playerState.getScore()),
        new ConstantTransform(new Vector2d(0.85 * width, 0.18 * height)));
    playerZone.add(playerScore);

    playerZone.add(passiveCubes);
    playerZone.add(activeCubes);
    playerZone.add(pawns);

    addCubesToPlayer(true, playerState.getNbActiveCubes());
    addCubesToPlayer(false, playerState.getNbPassiveCubes());

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
        Sprite pawn = new Sprite(spriteResources.getPawn(playerColor),
            new ConstantTransform(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
      if (playerState.isHoldingNeutralArchitect()) {
        Sprite pawn = new Sprite(spriteResources.getPawn(PlayerColor.NEUTRAL),
            new ConstantTransform(pawnStack.getPosition(index)));
        pawns.add(pawn);
        index++;
      }
    }

    if (playerState.getLeaderCard() != null) {
      Sprite card = new Sprite(spriteResources.getLeader(
          playerState.getLeaderCard().getInfluenceType()),
          new ConstantTransform(new Vector2d(width * 0.8, height * 0.58)));
      playerZone.add(card);
    }

    root.add(playerZone);

    scoreRenderer.renderPlayer(playerState, boardRoot);
  }

  /**
   * Remove a given number of cubes from a given player's active or passive reserve and return the
   * global transforms of the removed cubes.
   * @param active True to remove the cubes from the active reserve, false for the passive.
   * @param nbCubes The number of cubes to remove, must be more than what is in the reserve.
   * @return The list of global transforms of the added cubes.
   */
  public List<Transform> removeCubesFromPlayer(boolean active, int nbCubes) {
    assert nbCubes >= 0;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    if (nbCubes == 0) {
      return result;
    }
    List<SceneNode> cubes = active ? activeCubeStack : passiveCubeStack;
    SceneNodeList parent = active ? activeCubes : passiveCubes;
    assert cubes.size() >= nbCubes;
    int newNbCubes = cubes.size() - nbCubes;

    ConstantTransform parentTransform = parent.getTotalTransform(0);
    for (int i = 0; i < nbCubes; ++i) {
      SceneNode cube = cubes.remove(newNbCubes);
      result.add(parentTransform.times(cube.getTransform()));
      cube.setParent(null);
    }
    return result;
  }

  /**
   * Add a given number of cubes of a given player to a given influence zone and return the
   * global transforms of the newly added cubes.
   * @param active True to add the cubes from the active reserve, false for the passive.
   * @param nbCubes The number of cubes to add.
   * @return The list of global transforms of the added cubes.
   */
  public List<Transform> addCubesToPlayer(boolean active, int nbCubes) {
    assert nbCubes >= 0;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    if (nbCubes == 0) {
      return result;
    }
    List<SceneNode> cubes = active ? activeCubeStack : passiveCubeStack;
    SceneNodeList parent = active ? activeCubes : passiveCubes;
    int newNbCubes = cubes.size() + nbCubes;

    ConstantTransform parentTransform = parent.getTotalTransform(0);
    for (int i = cubes.size(); i < newNbCubes; ++i) {
      int column = i % 9;
      int line = i / 9;
      ConstantTransform childTransform = new ConstantTransform(cubeGrid.getPosition(column, line));
      Sprite cube = new Sprite(spriteResources.getCube(playerColor), childTransform);
      parent.add(cube);
      result.add(parentTransform.times(childTransform));
      cubes.add(cube);
    }
    return result;
  }
}
