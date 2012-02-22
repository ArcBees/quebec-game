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
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.state.TileState;
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

  private static class TileInfo {
    SceneNodeList root;
    Tile tile;
    double rotation;
    PlayerColor architectColor;
    SceneNode tileSprite;
    SceneNode architectNode;
    SceneNode activeTokenNode;
    CubeStack cubesOnSpot[] = new CubeStack[3];
    SceneNodeList starTokenNode;
    public TileInfo(SceneNodeList root, Tile tile, double rotation) {
      this.root = root;
      this.tile = tile;
      this.rotation = rotation;
      this.architectColor = PlayerColor.NONE;
      this.architectNode = null;
      this.activeTokenNode = null;
      for (int i = 0; i < 3; ++i) {
        cubesOnSpot[i] = new CubeStack();
      }
    }
  }

  public static final double WIDTH = 1.318209;

  private final SpriteResources spriteResources;

  private final CubeGrid zoneGrid = new CubeGrid(25, 5, 0.03);

  private final SceneNodeList backgroundBoardRoot;
  private final TileInfo tileGrid[][] = new TileInfo[18][8];

  private final SceneNodeList[] influenceZoneNode = new SceneNodeList[5];
  private final CubeStack[][] cubeStacksInZone = new CubeStack[5][5];  // [influenceType][line]

  private final SceneNode[] leaderCardNode = new SceneNode[5];

  @Inject
  BoardRenderer(SpriteResources spriteResources, @Assisted double leftPosition) {
    this.spriteResources = spriteResources;
    backgroundBoardRoot = new SceneNodeList(
        new ConstantTransform(new Vector2d(leftPosition + 0.5 * WIDTH, 0.5)));
    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        cubeStacksInZone[i][j] = new CubeStack();
      }
    }

    for (InfluenceType influenceType : InfluenceType.values()) {
      int index = influenceType.ordinal();
      // Add the main node for the influence zone.
      Vector2d translation;
      if (influenceType == InfluenceType.CITADEL) {
        translation = new Vector2d(0.45, -0.05);
      } else {
        translation = new Vector2d(0.51 * (((index + 1) % 4 / 2 == 0) ? 1 : -1),
                                   0.35 * ((index / 2 == 0) ? 1 : -1));
      }
      influenceZoneNode[index] = new SceneNodeList(new ConstantTransform(translation));
    }
  }

  /**
   * Renders the board state.
   * @param gameState The desired game state.
   * @param backgroundRoot The root of the objects behind the glass screen.
   */
  public void render(GameState gameState, SceneNodeList backgroundRoot) {
    // Clear everything first.
    backgroundBoardRoot.clear();
    Sprite boardSprite = new Sprite(spriteResources.get(SpriteResources.Type.board));
    backgroundBoardRoot.add(boardSprite);
    backgroundRoot.add(backgroundBoardRoot);

    for (int i = 0; i < 18; ++i) {
      for (int j = 0; j < 8; ++j) {
        tileGrid[i][j] = null;
      }
    }

    for (SceneNodeList node : influenceZoneNode) {
      node.clear();
      backgroundBoardRoot.add(node);
    }

    for (int i = 0; i < 5; ++i) {
      for (int j = 0; j < 5; ++j) {
        cubeStacksInZone[i][j].cubes.clear();
      }
    }

    for (int i = 0; i < 5; ++i) {
      leaderCardNode[i] = null;
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
      addLeaderCard(leaderCard);
    }
  }

  private void renderInfluenceZones(GameState gameState) {
    for (InfluenceType influenceType : InfluenceType.values()) {
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
      double rotation = tileTransform.getRotation(0);
      tileGrid[column][line] = new TileInfo(tileNode, tileState.getTile(), rotation);

      // Render the tile itself.
      TileInfo tileInfo = tileGrid[column][line];
      if (tileState.isBuildingFacing()) {
        renderBuildingTile(tileInfo, tileState, tileNode);
      } else {
        renderTile(gameState, tileInfo, tileState);
      }
    }
  }

  private void renderTile(GameState gameState, TileInfo tileInfo, TileState tileState) {
    Tile tile = tileState.getTile();
    tileInfo.tileSprite = new Sprite(spriteResources.getTile(tile.getInfluenceType(),
        tile.getCentury()));
    tileInfo.root.add(tileInfo.tileSprite);

    // Add the architect pawn.
    PlayerColor architectColor = tileState.getArchitect();
    if (architectColor.isArchitectColor()) {
      addArchitectToTile(tileInfo, architectColor);
    } else {
      // Add the active token if needed.
      if (tile.getCentury() == gameState.getCentury()) {
        tileInfo.architectColor = PlayerColor.NONE;
        tileInfo.activeTokenNode = new Sprite(spriteResources.get(Type.activeToken),
            getArchitectParentTransform(tileInfo));
        tileInfo.root.add(tileInfo.activeTokenNode);
      }
    }

    // Add the cubes.
    int cubesPerSpot = tileState.getCubesPerSpot();
    CubeGrid cubeGrid = new CubeGrid(cubesPerSpot, 1);
    for (int spot = 0; spot < 3; ++spot) {
      PlayerColor cubesColor = tileState.getColorInSpot(spot);
      if (cubesColor.isNormalColor()) {
        addCubesToTile(cubeGrid, tileInfo, cubesColor, spot, cubesPerSpot);
      }
    }
  }

  private void renderBuildingTile(TileInfo tileInfo, TileState tileState, SceneNodeList tileNode) {
    Tile tile = tileState.getTile();
    tileInfo.tileSprite = new Sprite(spriteResources.getBuildingTile(tile.getInfluenceType(),
        tile.getCentury(), tile.getBuildingIndex()));
    tileNode.add(tileInfo.tileSprite);
    PlayerColor starTokenColor = tileState.getStarTokenColor();
    if (starTokenColor.isNormalColor()) {
      addStarTokenTo(tileInfo, tileState.getStarTokenColor(), tileState.getNbStars());
    }
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
   * Returns the node containing the cubes of the specified influence zone.
   * @param influenceZone The influence zone for which to get the node.
   * @return The node containing the cubes of that influence zone.
   */
  public SceneNode getInfluenceZoneNode(InfluenceType influenceZone) {
    return influenceZoneNode[influenceZone.ordinal()];
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

  /**
   * Remove a given number of cubes from a given spot on a given tile and return the global
   * transforms of the removed cubes. The number of cubes removed must be exactly the number that
   * can be contained on that spot, the color must match the color of the player on the spot. Tiles
   * are compared by pointer so they must come from the same pool.
   *
   * @param tile The tile to remove cubes from.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param spot The index of the spot to remove cubes from.
   * @param nbCubes The number of cubes to remove.
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> removeCubesFromTile(Tile tile, PlayerColor playerColor, int spot,
      int nbCubes) {
    assert nbCubes > 0;
    assert playerColor.isNormalColor();
    TileInfo tileInfo = findTileInfo(tile);
    assert tileInfo != null;
    assert tileInfo.cubesOnSpot[spot].playerColor == playerColor;
    assert tileInfo.cubesOnSpot[spot].cubes.size() == nbCubes;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    for (SceneNode cube : tileInfo.cubesOnSpot[spot].cubes) {
      result.add(cube.getTotalTransform(0));
    }
    // Remove the cube holder node itself, effectively removing all the cubes
    tileInfo.cubesOnSpot[spot].cubes.get(0).getParent().setParent(null);

    return result;
  }

  /**
   * Add a given number of cubes to a given spot on a given tile and return the global transforms of
   * the added cubes. The number of cubes added must be exactly the number that can be contained on
   * that spot. Tiles are compared by pointer so they must come from the same pool.
   *
   * @param tile The tile to remove cubes from.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param spot The index of the spot to remove cubes from.
   * @param nbCubes The number of cubes to remove.
   * @return The list of global transforms of the added cubes.
   */
  public List<Transform> addCubesToTile(Tile tile, PlayerColor playerColor, int spot, int nbCubes) {
    return addCubesToTile(new CubeGrid(nbCubes, 1), findTileInfo(tile), playerColor, spot, nbCubes);
  }

  /**
   * Remove an architect from the given tile.
   * @param tile The tile to remove an architect from.
   * @param architectColor The color of the architect to remove. (not NONE)
   * @return The global transforms of the removed architect.
   */
  public Transform removeArchitectFromTile(Tile tile, PlayerColor architectColor) {
    TileInfo tileInfo = findTileInfo(tile);
    assert tileInfo.architectColor == architectColor;
    assert tileInfo.architectNode != null;
    assert tileInfo.activeTokenNode == null;
    // We remove the parent of the architect node from the tree.
    Transform result = tileInfo.architectNode.getTotalTransform(0);
    tileInfo.architectNode.getParent().setParent(null);
    tileInfo.architectColor = PlayerColor.NONE;
    tileInfo.architectNode = null;
    return result;
  }

  /**
   * Add an architect to the given tile.
   * @param tile The tile to add an architect to.
   * @param architectColor The color of the architect to add. (not NONE)
   * @return The global transforms of the added architect.
   */
  public Transform addArchitectToTile(Tile tile, PlayerColor architectColor) {
    return addArchitectToTile(findTileInfo(tile), architectColor);
  }

  /**
   * Gets the transform of the architect slot on a given tile.
   * @param tile The tile at which to get the architect transform.
   * @return The global transforms of the architect.
   */
  public Transform getArchitectSlotOnTileTransform(Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    return tileInfo.root.getTotalTransform(0).times(getArchitectParentTransform(tileInfo));
  }

  /**
   * Gets the transform of an architect on a given tile.
   * @param tile The tile at which to get the architect transform.
   * @return The global transforms of the architect.
   */
  public Transform getArchitectOnTileTransform(Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    return tileInfo.root.getTotalTransform(0).times(getArchitectParentTransform(tileInfo)).times(
        getArchitectTransform());
  }

  /**
   * Gets the global transform of a given tile.
   * @param tile The tile for which to get the transform.
   * @return The global transforms of the tile.
   */
  public Transform getTileTransform(Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    return tileInfo.root.getTotalTransform(0);
  }

  /**
   * Gets the global transform of a given action.
   * @param boardAction The board action for which to get the transform.
   * @return The global transforms of the board action.
   */
  public Transform getActionTransform(BoardAction boardAction) {
    Vector2d actionLocation = boardAction.getLocation();
    return backgroundBoardRoot.getTotalTransform(0).times(new ConstantTransform(
        Board.positionForLocation(actionLocation.getColumn(), actionLocation.getLine())));
  }

  private List<Transform> addCubesToTile(CubeGrid cubeGrid, TileInfo tileInfo,
      PlayerColor playerColor, int spot, int nbCubes) {
    assert playerColor.isNormalColor();
    assert nbCubes > 0;
    List<Transform> result = new ArrayList<Transform>(nbCubes);
    assert tileInfo != null;
    // Add the node to hold these cubes.
    double x = -0.0225 + spot * 0.0225;
    double y = spot == 1 ? 0.0225 : 0;
    SceneNodeList cubesNode = new SceneNodeList(new ConstantTransform(new Vector2d(x, y), 1.0,
        -tileInfo.rotation));
    tileInfo.root.add(cubesNode);
    tileInfo.root.sendToBack(cubesNode);
    tileInfo.root.sendToBack(tileInfo.tileSprite);
    // Add all the cubes to the node.
    tileInfo.cubesOnSpot[spot].playerColor = playerColor;
    tileInfo.cubesOnSpot[spot].cubes.clear();
    ConstantTransform parentTransform = cubesNode.getTotalTransform(0);
    for (int cubeIndex = 0; cubeIndex < nbCubes; ++cubeIndex) {
      ConstantTransform childTransform = new ConstantTransform(cubeGrid.getPosition(cubeIndex, 0));
      Sprite cubeSprite = new Sprite(spriteResources.getCube(playerColor), childTransform);
      tileInfo.cubesOnSpot[spot].cubes.add(cubeSprite);
      result.add(parentTransform.times(childTransform));
      cubesNode.add(cubeSprite);
    }
    return result;
  }

  private Transform addArchitectToTile(TileInfo tileInfo, PlayerColor architectColor) {
    assert tileInfo.architectColor == PlayerColor.NONE;
    assert tileInfo.architectNode == null;
    if (tileInfo.activeTokenNode != null) {
      tileInfo.activeTokenNode.setParent(null);
      tileInfo.activeTokenNode = null;
    }
    tileInfo.architectColor = architectColor;
    SceneNodeList architectParentNode = new SceneNodeList(getArchitectParentTransform(tileInfo));
    tileInfo.root.add(architectParentNode);
    tileInfo.root.sendToFront(architectParentNode);
    tileInfo.architectNode = new Sprite(spriteResources.getPawn(architectColor),
        getArchitectTransform());
    architectParentNode.add(tileInfo.architectNode);
    return tileInfo.architectNode.getTotalTransform(0);
  }

  private ConstantTransform getArchitectParentTransform(TileInfo tileInfo) {
    return new ConstantTransform(new Vector2d(0, -0.0225), 1.0, -tileInfo.rotation);
  }

  private ConstantTransform getArchitectTransform() {
    return new ConstantTransform(new Vector2d(0, -0.01));
  }

  /**
   * Highlight a specific tile.
   * @param foregroundRoot The root of the objects in front of the glass screen.
   * @param tile The tile to highlight.
   */
  public void highlightTile(SceneNodeList foregroundRoot, Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    assert tileInfo != null;
    Transform globalTransform = tileInfo.root.getTotalTransform(0);
    SceneNode highlightedTile = tileInfo.root.deepClone();
    highlightedTile.setParent(foregroundRoot);
    highlightedTile.setTransform(globalTransform);
  }

  /**
   * Highlight a specific leader card on the board.
   * @param leaderCard The leader card to highlight.
   */
  public void highlightLeaderCard(SceneNodeList foregroundRoot, LeaderCard leaderCard) {
    SceneNode node = leaderCardNode[leaderCard.ordinal()];
    assert node != null;
    Transform globalTransform = node.getTotalTransform(0);
    SceneNode highlightedCard = node.deepClone();
    highlightedCard.setParent(foregroundRoot);
    highlightedCard.setTransform(globalTransform);
  }

  /**
   * Gets a copy of the scene node corresponding to the specified tile.
   * @param tile The tile for which to get a copy of the scene node.
   * @return A copied scene node corresponding to that tile.
   */
  public SceneNode copyTile(Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    return tileInfo.root.deepClone();
  }

  /**
   * Gets a copy of the scene node corresponding to the specified leader card on the board.
   * @param leaderCard The leader card for which to get a copy of the scene node.
   * @return A copied scene node corresponding to that leader card.
   */
  public SceneNode copyLeaderCard(LeaderCard leaderCard) {
    SceneNode node = leaderCardNode[leaderCard.ordinal()];
    assert node != null;
    return node.deepClone();
  }

  private TileInfo findTileInfo(Tile tile) {
    for (int i = 0; i < 18; ++i) {
      for (int j = 0; j < 8; ++j) {
        if (tileGrid[i][j] != null && tileGrid[i][j].tile == tile) {
          return tileGrid[i][j];
        }
      }
    }
    return null;
  }

  /**
   * Ensure that all the not that can be turned invisible are made back visible here.
   */
  void resetVisiblity() {
    for (SceneNode node : influenceZoneNode)  {
      node.setVisible(true);
    }
  }

  /**
   * Remove the leader card from the board.
   * @param leaderCard The leader card to remove.
   * @return The global transforms of the removed leader card.
   */
  public Transform removeLeaderCard(LeaderCard leaderCard) {
    int index = leaderCard.ordinal();
    assert leaderCardNode[index] != null;
    Transform result = leaderCardNode[index].getTotalTransform(0);
    leaderCardNode[index].setParent(null);
    leaderCardNode[index] = null;
    return result;
  }

  /**
   * Add the leader card to the board.
   * @param leaderCard The leader card to add.
   * @return The global transforms of the added leader card.
   */
  public Transform addLeaderCard(LeaderCard leaderCard) {
    int index = leaderCard.ordinal();
    assert leaderCardNode[index] == null;
    leaderCardNode[index] = new Sprite(spriteResources.getLeader(leaderCard),
        getLeaderCardTransform(index));
    backgroundBoardRoot.add(leaderCardNode[index]);
    return leaderCardNode[index].getTotalTransform(0);
  }

  /**
   * Gets the global transform of the leader card location on the board.
   * @param leaderCard The leader card to get.
   * @return The global transforms of the leader card location.
   */
  public Transform getLeaderCardTransform(LeaderCard leaderCard) {
    int index = leaderCard.ordinal();
    return backgroundBoardRoot.getTotalTransform(0).times(getLeaderCardTransform(index));
  }

  /**
   * Removes a star token sitting on a given tile. Does nothing if the tile has no star token.
   * @param tile The tile from which to remove the star token.
   */
  public void removeStarTokenFrom(Tile tile) {
    TileInfo tileInfo = findTileInfo(tile);
    assert tileInfo != null;
    if (tileInfo.starTokenNode != null) {
      tileInfo.starTokenNode.setParent(null);
      tileInfo.starTokenNode = null;
    }
  }

  /**
   * Adds a star token on a given tile, returns the transform of the added token.
   * @param tile The tile to which to add the star token.
   * @param starTokenColor The color of the star token to add.
   * @param nbStars The number of stars to add.
   * @return The transform of the newly added star token.
   */
  public Transform addStarTokenTo(Tile tile, PlayerColor starTokenColor, int nbStars) {
    return addStarTokenTo(findTileInfo(tile), starTokenColor, nbStars);
  }

  private Transform addStarTokenTo(TileInfo tileInfo, PlayerColor starTokenColor, int nbStars) {
    assert tileInfo != null;
    assert tileInfo.starTokenNode == null;
    tileInfo.starTokenNode = new SceneNodeList(new ConstantTransform(new Vector2d(), 1,
        -tileInfo.rotation));
    tileInfo.root.add(tileInfo.starTokenNode);
    tileInfo.root.sendToFront(tileInfo.starTokenNode);
    Sprite starToken = new Sprite(spriteResources.getStarToken(starTokenColor, nbStars),
        new ConstantTransform(new Vector2d(0, -0.02)));
    tileInfo.starTokenNode.add(starToken);
    return starToken.getTotalTransform(0);
  }

  private ConstantTransform getLeaderCardTransform(int index) {
    double x = 0.068 * index + 0.09;
    return new ConstantTransform(new Vector2d(x, -0.3));
  }
}
