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

import com.philbeaudoin.quebec.client.scene.Rectangle;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.GameState;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.PlayerState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a game state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateRenderer {

  public static final double LEFT_COLUMN_WIDTH = 0.38209;

  private final SceneNodeList root = new SceneNodeList();
  private final SceneNodeList backgroundRoot = new SceneNodeList();
  private final SceneNodeList glassScreenRoot = new SceneNodeList();
  private final SceneNodeList foregroundRoot = new SceneNodeList();

  private final RendererFactories factories;
  private final ScoreRenderer scoreRenderer;
  private final BoardRenderer boardRenderer;
  private final ArrayList<PlayerStateRenderer> playerStateRenderers =
      new ArrayList<PlayerStateRenderer>(5);

  @Inject
  public GameStateRenderer(RendererFactories factories) {
    this.factories = factories;
    scoreRenderer = factories.createScoreRenderer();
    boardRenderer = factories.createBoardRenderer(LEFT_COLUMN_WIDTH);
    root.add(backgroundRoot);
    root.add(glassScreenRoot);
    root.add(foregroundRoot);
  }

  /**
   * Renders the game state.
   * @param gameState The desired game state.
   * @param spriteResources The resources.
   */
  public void render(GameState gameState) {
    List<PlayerState> playerStates = gameState.getPlayerStates();
    initPlayerStateRenderers(playerStates);

    // Clear everything save for the root.
    backgroundRoot.clear();
    glassScreenRoot.clear();
    foregroundRoot.clear();

    // Render the board first.
    boardRenderer.render(gameState, backgroundRoot, foregroundRoot);

    int index = 0;
    for (PlayerState playerState : playerStates) {
      playerStateRenderers.get(index).render(playerState, backgroundRoot,
          boardRenderer.getBackgroundBoardRoot());
      index++;
    }

    // If there is anything in the foreground, show the glass screen
    if (!foregroundRoot.getChildren().isEmpty()) {
      glassScreenRoot.add(new Rectangle(0, 0, 10, 10,
          "rgba(0, 0, 0, 0.5)", "rgba(0, 0, 0, 0.5)", null, 0));
    }
  }

  /**
   * Access the root of the objects.
   * @return The rendered global root.
   */
  public SceneNodeList getRoot() {
    return root;
  }


  /**
   * Creates the player state renderers if needed.
   * @param playerStates The states of the players to render.
   */
  private void initPlayerStateRenderers(List<PlayerState> playerStates) {
    if (playerStateRenderers.size() != playerStates.size()) {
      playerStateRenderers.clear();
      double deltaY = 0;
      for (int i = 0; i < playerStates.size(); ++i) {
        PlayerStateRenderer playerStateRenderer = factories.createPlayerStateRenderer(
            new Vector2d(LEFT_COLUMN_WIDTH, 0.15), new ConstantTransform(new Vector2d(0, deltaY)),
            scoreRenderer);
        deltaY += 0.15;
        playerStateRenderers.add(playerStateRenderer);
      }
    }
  }

  /**
   * Remove a given number of cubes of a given player from a given influence zone and return the
   * global transforms of the removed cubes.
   * @param influenceType The influence type of the zone to remove cubes from.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param nbCubes The number of cubes to remove, must be more than what is contained in the zone.
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> removeCubesFromInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor, int nbCubes) {
    return boardRenderer.removeCubesFromInfluenceZone(influenceType, playerColor, nbCubes);
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
    return boardRenderer.addCubesToInfluenceZone(influenceType, playerColor, nbCubes);
  }

  /**
   * Call this method to reset the color associated with each line in each influence zone so that
   * lines that have no cubes can take any color. Be aware that undoing a move after that can leave
   * the rendered state slightly different from the original state.
   */
  public void resetColorForInfluenceZoneLines() {
    boardRenderer.resetColorForInfluenceZoneLines();
  }
}
