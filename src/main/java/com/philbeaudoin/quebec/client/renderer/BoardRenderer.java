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
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.client.scene.SpriteResources.Type;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.BoardAction;
import com.philbeaudoin.quebec.shared.GameState;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.LeaderCard;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.Tile;
import com.philbeaudoin.quebec.shared.TileState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer for the state of the board. Keeps track of the rendered objects so they can be
 * animated.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class BoardRenderer {

  private static class CubeStack {
    List<SceneNode> cubes;
    PlayerColor playerColor;
    CubeStack() {
      cubes = new ArrayList<SceneNode>();
      playerColor = PlayerColor.NONE;
    }
  }

  public static final double WIDTH = 1.318209;

  private final SpriteResources spriteResources;

  private final CubeGrid zoneGrid = new CubeGrid(25, 5, 0.03);

  private final SceneNodeList backgroundBoardRoot;
  private final SceneNodeList foregroundBoardRoot;
  private final SceneNodeList tileGrid[][] = new SceneNodeList[18][8];

  private final SceneNodeList[] influenceZoneNode = new SceneNodeList[5];
  private final CubeStack[][] cubeStacksInZone = new CubeStack[5][5];  // [influenceType][line]

  @Inject
  BoardRenderer(SpriteResources spriteResources, @Assisted double leftPosition) {
    this.spriteResources = spriteResources;
    backgroundBoardRoot = new SceneNodeList(
        new ConstantTransform(new Vector2d(leftPosition + 0.5 * WIDTH, 0.5)));
    foregroundBoardRoot = new SceneNodeList(
        new ConstantTransform(new Vector2d(leftPosition + 0.5 * WIDTH, 0.5)));
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        cubeStacksInZone[i][j] = new CubeStack();
      }
    }
  }

  /**
   * Renders the board state.
   * @param gameState The desired game state.
   * @param backgroundRoot The root of the objects behind the glass screen.
   * @param foregroundRoot The root of the objects in front of the glass screen.
   */
  public void render(GameState gameState, SceneNodeList backgroundRoot,
      SceneNodeList foregroundRoot) {
    // Clear everything first.
    backgroundBoardRoot.clear();
    Sprite boardSprite = new Sprite(spriteResources.get(SpriteResources.Type.board));
    backgroundBoardRoot.add(boardSprite);
    backgroundRoot.add(backgroundBoardRoot);
    foregroundBoardRoot.clear();
    foregroundRoot.add(foregroundBoardRoot);

    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        cubeStacksInZone[i][j].cubes.clear();
        cubeStacksInZone[i][j].playerColor = PlayerColor.NONE;
      }
    }

    renderCards(gameState);
    renderInfluenceZones(gameState);
    renderTiles(gameState);
  }

  /**
   * Access the board root rendered behind the glass screen created by this renderer.
   * @return The rendered board root.
   */
  public SceneNodeList getBackgroundBoardRoot() {
    return backgroundBoardRoot;
  }

  private void renderCards(GameState gameState) {
    for (LeaderCard leaderCard : gameState.getAvailableLeaderCards()) {
      InfluenceType influenceType = leaderCard.getInfluenceType();
      double x = 0.068 * influenceType.ordinal() + 0.09;
      Sprite card = new Sprite(spriteResources.getLeader(influenceType),
          new ConstantTransform(new Vector2d(x, -0.3)));
      // TODO: Determine foreground/background.
      foregroundBoardRoot.add(card);
    }
  }

  private void renderInfluenceZones(GameState gameState) {
    for (InfluenceType influenceType : InfluenceType.values()) {
      int index = influenceType.ordinal();
      // Add the main node for the influence zone.
      Vector2d translation;
      if (influenceType == InfluenceType.CITADEL) {
        translation = new Vector2d(0.45, -0.05);
      } else {
        translation = new Vector2d(0.51 * ((index % 2 == 0) ? 1 : -1),
                                   0.35 * ((index / 2 == 0) ? 1 : -1));
      }
      influenceZoneNode[index] = new SceneNodeList(new ConstantTransform(translation));
      backgroundBoardRoot.add(influenceZoneNode[index]);

      for (PlayerColor playerColor : PlayerColor.values()) {
        if (playerColor.isNormalColor()) {
          addCubesToInfluenceZone(influenceType, playerColor,
              gameState.getPlayerCubesInInfluenceZone(influenceType, playerColor));
        }
      }
    }
  }

  private void renderTiles(GameState gameState) {
    for (TileState tileState : gameState.getTileStates()) {
      int column = tileState.getLocation().getColumn();
      int line = tileState.getLocation().getLine();

      // Add the main node for the tile.
      BoardAction boardAction = Board.actionForTileLocation(column, line);
      assert boardAction != null;
      Transform tileTransform = getTileTransform(column, line, 1.0,
          !tileState.isBuildingFacing());
      SceneNodeList tileNode = new SceneNodeList(tileTransform);
      backgroundBoardRoot.add(tileNode);
      tileGrid[column][line] = tileNode;

      // Render the tile itself.
      if (tileState.isBuildingFacing()) {
        renderBuildingTile(tileState, tileNode);
      } else {
        renderTile(gameState, tileState, tileNode, tileTransform.getRotation(0.0));
      }
    }
  }

  private void renderTile(GameState gameState, TileState tileState, SceneNodeList tileNode,
      double rotation) {
    Tile tile = tileState.getTile();
    Sprite tileSprite = new Sprite(spriteResources.getTile(tile.getInfluenceType(),
        tile.getCentury()));
    tileNode.add(tileSprite);

    // Add the architect pawn.
    PlayerColor architectColor = tileState.getArchitect();
    if (architectColor != PlayerColor.NONE) {
      SceneNodeList architectNode = new SceneNodeList(
          new ConstantTransform(new Vector2d(0, -0.0225), 1.0, -rotation));
      tileNode.add(architectNode);
      Sprite architectSprite = new Sprite(spriteResources.getPawn(architectColor),
          new ConstantTransform(new Vector2d(0, -0.01)));
      architectNode.add(architectSprite);
    } else {
      // Add the active token if needed.
      if (tile.getCentury() == gameState.getCentury()) {
        Sprite activeTokenSprite = new Sprite(spriteResources.get(Type.activeToken),
            new ConstantTransform(new Vector2d(0, -0.0225), 1.0, -rotation));
        tileNode.add(activeTokenSprite);
      }
    }

    // Add the cubes.
    int cubesPerSpot = tileState.cubesPerSpot();
    CubeGrid cubeGrid = new CubeGrid(cubesPerSpot, 1);
    for (int spot = 0; spot < 3; ++spot) {
      PlayerColor cubesColor = tileState.getColorInSpot(spot);
      if (cubesColor != PlayerColor.NONE) {
        // Add the node to hold these cubes.
        double x = -0.0225 + spot * 0.0225;
        double y = spot == 1 ? 0.0225 : 0;
        SceneNodeList cubes = new SceneNodeList(new ConstantTransform(new Vector2d(x, y), 1.0,
            -rotation));
        tileNode.add(cubes);
        // Add all the cubes to the node.
        for (int cubeIndex = 0; cubeIndex < cubesPerSpot; ++cubeIndex) {
          Sprite cubeSprite = new Sprite(spriteResources.getCube(cubesColor),
              new ConstantTransform(cubeGrid.getPosition(cubeIndex, 0)));
          cubes.add(cubeSprite);
        }
      }
    }
  }

  private void renderBuildingTile(TileState tileState, SceneNodeList tileNode) {
    Tile tile = tileState.getTile();
    Sprite tileSprite = new Sprite(spriteResources.getBuildingTile(tile.getInfluenceType(),
        tile.getCentury(), tile.getBuildingIndex()));
    tileNode.add(tileSprite);

    // TODO: Render star marker.
  }

  private Transform getTileTransform(int column, int line, double scaling,
      boolean applyRotation) {
    Vector2d translation = Board.positionForLocation(column, line);
    double rotation = applyRotation ? Board.rotationAngleForLocation(column, line) : 0;
    return new ConstantTransform(translation, scaling, rotation);
  }

  /**
   * Remove a given number of cubes of a given player from a given influence zone and return the
   * transforms of the removed cubes.
   * @param influenceType The influence type of the zone to remove cubes from.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param nbCubes The number of cubes to remove, cannot be more than what is in the zone.
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> removeCubesFromInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor, int nbCubes) {
    assert playerColor.isNormalColor();
    assert nbCubes >= 0;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    if (nbCubes == 0) {
      return result;
    }
    int index = influenceType.ordinal();
    int line = findLineForColor(influenceType, playerColor);
    List<SceneNode> cubes = cubeStacksInZone[index][line].cubes;
    assert cubes.size() >= nbCubes;
    int newNbCubes = cubes.size() - nbCubes;

    ConstantTransform parentTransform = influenceZoneNode[index].getTotalTransform(0);
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
   * @param influenceType The influence type of the zone to add cubes to.
   * @param playerColor The color of the player whose cube to add (not NONE or NEUTRAL).
   * @param nbCubes The number of cubes to add, must be positive or 0.
   * @return The list of global transforms of the added cubes.
   */
  public List<Transform> addCubesToInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor, int nbCubes) {
    assert playerColor.isNormalColor();
    assert nbCubes >= 0;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    if (nbCubes == 0) {
      return result;
    }
    int index = influenceType.ordinal();
    int line = findLineForColor(influenceType, playerColor);
    cubeStacksInZone[index][line].playerColor = playerColor;
    List<SceneNode> cubes = cubeStacksInZone[index][line].cubes;
    int newNbCubes = cubes.size() + nbCubes;

    ConstantTransform parentTransform = influenceZoneNode[index].getTotalTransform(0);
    for (int i = cubes.size(); i < newNbCubes; ++i) {
      ConstantTransform childTransform = new ConstantTransform(zoneGrid.getPosition(i, line));
      Sprite cube = new Sprite(spriteResources.getCube(playerColor), childTransform);
      cubes.add(cube);
      result.add(parentTransform.times(childTransform));
      influenceZoneNode[index].add(cube);
    }
    return result;
  }

  /**
   * Call this method to reset the color associated with each line in each influence zone so that
   * lines that have no cubes can take any color. Be aware that undoing a move after that can leave
   * the rendered state slightly different from the original state.
   */
  public void resetColorForInfluenceZoneLines() {
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        if (cubeStacksInZone[i][j].cubes.isEmpty()) {
          cubeStacksInZone[i][j].playerColor = PlayerColor.NONE;
        }
      }
    }
  }

  /**
   * Find the line on which to place cubes of the given color in the given influence zone.
   * @param influenceType The influence zone in which to find a line for a color.
   * @param playerColor The color of cubes for which to find the line.
   */
  private int findLineForColor(InfluenceType influenceType, PlayerColor playerColor) {
    int index = influenceType.ordinal();
    boolean reverseLineOrder = influenceType == InfluenceType.RELIGIOUS ||
        influenceType == InfluenceType.POLITIC;
    int line = reverseLineOrder ? 4 : 0;
    int chosenLine = -1;
    for (int i = 0; i < 5; ++i) {
      PlayerColor colorOnLine = cubeStacksInZone[index][line].playerColor;
      if (colorOnLine == playerColor) {
        chosenLine = line;
        break;
      } else if (chosenLine == -1 && colorOnLine == PlayerColor.NONE) {
        // Keep the first empty line found, but keep looking for this color.
        chosenLine = line;
      }
      line += reverseLineOrder ? -1 : 1;
    }
    assert chosenLine != -1;
    return chosenLine;
  }
}
