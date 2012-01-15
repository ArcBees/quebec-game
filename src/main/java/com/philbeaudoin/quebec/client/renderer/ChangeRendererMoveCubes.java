package com.philbeaudoin.quebec.client.renderer;

import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.Sprite;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.GameStateChangeMoveCubes;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.UserPreferences;
import com.philbeaudoin.quebec.shared.utils.ArcTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A change renderer that can apply a {@link GameStateChangeMoveCubes} to a scene graph.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ChangeRendererMoveCubes implements ChangeRenderer {

  private final SpriteResources spriteResources;
  private final UserPreferences userPreferences;
  private final int nbCubes;
  private final SceneCubeDestination from;
  private final SceneCubeDestination to;

  @Inject
  ChangeRendererMoveCubes(
      SpriteResources spriteResources,
      UserPreferences userPreferences,
      @Assisted int nbCubes,
      @Assisted("from") SceneCubeDestination from,
      @Assisted("to") SceneCubeDestination to) {
    this.spriteResources = spriteResources;
    this.userPreferences = userPreferences;
    this.nbCubes = nbCubes;
    this.from = from;
    this.to = to;
    assert from.getPlayerColor() == to.getPlayerColor();
  }

  @Override
  public void applyRemovals(GameStateRenderer renderer) {
    from.removeFrom(nbCubes, renderer);
  }

  @Override
  public void applyAdditions(GameStateRenderer renderer) {
    to.addTo(nbCubes, renderer);
  }

  @Override
  public void undoRemovals(GameStateRenderer renderer) {
    from.addTo(nbCubes, renderer);
  }

  @Override
  public void undoAdditions(GameStateRenderer renderer) {
    to.removeFrom(nbCubes, renderer);
  }

  @Override
  public void generateAnim(GameStateRenderer renderer, SceneNodeList animRoot,
      double startingTime) {
    List<Transform> startTransforms = from.removeFrom(nbCubes, renderer);
    List<Transform> finishTransforms = to.addTo(nbCubes, renderer);
    assert startTransforms.size() == finishTransforms.size();
    assert startTransforms.size() == nbCubes;
    double endingTime = startingTime + userPreferences.getAnimDuration();
    PlayerColor playerColor = from.getPlayerColor();
    for (int i = 0; i < nbCubes; ++i) {
      Sprite cube = new Sprite(spriteResources.getCube(playerColor),
          new ArcTransform(startTransforms.get(i), finishTransforms.get(i),
              startingTime, endingTime));
      animRoot.add(cube);
    }
  }

}
