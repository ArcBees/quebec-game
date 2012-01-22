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

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.client.utils.PawnStack;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a player state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ScoreRenderer {

  private final SpriteResources spriteResources;

  private final HashMap<Integer, SceneNodeList> nodeForScore =
      new HashMap<Integer, SceneNodeList>();

  private final SceneNode pawns[] = new SceneNode[5];

  @Inject
  public ScoreRenderer(SpriteResources spriteResources) {
    this.spriteResources = spriteResources;
  }

  /**
   * Renders the pawn of the given player according to its state.
   * @param playerState The state of the player to initialize.
   * @param boardRoot The root scene node of the board.
   */
  void renderPlayer(PlayerState playerState, SceneNodeList boardRoot) {
    PlayerColor color = playerState.getPlayer().getColor();
    int colorIndex = color.ordinal() - 1;
    assert colorIndex >= 0 && colorIndex < 5;
    if (pawns[colorIndex] == null) {
      pawns[colorIndex] = new Sprite(spriteResources.getPawn(color));
    }

    int score = playerState.getScore() % 100;
    SceneNodeList parent = nodeForScore.get(score);
    if (parent == null) {
      parent = new SceneNodeList(new ConstantTransform(getScorePosition(score)));
      nodeForScore.put(score, parent);
    }
    // Ensures the parent is in the tree.
    if (parent.getParent() == null) {
      boardRoot.add(parent);
    }
    SceneNodeList oldParent = pawns[colorIndex].getParent();
    parent.add(pawns[colorIndex]);

    // Remove old parent if it is no longer needed.
    if (oldParent != null && !oldParent.hasChild()) {
      oldParent.setParent(null);
    }

    // Update all the child positions
    List<SceneNode> pawnsOnScore = parent.getChildren();
    PawnStack pawnStack = new PawnStack(pawnsOnScore.size());
    int index = 0;
    for (SceneNode node : pawnsOnScore) {
      node.setTransform(new ConstantTransform(pawnStack.getPosition(index)));
      index++;
    }
  }

  private Vector2d getScorePosition(int score) {
    int moduloScore = score % 50;
    double x = 0.614 - (Math.min(28, moduloScore)) * 0.0439;
    double y = 0.442 - (Math.max(0, moduloScore - 28)) * 0.0416;
    if ((score / 50) % 2 == 1) {
      x *= -1.0;
      y *= -1.0;
      x -= 0.001;
      y -= 0.03;
    }
    return new Vector2d(x, y);
  }
}
