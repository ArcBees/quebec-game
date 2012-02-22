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
import javax.inject.Provider;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.philbeaudoin.quebec.client.interaction.Interaction;
import com.philbeaudoin.quebec.client.interaction.InteractionFactories;
import com.philbeaudoin.quebec.client.interaction.InteractionGenerator;
import com.philbeaudoin.quebec.client.scene.ComplexText;
import com.philbeaudoin.quebec.client.scene.Rectangle;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.client.scene.SpriteResources;
import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.action.PossibleActions;
import com.philbeaudoin.quebec.shared.message.Message;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.state.GameState;
import com.philbeaudoin.quebec.shared.state.LeaderCard;
import com.philbeaudoin.quebec.shared.state.PlayerState;
import com.philbeaudoin.quebec.shared.state.Tile;
import com.philbeaudoin.quebec.shared.statechange.GameStateChange;
import com.philbeaudoin.quebec.shared.utils.Callback;
import com.philbeaudoin.quebec.shared.utils.CallbackRegistration;
import com.philbeaudoin.quebec.shared.utils.ConstantTransform;
import com.philbeaudoin.quebec.shared.utils.Transform;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * The renderer of a game state. Keeps track of the rendered objects so they can be animated.
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class GameStateRenderer {

  public static final double LEFT_COLUMN_WIDTH = 0.38209;

  public static final double TEXT_CENTER = 1.05;
  public static final double TEXT_LINE_1 = 0.095;
  public static final double TEXT_LINE_2 = 0.136;

  private final SceneNodeList staticRoot = new SceneNodeList();
  private final SceneNodeList dynamicRoot = new SceneNodeList();
  private final SceneNodeList backgroundRoot = new SceneNodeList();
  private final SceneNodeList glassScreenRoot = new SceneNodeList();
  private final SceneNodeList foregroundRoot = new SceneNodeList();
  private final SceneNodeList animationRoot = new SceneNodeList();
  private final ArrayList<Interaction> interactions = new ArrayList<Interaction>();

  private final Scheduler scheduler;
  private final RendererFactories factories;
  private final InteractionFactories interactionFactories;
  private final Provider<MessageRenderer> messageRendererProvider;
  private final ChangeRendererGenerator changeRendererGenerator;
  private final ScoreRenderer scoreRenderer;
  private final BoardRenderer boardRenderer;
  private final ArrayList<PlayerStateRenderer> playerStateRenderers =
      new ArrayList<PlayerStateRenderer>(5);

  private boolean forceGlassScreen;
  private boolean refreshNeeded = true;

  private CallbackRegistration animationCompletedRegistration;

  @Inject
  public GameStateRenderer(Scheduler scheduler, RendererFactories factories,
      InteractionFactories interactionFactories,
      SpriteResources spriteResources,
      Provider<MessageRenderer> messageRendererProvider,
      ChangeRendererGenerator changeRendererGenerator) {
    this.scheduler = scheduler;
    this.factories = factories;
    this.interactionFactories = interactionFactories;
    this.messageRendererProvider = messageRendererProvider;
    this.changeRendererGenerator = changeRendererGenerator;
    scoreRenderer = factories.createScoreRenderer();
    boardRenderer = factories.createBoardRenderer(LEFT_COLUMN_WIDTH);
    staticRoot.add(backgroundRoot);
    staticRoot.add(glassScreenRoot);
    staticRoot.add(foregroundRoot);
    dynamicRoot.add(animationRoot);
  }

  /**
   * Renders the game state.
   * @param gameState The desired game state.
   */
  public void render(final GameState gameState) {
    refreshNeeded = true;
    List<PlayerState> playerStates = gameState.getPlayerStates();
    initPlayerStateRenderers(playerStates);

    // Clear interactions.
    interactions.clear();

    // Clear everything save for the root.
    backgroundRoot.clear();
    glassScreenRoot.clear();
    foregroundRoot.clear();

    // Render the board first.
    boardRenderer.render(gameState, backgroundRoot);

    int index = 0;
    for (PlayerState playerState : playerStates) {
      playerStateRenderers.get(index).render(playerState, backgroundRoot,
          boardRenderer.getBackgroundBoardRoot());
      index++;
    }

    // TODO(beaudoin): This is a bit of an anti-pattern. The player should probably render the
    //     interactions.
    if (gameState.getCurrentPlayer().getPlayer().isRobot()) {
      PossibleActions possibleActions = gameState.getPossibleActions();
      if (possibleActions != null && possibleActions.getNbActions() > 0) {
        int actionIndex = (int) (Math.random() * possibleActions.getNbActions());

        // TODO(beaudoin): This is duplicate code from InteractionWithAction, extract somewhere.
        final GameStateChange gameStateChange = possibleActions.execute(actionIndex, gameState);

        generateAnimFor(gameStateChange);

        scheduler.scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            if (!isAnimationCompleted(0.0)) {
              animationCompletedRegistration = addAnimationCompletedCallback(
                  new Callback() {
                    @Override public void execute() {
                        animationCompletedRegistration.unregister();
                        animationCompletedRegistration = null;
                        renderForNextMove(gameStateChange, gameState);
                    }
                  });
            } else {
              renderForNextMove(gameStateChange, gameState);
            }
          }
        });
      }
    } else {
      // Render the possible actions.
      PossibleActions possibleActions = gameState.getPossibleActions();
      if (possibleActions != null) {
        InteractionGenerator generator =
            interactionFactories.createInteractionGenerator(gameState, this);
        possibleActions.accept(generator);
        generator.generateInteractions();
        Message message = possibleActions.getMessage();
        if (message != null) {
          MessageRenderer messageRenderer = messageRendererProvider.get();
          message.accept(messageRenderer);
          addToAnimationGraph(new ComplexText(messageRenderer.getComponents(),
              new ConstantTransform(new Vector2d(TEXT_CENTER, TEXT_LINE_1))));
        }
      }
      for (Interaction interaction : interactions) {
        interaction.highlight();
      }
    }
  }

  private void renderForNextMove(GameStateChange gameStateChange, GameState gameState) {
    clearAnimationGraph();
    gameStateChange.apply(gameState);
    render(gameState);
  }

  private void addOrClearGlassScreen() {
    boolean glassScreenNeeded = !foregroundRoot.getChildren().isEmpty() || forceGlassScreen;
    boolean glassScreenPresent = !glassScreenRoot.getChildren().isEmpty();
    // If there is anything in the foreground, show the glass screen
    if (glassScreenNeeded && !glassScreenPresent) {
      glassScreenRoot.add(new Rectangle(0, 0, 10, 10,
          "rgba(0, 0, 0, 0.5)", "rgba(0, 0, 0, 0.5)", null, 0));
    } else if (!glassScreenNeeded && glassScreenPresent) {
      glassScreenRoot.clear();
    }
  }

  /**
   * Remove a given number of cubes of a given player from a given influence zone and return the
   * global transforms of the removed cubes.
   * @param influenceType The influence type of the zone to remove cubes from.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param nbCubes The number of cubes to remove, cannot be more than what is in the zone.
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> removeCubesFromInfluenceZone(InfluenceType influenceType,
      PlayerColor playerColor, int nbCubes) {
    refreshNeeded = true;
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
    refreshNeeded = true;
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

  /**
   * Remove a given number of cubes from a given player's active or passive reserve and return the
   * global transforms of the removed cubes.
   * @param playerColor The color of the player whose cube to remove (not NONE or NEUTRAL).
   * @param active True to remove the cubes from the active reserve, false for the passive.
   * @param nbCubes The number of cubes to remove, must be more than what is in the reserve.
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> removeCubesFromPlayer(PlayerColor playerColor,
      boolean active, int nbCubes) {
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.removeCubesFromPlayer(active, nbCubes);
  }

  /**
   * Add a given number of cubes from a given player's active or passive reserve and return the
   * global transforms of the newly added cubes.
   * @param playerColor The color of the player whose cube to add (not NONE or NEUTRAL).
   * @param active True to add the cubes from the active reserve, false for the passive.
   * @param nbCubes The number of cubes to add, must be positive or 0.
   * @return The list of global transforms of the added cubes.
   */
  public List<Transform> addCubesToPlayer(PlayerColor playerColor,
      boolean active, int nbCubes) {
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.addCubesToPlayer(active, nbCubes);
  }

  /**
   * Gets the global transform for a given player's active or passive reserve.
   * @param playerColor The color of the desired player reserve (not NONE or NEUTRAL).
   * @param active True to get the active reserve, false for the passive.
   * @return The global transform of the reserve.
   */
  public Transform getPlayerCubeZoneTransform(PlayerColor playerColor, boolean active) {
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    return playerStateRenderer.getCubeZoneTransform(active);
  }

  /**
   * Returns the node containing the cubes of the specified influence zone.
   * @param influenceZone The influence zone for which to get the node.
   * @return The node containing the cubes of that influence zone.
   */
  public SceneNode getInfluenceZoneNode(InfluenceType influenceZone) {
    return boardRenderer.getInfluenceZoneNode(influenceZone);
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
    refreshNeeded = true;
    return boardRenderer.removeCubesFromTile(tile, playerColor, spot, nbCubes);
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
   * @return The list of global transforms of the removed cubes.
   */
  public List<Transform> addCubesToTile(Tile tile, PlayerColor playerColor, int spot, int nbCubes) {
    refreshNeeded = true;
    return boardRenderer.addCubesToTile(tile, playerColor, spot, nbCubes);
  }

  /**
   * Remove the standard or neutral architect from the given player zone.
   * @param playerColor The color of the player zone from which to remove an architect (not NONE or
   *     NEUTRAL).
   * @param neutralArchitect True to remove the neutral architect.
   * @return The global transforms of the removed architect.
   */
  public Transform removeArchitectFromPlayer(PlayerColor playerColor, boolean neutralArchitect) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.removeArchitect(neutralArchitect);
  }

  /**
   * Add the standard or neutral architect to the given player zone.
   * @param playerColor The color of the player zone to which to add an architect (not NONE or
   *     NEUTRAL).
   * @param neutralArchitect True to add the neutral architect.
   * @return The global transforms of the added architect.
   */
  public Transform addArchitectToPlayer(PlayerColor playerColor, boolean neutralArchitect) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.addArchitect(neutralArchitect);
  }

  /**
   * Gets the transform of the standard or neutral architect on the given player zone.
   * @param playerColor The color of the player zone (not NONE or NEUTRAL).
   * @param neutralArchitect True to get the transform of the neutral architect.
   * @return The global transforms of the architect.
   */
  public Transform getArchitectOnPlayerTransform(PlayerColor playerColor,
      boolean neutralArchitect) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    return playerStateRenderer.getArchitectTransform(neutralArchitect);
  }

  /**
   * Remove an architect from the given tile.
   * @param tile The tile to remove an architect from.
   * @param architectColor The color of the architect to remove. (not NONE)
   * @return The global transforms of the removed architect.
   */
  public Transform removeArchitectFromTile(Tile tile, PlayerColor architectColor) {
    refreshNeeded = true;
    return boardRenderer.removeArchitectFromTile(tile, architectColor);
  }

  /**
   * Add an architect to the given tile.
   * @param tile The tile to add an architect to.
   * @param architectColor The color of the architect to add. (not NONE)
   * @return The global transforms of the added architect.
   */
  public Transform addArchitectToTile(Tile tile, PlayerColor architectColor) {
    refreshNeeded = true;
    return boardRenderer.addArchitectToTile(tile, architectColor);
  }

  /**
   * Gets the transform of the architect slot on a given tile.
   * @param tile The tile at which to get the architect transform.
   * @return The global transforms of the architect.
   */
  public Transform getArchitectSlotOnTileTransform(Tile tile) {
    return boardRenderer.getArchitectSlotOnTileTransform(tile);
  }

  /**
   * Gets the transform of an architect on a given tile.
   * @param tile The tile at which to get the architect transform.
   * @return The global transforms of the architect.
   */
  public Transform getArchitectOnTileTransform(Tile tile) {
    return boardRenderer.getArchitectOnTileTransform(tile);
  }

  /**
   * Gets the global transform of a given tile.
   * @param tile The tile for which to get the transform.
   * @return The global transforms of the tile.
   */
  public Transform getTileTransform(Tile tile) {
    return boardRenderer.getTileTransform(tile);
  }

  /**
   * Gets the global transform of a given action.
   * @param boardAction The board action for which to get the transform.
   * @return The global transforms of the board action.
   */
  public Transform getActionTransform(BoardAction boardAction) {
    return boardRenderer.getActionTransform(boardAction);
  }

  /**
   * Remove the leader card from the given player zone.
   * @param playerColor The color of the player zone from which to remove the leader card (not NONE
   *     or NEUTRAL).
   * @return The global transforms of the removed leader card.
   */
  public Transform removeLeaderCardFromPlayer(PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.removeLeaderCard();
  }

  /**
   * Add the leader card to the given player zone.
   * @param playerColor The color of the player zone to which to add the leader card (not NONE
   *     or NEUTRAL).
   * @param leaderCard The leader card to add.
   * @return The global transforms of the added leader card.
   */
  public Transform addLeaderCardToPlayer(PlayerColor playerColor, LeaderCard leaderCard) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.addLeaderCard(leaderCard);
  }

  /**
   * Gets the global transform of the leader card location on a given player.
   * @param playerColor The color of the player zone on which to look for the leader card transform
   *     (not NONE or NEUTRAL).
   * @return The global transforms of the leader card location.
   */
  public Transform getLeaderCardOnPlayerTransform(PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    PlayerStateRenderer playerStateRenderer = getPlayerStateRenderer(playerColor);
    assert playerStateRenderer != null;
    refreshNeeded = true;
    return playerStateRenderer.getLeaderCardTransform();
  }

  /**
   * Remove the leader card from the board.
   * @param leaderCard The leader card to remove.
   * @return The global transforms of the removed leader card.
   */
  public Transform removeLeaderCardFromBoard(LeaderCard leaderCard) {
    refreshNeeded = true;
    return boardRenderer.removeLeaderCard(leaderCard);
  }

  /**
   * Add the leader card to the board.
   * @param leaderCard The leader card to add.
   * @return The global transforms of the added leader card.
   */
  public Transform addLeaderCardToBoard(LeaderCard leaderCard) {
    refreshNeeded = true;
    return boardRenderer.addLeaderCard(leaderCard);
  }

  /**
   * Gets the global transform of the leader card location on the board.
   * @param leaderCard The leader card to get.
   * @return The global transforms of the leader card location.
   */
  public Transform getLeaderCardOnBoardTransform(LeaderCard leaderCard) {
    return boardRenderer.getLeaderCardTransform(leaderCard);
  }

  /**
   * Removes a star token sitting on a given tile. Does nothing if the tile has no star token.
   * @param tile The tile from which to remove the star token.
   */
  public void removeStarTokenFrom(Tile tile) {
    refreshNeeded = true;
    boardRenderer.removeStarTokenFrom(tile);
  }

  /**
   * Adds a star token on a given tile, returns the transform of the added token.
   * @param tile The tile to which to add the star token.
   * @param starTokenColor The color of the star token to add.
   * @param nbStars The number of stars to add.
   * @return The transform of the newly added star token.
   */
  public Transform addStarTokenTo(Tile tile, PlayerColor starTokenColor, int nbStars) {
    refreshNeeded = true;
    return boardRenderer.addStarTokenTo(tile, starTokenColor, nbStars);
  }

  /**
   * Highlight a specific tile.
   * @param tile The tile to highlight.
   */
  public void highlightTile(Tile tile) {
    refreshNeeded = true;
    boardRenderer.highlightTile(foregroundRoot, tile);
    addOrClearGlassScreen();
  }

  /**
   * Highlight a specific leader card on the board.
   * @param leaderCard The leader card to highlight.
   */
  public void highlightLeaderCard(LeaderCard leaderCard) {
    refreshNeeded = true;
    boardRenderer.highlightLeaderCard(foregroundRoot, leaderCard);
    addOrClearGlassScreen();
  }

  /**
   * Forces the glass screen to be shown so tiles drawn in the animation layer appear highlighted.
   */
  public void forceGlassScreen() {
    refreshNeeded = true;
    forceGlassScreen = true;
    addOrClearGlassScreen();
  }

  /**
   * Gets a copy of the scene node corresponding to the specified tile.
   * @param tile The tile for which to get a copy of the scene node.
   * @return A copied scene node corresponding to that tile.
   */
  public SceneNode copyTile(Tile tile) {
    return boardRenderer.copyTile(tile);
  }

  /**
   * Gets a copy of the scene node corresponding to the specified leader card on the board.
   * @param leaderCard The leader card for which to get a copy of the scene node.
   * @return A copied scene node corresponding to that leader card.
   */
  public SceneNode copyLeaderCardOnBoard(LeaderCard leaderCard) {
    return boardRenderer.copyLeaderCard(leaderCard);
  }

  /**
   * Remove all the highlighted components by clearing all the node in the foreground layer.
   */
  public void removeAllHighlights() {
    refreshNeeded = true;
    forceGlassScreen = false;
    foregroundRoot.clear();
    addOrClearGlassScreen();
  }

  /**
   * Indicates that the mouse has moved and that active interactions should be executed.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  public void onMouseMove(double x, double y, double time) {
    for (Interaction interaction : interactions) {
      interaction.onMouseMove(x, y, time);
    }
  }

  /**
   * Indicates that the mouse has been clicked and that active interactions should be executed.
   * @param x The x location of the mouse.
   * @param y The y location of the mouse.
   * @param time The current time.
   */
  public void onMouseClick(double x, double y, double time) {
    for (Interaction interaction : interactions) {
      interaction.onMouseClick(x, y, time);
    }
  }

  /**
   * Adds an interaction that the user can have with the board.
   * @param interaction The interaction to add.
   */
  public void addInteraction(Interaction interaction) {
    interactions.add(interaction);
  }

  /**
   * Clear all the interactions the user can have with the board.
   */
  public void clearInteractions() {
    interactions.clear();
    // Some interactions make node invisible, turn these nodes visible here.
    boardRenderer.resetVisiblity();
  }

  /**
   * Adds a node to be rendered in the animation graph.
   * @param sceneNode The scene node to add.
   */
  public void addToAnimationGraph(SceneNode sceneNode) {
    animationRoot.add(sceneNode);
  }

  /**
   * Clears the entire animation graph.
   */
  public void clearAnimationGraph() {
    animationRoot.clear();
  }

  /**
   * Adds a callback to be triggered when the animation is completed.
   * @param callback The callback to tigger.
   * @return The callback registration.
   */
  public CallbackRegistration addAnimationCompletedCallback(Callback callback) {
    return animationRoot.addAnimationCompletedCallback(callback);
  }

  /**
   * Checks whether or not the animation is completed at the given time.
   * @param time The time at which to check for animation completion.
   * @return True if the animation is completed at that time, false otherwise.
   */
  public boolean isAnimationCompleted(double time) {
    return animationRoot.isAnimationCompleted(time);
  }

  /**
   * Draws everything in the static layers to the given HTML5 context.
   * @param context The context to draw to.
   */
  public void drawStaticLayers(Context2d context) {
    refreshNeeded = false;
    staticRoot.draw(0, context);
  }

  /**
   * Draws everything in the dynamic layers to the given HTML5 context.
   * @param context The context to draw to.
   */
  public void drawDynamicLayers(double time, Context2d context) {
    dynamicRoot.draw(time, context);
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
   * Gets the player state renderer for a given player color.
   * @param playerColor The player color.
   * @return The player state renderer.
   */
  private PlayerStateRenderer getPlayerStateRenderer(PlayerColor playerColor) {
    assert playerColor.isNormalColor();
    for (PlayerStateRenderer playerRenderer : playerStateRenderers) {
      if (playerRenderer.getPlayerColor() == playerColor) {
        return playerRenderer;
      }
    }
    return null;
  }

  /**
   * Checks whether the static layers need to be redrawn.
   * @return True if the static layers need to be redrawn.
   */
  public boolean isRefreshNeeded() {
    return refreshNeeded;
  }

  /**
   * Generates the animation corresponding to a given game state change.
   * @param gameStateChange The game state change to animate.
   */
  public void generateAnimFor(GameStateChange gameStateChange) {
    ChangeRenderer changeRenderer = gameStateChange.accept(changeRendererGenerator);
    changeRenderer.generateAnim(this, 0.0);
    changeRenderer.undoAdditions(this);
  }
}

