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

import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.GameState;
import com.philbeaudoin.quebec.shared.PlayerState;
import com.philbeaudoin.quebec.shared.utils.Transformation;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a game state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin
 */
public class GameStateRenderer {

  public static final double LEFT_COLUMN_WIDTH = 0.38209;

  private final SceneNodeList root = new SceneNodeList();

  private final ScoreRenderer scoreRenderer = new ScoreRenderer();
  private final BoardRenderer boardRenderer = new BoardRenderer(LEFT_COLUMN_WIDTH);
  private final ArrayList<PlayerStateRenderer> playerStateRenderers =
      new ArrayList<PlayerStateRenderer>(5);

  /**
   * Renders the game state.
   * @param gameState The desired game state.
   * @param spriteResources The resources.
   */
  public void render(GameState gameState, SpriteResources spriteResources) {
    List<PlayerState> playerStates = gameState.getPlayerStates();
    initPlayerStateRenderers(playerStates);

    // Clear everything under the root node.
    root.clear();

    // Render the board first.
    boardRenderer.render(gameState, spriteResources, root);

    int index = 0;
    for (PlayerState playerState : playerStates) {
      playerStateRenderers.get(index).render(playerState, spriteResources, root,
          boardRenderer.getBoardRoot());
      index++;
    }
  }

  /**
   * Access the root created by this renderer.
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
        PlayerStateRenderer playerStateRenderer = new PlayerStateRenderer(LEFT_COLUMN_WIDTH, 0.15,
            new Transformation(new Vector2d(0, deltaY)), scoreRenderer);
        deltaY += 0.15;
        playerStateRenderers.add(playerStateRenderer);
      }
    }
  }

}
