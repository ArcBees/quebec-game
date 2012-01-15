package com.philbeaudoin.quebec.client.renderer;

import java.util.List;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.shared.CubeDestinationInfluenceZone;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A cube destination within a scene graph corresponding to an given player color within an
 * influence zone.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 *
 */
public class SceneCubeDestinationInfluenceZone implements SceneCubeDestination {

  private final CubeDestinationInfluenceZone cubeDestinationInfluenceZone;

  @Inject
  public SceneCubeDestinationInfluenceZone(
      @Assisted CubeDestinationInfluenceZone cubeDestinationInfluenceZone) {
    this.cubeDestinationInfluenceZone = cubeDestinationInfluenceZone;
  }

  @Override
  public PlayerColor getPlayerColor() {
    return cubeDestinationInfluenceZone.getPlayerColor();
  }

  @Override
  public List<Transform> removeFrom(int nbCubes, GameStateRenderer renderer) {
    return renderer.removeCubesFromInfluenceZone(getInfluenceType(), getPlayerColor(), nbCubes);
  }

  @Override
  public List<Transform> addTo(int nbCubes, GameStateRenderer renderer) {
    return renderer.addCubesToInfluenceZone(getInfluenceType(), getPlayerColor(), nbCubes);
  }

  private InfluenceType getInfluenceType() {
    return cubeDestinationInfluenceZone.getInfluenceType();
  }
}
