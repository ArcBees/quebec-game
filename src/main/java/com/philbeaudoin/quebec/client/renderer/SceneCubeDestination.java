package com.philbeaudoin.quebec.client.renderer;

import java.util.List;

import com.philbeaudoin.quebec.shared.CubeDestination;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.utils.Transform;

/**
 * A valid place to take or send cubes within a scene graph. The destination implies a color of
 * cubes. The render-side equivalent of {@link CubeDestination}.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public interface SceneCubeDestination {

  /**
   * @return The color of the player associated with that destination.
   */
  PlayerColor getPlayerColor();

  /**
   * Remove cubes from that specific destination in the provided scene graph.
   * @param nbCubes The number of cubes to remove.
   * @param renderer The renderer containing the scene graph from which to remove cubes.
   * @return The global transforms of the cubes removed from the scene graph.
   */
  List<Transform> removeFrom(int nbCubes, GameStateRenderer renderer);

  /**
   * Add cubes to this specific destination in the provided scene graph.
   * @param nbCubes The number of cubes to add.
   * @param renderer The renderer containing the scene graph onto which to add cubes.
   * @return The global transforms of the cubes added to the scene graph.
   */
  List<Transform> addTo(int nbCubes, GameStateRenderer renderer);
}
