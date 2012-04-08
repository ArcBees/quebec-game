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

package com.philbeaudoin.quebec.shared.message;

import com.philbeaudoin.quebec.shared.InfluenceType;
import com.philbeaudoin.quebec.shared.PlayerColor;
import com.philbeaudoin.quebec.shared.ScoringInformation;
import com.philbeaudoin.quebec.shared.ZoneScoringInformation;
import com.philbeaudoin.quebec.shared.state.ActionType;

/**
 * Interface for messages. These messages can be rendered in various ways by a custom
 * {@link Message.Visitor}. This interface contains a subclass for each message. Each subclass gives
 * an example of the message rendered as:
 * <ul>
 * <li>A graphic element, with icons</li>
 * <li>A purely textual element</li>
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 *
 */
public interface Message {

  <T> T accept(Visitor<T> visitor);

 /**
  * Visitor for {@link Message}.
  * @param <T> The return type of the visitor method.
  */
  public interface Visitor<T> {
    T visit(Text host);
    T visit(MultilineText host);
    T visit(TakeThisLeaderCard host);
    T visit(MoveEitherArchitect host);
    T visit(MoveArchitect host);
    T visit(SendPassiveCubesToOneOfTwoZones host);
    T visit(SendActiveCubesToOneOfTwoZones host);
    T visit(SendPassiveCubesToZone host);
    T visit(SendActiveCubesToZone host);
    T visit(SendPassiveCubesToAnyZone host);
    T visit(SendPassiveCubesToAnyZoneOrCitadel host);
    T visit(SendPassiveCubesToThisTile host);
    T visit(SendActiveCubesToThisTile host);
    T visit(SendActiveCubesToThisTileAndExecuteAction host);
    T visit(ActivateCubes host);
    T visit(ScorePoints host);
    T visit(MoveOneOrTwoCubesSelectOrigin host);
    T visit(MoveCubesSelectDestination host);
    T visit(SelectWhereToEmptyTile host);
    T visit(MoveArchitectOut host);
    T visit(RemoveNeutralArchitect host);
    T visit(InformationOnZoneScore host);
    T visit(InformationOnIncompleteBuildingsScore host);
    T visit(InformationOnActiveCubesScore host);
    T visit(InformationOnBuildingsScore host);
  }

  /**
   * Generic message containing only text, no extra parameters, no graphics.
   */
  public class Text implements Message {
    private final String methodName;
    public Text(String methodName) {
      this.methodName = methodName;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public String getMethodName() {
      return methodName;
    }
  }

  /**
   * Generic message containing only text, no extra parameters, no graphics. This text is broken
   * automatically in multiple lines. The method name must refer to a method in
   * {@link com.philbeaudoin.quebec.client.resources.text.GraphicConstantsWithLookup}.
   */
  public class MultilineText implements Message {
    private final String methodName;
    private final double maxWidth;
    public MultilineText(String methodName) {
      this.methodName = methodName;
      this.maxWidth = 0.45;
    }
    public MultilineText(String methodName, double maxWidth) {
      this.methodName = methodName;
      this.maxWidth = maxWidth;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public String getMethodName() {
      return methodName;
    }
    public double getMaxWidth() {
      return maxWidth;
    }
  }

