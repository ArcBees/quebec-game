package com.philbeaudoin.quebec.client.renderer;

import java.util.ArrayList;

import javax.inject.Inject;

import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.GameStateChangeComposite;
import com.philbeaudoin.quebec.shared.UserPreferences;

/**
 * A change renderer that can apply a {@link GameStateChangeComposite} to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererComposite implements ChangeRenderer {

  private final UserPreferences userPreferences;
  private final ArrayList<ChangeRenderer> gameStateChangeRenderers =
      new ArrayList<ChangeRenderer>();

  @Inject
  public ChangeRendererComposite(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;
  }

  /**
   * Adds a {@link ChangeRenderer} to this composite.
   * @param changeRenderer The change renderer to add.
   */
  void add(ChangeRenderer changeRenderer) {
    gameStateChangeRenderers.add(changeRenderer);
  }

  @Override
  public void applyRemovals(GameStateRenderer renderer) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.applyRemovals(renderer);
    }
  }

  @Override
  public void applyAdditions(GameStateRenderer renderer) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.applyAdditions(renderer);
    }
  }

  @Override
  public void undoRemovals(GameStateRenderer renderer) {
    for (int i = gameStateChangeRenderers.size() - 1 ; i >= 0; i--) {
      gameStateChangeRenderers.get(i).undoRemovals(renderer);
    }
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    for (int i = gameStateChangeRenderers.size() - 1 ; i >= 0; i--) {
      gameStateChangeRenderers.get(i).undoAdditions(renderer);
    }
  }


  @Override
  public void generateAnim(GameStateRenderer renderer,
      SceneNodeList animRoot, double startingTime) {
    for (ChangeRenderer gameStateChangeRenderer : gameStateChangeRenderers) {
      gameStateChangeRenderer.generateAnim(renderer, animRoot, startingTime);
      startingTime += userPreferences.getAnimDuration();
    }
  }
}
