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
    T visit(MoveYourArchitect host);
    T visit(MoveYourArchitectToThisTile host);
    T visit(MoveEitherArchitect host);
    T visit(SelectWhichArchitect host);
    T visit(SendPassiveCubesToOneOfTwoZones host);
    T visit(SendActiveCubesToOneOfTwoZones host);
    T visit(SendPassiveCubesToZone host);
    T visit(SendActiveCubesToZone host);
    T visit(SelectAction host);
    T visit(SendPassiveCubesToAnyZone host);
    T visit(SendPassiveCubesToAnyZoneOrCitadel host);
    T visit(TakeThisLeaderCard host);
    T visit(SendPassiveCubesToThisTile host);
    T visit(SendActiveCubesToThisTile host);
    T visit(SendActiveCubesToThisTileAndExecuteAction host);
    T visit(ActivateCubes host);
    T visit(Skip host);
    T visit(ScorePoints host);
    T visit(SelectStarTokenToIncrease host);
  }

  /**
   * Select a tile to move your architect.
   */
  public class MoveYourArchitect implements Message {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Move your architect to this tile.
   */
  public class MoveYourArchitectToThisTile implements Message {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Take this leader card.
   */
  public class TakeThisLeaderCard implements Message {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Skip.
   */
  public class Skip implements Message {
    @Override
    public <T> T accept(Visitor<T> visitor) {
      return visitor.visit(this);
    }
  }

  /**
   * Select star token to increase.
   */
  public class SelectStarTokenToIncrease implements Message {
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
   * Select {architect A} or {architect B}.
   * Select the red or the neutral architect.
   */
  public class SelectWhichArchitect extends BaseMessage implements Message {
    public SelectWhichArchitect(PlayerColor architectA, PlayerColor architectB) {
      super(architectA, architectB);
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
   * Select action to execute.
   */
  public class SelectAction implements Message {
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
}