  /**
   * Take this leader card.
   */
  public class TakeThisLeaderCard extends BaseMessageWithCount implements Message {
    public TakeThisLeaderCard(int nbCubesToActivate, PlayerColor playerColor) {
      super(nbCubesToActivate, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move {architect A} or {architect B}.
   * Move the red or the neutral architect.
   */
  public class MoveEitherArchitect extends BaseMessage implements Message {
    public MoveEitherArchitect(PlayerColor architectA, PlayerColor architectB) {
      super(architectA, architectB);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move {architect}.
   * Select the red architect.
   */
  public class MoveArchitect extends BaseMessage implements Message {
    public MoveArchitect(PlayerColor architect) {
      super(architect);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send passive {cube}{cube} to {zone A} or {zone B}.
   * Send 2 passive cubes to the economic or religious zone.
   */
  public class SendPassiveCubesToOneOfTwoZones extends BaseMessageWithCount implements Message {
    public SendPassiveCubesToOneOfTwoZones(int nbCubes, PlayerColor playerColor,
        InfluenceType zone1, InfluenceType zone2) {
      super(nbCubes, array(playerColor), array(zone1, zone2));
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send active {cube}{cube} to {zone A} or {zone B}.
   * Send 2 active cubes to the economic or religious zone.
   */
  public class SendActiveCubesToOneOfTwoZones extends BaseMessageWithCount implements Message {
    public SendActiveCubesToOneOfTwoZones(int nbCubes, PlayerColor playerColor, InfluenceType zone1,
        InfluenceType zone2) {
      super(nbCubes, array(playerColor), array(zone1, zone2));
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send passive {cube}{cube} to {zone}.
   * Send 2 passive cubes to the economic zone.
   */
  public class SendPassiveCubesToZone extends BaseMessageWithCount implements Message {
    public SendPassiveCubesToZone(int nbCubes, PlayerColor playerColor, InfluenceType zone) {
      super(nbCubes, array(playerColor), array(zone));
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send active {cube}{cube} to {zone}.
   * Send 2 active cubes to the economic zone.
   */
  public class SendActiveCubesToZone extends BaseMessageWithCount implements Message {
    public SendActiveCubesToZone(int nbCubes, PlayerColor playerColor, InfluenceType zone) {
      super(nbCubes, array(playerColor), array(zone));
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send {cube} to {religious}{politic}{economic} or {cultural}.
   */
  public class SendPassiveCubesToAnyZone extends BaseMessageWithCount implements Message {
    public SendPassiveCubesToAnyZone(int nbCubes, PlayerColor playerColor) {
      super(nbCubes, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send {cube} to {religious}{politic}{economic}{cultural} or {citadel}.
   */
  public class SendPassiveCubesToAnyZoneOrCitadel extends BaseMessageWithCount implements Message {
    public SendPassiveCubesToAnyZoneOrCitadel(int nbCubes, PlayerColor playerColor) {
      super(nbCubes, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send passiv {cube}{cube} to this tile.
   * Send 2 active cubes to this tile.
   */
  public class SendPassiveCubesToThisTile extends BaseMessageWithCount implements Message {
    public SendPassiveCubesToThisTile(int nbCubes, PlayerColor playerColor) {
      super(nbCubes, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send active {cube}{cube} to this tile.
   * Send 2 active cubes to this tile.
   */
  public class SendActiveCubesToThisTile extends BaseMessageWithCount implements Message {
    public SendActiveCubesToThisTile(int nbCubes, PlayerColor playerColor) {
      super(nbCubes, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Send active {cube}{cube} to this tile and execute action.
   * Send 2 active cubes to this tile and execute action.
   */
  public class SendActiveCubesToThisTileAndExecuteAction extends BaseMessageWithCount
      implements Message {
    private final ActionType actionType;
    public SendActiveCubesToThisTileAndExecuteAction(int nbCubes, PlayerColor playerColor,
        ActionType actionType) {
      super(nbCubes, playerColor);
      this.actionType = actionType;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public ActionType getActionType() {
      return actionType;
    }
  }

  /**
   * Activate {cube}{cube}.
   * Activate 2 cubes.
   */
  public class ActivateCubes extends BaseMessageWithCount implements Message {
    public ActivateCubes(int nbCubes, PlayerColor playerColor) {
      super(nbCubes, playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Score 5 points.
   */
  public class ScorePoints extends BaseMessageWithCount implements Message {
    public ScorePoints(int nbPoints) {
      super(nbPoints);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move {white cube} or {white cube}{white cube}, select origin
   * Move 1 or 2 white cubes, select origin.
   */
  public class MoveOneOrTwoCubesSelectOrigin extends BaseMessage implements Message {
    public MoveOneOrTwoCubesSelectOrigin(PlayerColor playerColor) {
      super(playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move {white cube}{white cube} from {economic}, select destination
   * Move 2 white cubes from the economic zone, select destination.
   */
  public class MoveCubesSelectDestination extends BaseMessageWithCount implements Message {
    public MoveCubesSelectDestination(int nbCubes, PlayerColor playerColor, InfluenceType origin) {
      super(nbCubes, array(playerColor), array(origin));
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Player {white pawn}, send cubes to {religious}{politic}{economic} or {cultural}.
   * White player, send cubes to the religious, politic, economic or cultural zone.
   */
  public class SelectWhereToEmptyTile extends BaseMessage implements Message {
    public SelectWhereToEmptyTile(PlayerColor playerColor) {
      super(playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move {white architect} out, end century.
   */
  public class MoveArchitectOut extends BaseMessage implements Message {
    public MoveArchitectOut(PlayerColor playerColor) {
      super(playerColor);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Remove {neutral architect}, end century, do NOT activate cubes.
   */
  public class RemoveNeutralArchitect implements Message {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  // TODO(beaudoin): Refactor the following classes, they are all quite similar.

  /**
   * Option 1)
   * Scoring {religion}. Nobody scores.
   * Option 2)
   * Scoring {citadel}. {black pawn}: 1, {green pawn}:1. No Cascade.
   * Option 3)
   * Scoring {citadel}. {black pawn}: 3 {white pawn}: 6, {green pawn}: 6.
   * Cascades 3{white cube}{green cube}.
   */
  public class InformationOnZoneScore implements Message {
    private final ZoneScoringInformation scoringInformation;
    public InformationOnZoneScore(ZoneScoringInformation scoringInformation) {
      this.scoringInformation = scoringInformation;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public ZoneScoringInformation getScoringInformation() {
      return scoringInformation;
    }
  }

  /**
   * Option 1)
   * Scoring incomplete buildings. Nobody scores.
   * Option 2)
   * Scoring incomplete buildings. {black pawn}: 1, {green pawn}:4.
   */
  public class InformationOnIncompleteBuildingsScore implements Message {
    private final ScoringInformation scoringInformation;
    public InformationOnIncompleteBuildingsScore(ScoringInformation scoringInformation) {
      this.scoringInformation = scoringInformation;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public ScoringInformation getScoringInformation() {
      return scoringInformation;
    }
  }

  /**
   * Option 1)
   * Scoring active cubes. Nobody scores.
   * Option 2)
   * Scoring active cubes. {black pawn}: 1, {green pawn}:4.
   */
  public class InformationOnActiveCubesScore implements Message {
    private final ScoringInformation scoringInformation;
    public InformationOnActiveCubesScore(ScoringInformation scoringInformation) {
      this.scoringInformation = scoringInformation;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public ScoringInformation getScoringInformation() {
      return scoringInformation;
    }
  }

  /**
   * Option 1)
   * Scoring buildings. Nobody scores.
   * Option 2)
   * Scoring buildings. {black pawn}: 1, {green pawn}:4.
   */
  public class InformationOnBuildingsScore implements Message {
    private final ScoringInformation scoringInformation;
    public InformationOnBuildingsScore(ScoringInformation scoringInformation) {
      this.scoringInformation = scoringInformation;
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
    public ScoringInformation getScoringInformation() {
      return scoringInformation;
    }
  }
}
