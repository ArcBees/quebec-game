package com.philbeaudoin.quebec.client.renderer;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * Factory methods of the various renderer classes, used in assisted injection.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface RendererFactories {
  GameStateRenderer createGameStateRenderer();
  BoardRenderer createBoardRenderer(double leftPosition);
  ScoreRenderer createScoreRenderer();
  PlayerStateRenderer createPlayerStateRenderer(Vector2d size, Transform transform,
      ScoreRenderer scoreRenderer);

  ChangeRendererGenerator createChangeRendererGenerator();
  ChangeRendererComposite createChangeRendererComposite();
  ChangeRendererMoveCubes createChangeRendererMoveCubes(int nbCubes,
      @Assisted("from") SceneCubeDestination from, @Assisted("to") SceneCubeDestination to);
  SceneCubeDestinationGenerator createSceneGraphCubeDestinationGenerator();
  SceneCubeDestinationInfluenceZone createSceneDestinationInfluenceZone(
      CubeDestinationInfluenceZone cubeDestinationInfluenceZone);
}
