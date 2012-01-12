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

import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.client.utils.CubeGrid;
import com.philbeaudoin.quebec.shared.Board;
import com.philbeaudoin.quebec.shared.BoardAction;
import com.philbeaudoin.quebec.shared.GameState;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.InfluenceZoneState.PlayerCubes;
import com.philbeaudoin.quebec.shared.LeaderCard;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.Tile;
import com.philbeaudoin.quebec.shared.TileState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransformation;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer for the state of the board. Keeps track of the rendered objects so they can be
 * animated.
 * @author Philippe Beaudoin
 */
class BoardRenderer {

  public static final double WIDTH = 1.318209;

  private final SceneNodeList boardRoot;
  private final SceneNodeList tileGrid[][] = new SceneNodeList[18][8];

  BoardRenderer(double leftPosition) {
    boardRoot = new SceneNodeList(
        new ConstantTransformation(new Vector2d(leftPosition + 0.5 * WIDTH, 0.5)));
  }

  /**
   * Renders the board state.
   * @param gameState The desired game state.
   * @param spriteResources The resources.
   * @param root The global root scene node.
   */
  public void render(GameState gameState, SpriteResources spriteResources, SceneNodeList root) {
    // Clear everything first.
    boardRoot.clear();
    Sprite boardSprite = new Sprite(spriteResources.get(SpriteResources.Type.board));
    boardRoot.add(boardSprite);
    root.add(boardRoot);

    renderCards(gameState, spriteResources);
    renderInfluenceZones(gameState, spriteResources);
    renderTiles(gameState, spriteResources);
  }

  /**
   * Access the board root created by this renderer.
   * @return The rendered board root.
   */
  public SceneNodeList getBoardRoot() {
    return boardRoot;
  }

  private void renderCards(GameState gameState, SpriteResources spriteResources) {
    for (LeaderCard leaderCard : gameState.getAvailableLeaderCards()) {
      InfluenceType influenceType = leaderCard.getInfluenceType();
      double x = 0.068 * influenceType.ordinal() + 0.09;
      Sprite card = new Sprite(spriteResources.getLeader(influenceType),
          new ConstantTransformation(new Vector2d(x, -0.3)));
      boardRoot.add(card);
    }
  }

  private void renderInfluenceZones(GameState gameState,  SpriteResources spriteResources) {
    CubeGrid zoneGrid = new CubeGrid(25, 5, 0.03);
    for (InfluenceType influenceType : InfluenceType.values()) {
      // Add the main node for the influence zone.
      Vector2d translation;
      if (influenceType == InfluenceType.CITADEL) {
        translation = new Vector2d(0.45, -0.05);
      } else {
        int index = influenceType.ordinal();
        translation = new Vector2d(0.51 * ((index % 2 == 0) ? 1 : -1),
                                   0.35 * ((index / 2 == 0) ? 1 : -1));
      }
      SceneNodeList cubesInZoneNode = new SceneNodeList(new ConstantTransformation(translation));
      boardRoot.add(cubesInZoneNode);

      // Add cubes to the main node.
      boolean reverseLineOrder = influenceType == InfluenceType.RELIGIOUS ||
          influenceType == InfluenceType.POLITIC;
      int line = reverseLineOrder ? 4 : 0;
      for (PlayerCubes playerCubes : gameState.getPlayerCubesInInfluenceZone(influenceType)) {
        assert playerCubes.playerColor != PlayerColor.NONE &&
            playerCubes.playerColor != PlayerColor.NEUTRAL;
        for (int i = 0; i < playerCubes.cubes; ++i) {
          Sprite cube = new Sprite(spriteResources.getCube(playerCubes.playerColor),
              new ConstantTransformation(zoneGrid.getPosition(i, line)));
          cubesInZoneNode.add(cube);
        }
        line += reverseLineOrder ? -1 : 1;
      }
    }
  }

  private void renderTiles(GameState gameState, SpriteResources spriteResources) {
    for (TileState tileState : gameState.getTileStates()) {
      int column = tileState.getLocation().getColumn();
      int line = tileState.getLocation().getLine();

      // Add the main node for the tile.
      BoardAction boardAction = Board.actionForTileLocation(column, line);
      assert boardAction != null;
      Transformation tileTransformation = getTileTransformation(column, line, 1.0,
          !tileState.isBuildingFacing());
      SceneNodeList tileNode = new SceneNodeList(tileTransformation);
      boardRoot.add(tileNode);
      tileGrid[column][line] = tileNode;

      // Render the tile itself.
      if (tileState.isBuildingFacing()) {
        renderBuildingTile(tileState, tileNode, spriteResources);
      } else {
        renderTile(tileState, tileNode, spriteResources, tileTransformation.getRotation(0.0));
      }
    }
  }

  private void renderTile(TileState tileState, SceneNodeList tileNode,
      SpriteResources spriteResources, double rotation) {
    Tile tile = tileState.getTile();
    Sprite tileSprite = new Sprite(spriteResources.getTile(tile.getInfluenceType(),
        tile.getCentury()));
    tileNode.add(tileSprite);

    // TODO: Add the current century marker.

    // Add the architect pawn.
    PlayerColor architectColor = tileState.getArchitect();
    if (architectColor != PlayerColor.NONE) {
      SceneNodeList architectNode = new SceneNodeList(
          new ConstantTransformation(new Vector2d(0, -0.0225), 1.0, -rotation));
      tileNode.add(architectNode);
      Sprite architectSprite = new Sprite(spriteResources.getPawn(architectColor),
          new ConstantTransformation(new Vector2d(0, -0.01)));
      architectNode.add(architectSprite);
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
        SceneNodeList cubes = new SceneNodeList(new ConstantTransformation(new Vector2d(x, y), 1.0,
            -rotation));
        tileNode.add(cubes);
        // Add all the cubes to the node.
        for (int cubeIndex = 0; cubeIndex < cubesPerSpot; ++cubeIndex) {
          Sprite cubeSprite = new Sprite(spriteResources.getCube(cubesColor),
              new ConstantTransformation(cubeGrid.getPosition(cubeIndex, 0)));
          cubes.add(cubeSprite);
        }
      }
    }
  }

  private void renderBuildingTile(TileState tileState, SceneNodeList tileNode,
      SpriteResources spriteResources) {
    Tile tile = tileState.getTile();
    Sprite tileSprite = new Sprite(spriteResources.getBuildingTile(tile.getInfluenceType(),
        tile.getCentury(), tile.getBuildingIndex()));
    tileNode.add(tileSprite);

    // TODO: Render star marker.
  }

  private Transformation getTileTransformation(int column, int line, double scaling,
      boolean applyRotation) {
    Vector2d translation = Board.positionForLocation(column, line);
    double rotation = applyRotation ? Board.rotationAngleForLocation(column, line) : 0;
    return new ConstantTransformation(translation, scaling, rotation);
  }

}
