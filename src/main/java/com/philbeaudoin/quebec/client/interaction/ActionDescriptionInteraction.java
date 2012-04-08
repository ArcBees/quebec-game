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

package com.philbeaudoin.quebec.client.interaction;

import java.util.ArrayList;

import javax.inject.Inject;

import com.google.gwt.user.client.Timer;
import com.google.inject.assistedinject.Assisted;
import com.philbeaudoin.quebec.client.renderer.GameStateRenderer;
import com.philbeaudoin.quebec.client.renderer.TextBoxRenderer;
import com.philbeaudoin.quebec.client.scene.SceneNode;
import com.philbeaudoin.quebec.client.scene.SceneNodeList;
import com.philbeaudoin.quebec.shared.location.Location;
import com.philbeaudoin.quebec.shared.location.LocationBoardAction;
import com.philbeaudoin.quebec.shared.location.LocationRelative;
import com.philbeaudoin.quebec.shared.message.TextBoxInfo;
import com.philbeaudoin.quebec.shared.state.Board;
import com.philbeaudoin.quebec.shared.state.BoardAction;
import com.philbeaudoin.quebec.shared.utils.Vector2d;

/**
 * An interaction that displays an explanation for each building action if you hover the mouse over
 * it for long enough.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class ActionDescriptionInteraction {

  private final GameStateRenderer gameStateRenderer;
  private final ArrayList<ActionInfo> actionInfos = new ArrayList<ActionInfo>();

  // Keep the currently active ActionInfo. An ActionInfo is active if the mouse is over it, whether
  // it's displayed or not.
  ActionInfo currentlyActive;
  private final Timer timer;

  private SceneNode lastAddedNode;

  @Inject
  ActionDescriptionInteraction(TextBoxRenderer textBoxRenderer,
      @Assisted GameStateRenderer gameStateRenderer) {
    final BoardAction[] boardActions = Board.getAllActions();
    this.gameStateRenderer = gameStateRenderer;

    timer = new Timer() {
      @Override
      public void run() {
        displayTextbox();
      }
    };

    for (BoardAction boardAction : boardActions) {

      Location actionLocation = new LocationBoardAction(boardAction);
      Location to = new LocationRelative(actionLocation, new Vector2d(0, 1.2));
      Location anchor = new LocationRelative(actionLocation, new Vector2d(0, 2));
      SceneNodeList textbox = textBoxRenderer.render(
          new TextBoxInfo(boardAction.getDescription(), anchor, to), gameStateRenderer);

      Vector2d pos = gameStateRenderer.getActionTransform(boardAction).getTranslation(0);
      ActionInfo actionInfo = new ActionInfo(new CircleTrigger(pos, 0.044), textbox);
      actionInfos.add(actionInfo);
    }
  }

  public void onMouseMove(double x, double y) {
    ActionInfo activeActionInfo = null;
    for (ActionInfo actionInfo : actionInfos) {
      if (actionInfo.trigger.triggerAt(x, y)) {
        activeActionInfo = actionInfo;
        break;
      }
    }
    if (activeActionInfo != currentlyActive) {
      timer.cancel();
      currentlyActive = activeActionInfo;
      if (currentlyActive != null) {
        timer.schedule(800);
      } else {
        if (lastAddedNode != null) {
          lastAddedNode.setParent(null);
          lastAddedNode = null;
        }
      }
    }
  }

  private void displayTextbox() {
    assert currentlyActive != null;
    if (lastAddedNode != null) {
      lastAddedNode.setParent(null);
    }
    lastAddedNode = currentlyActive.textbox;
    gameStateRenderer.addToAnimationGraph(lastAddedNode);
  }

  private class ActionInfo {
    final Trigger trigger;
    final SceneNodeList textbox;
    public ActionInfo(Trigger trigger, SceneNodeList textbox) {
      this.trigger = trigger;
      this.textbox = textbox;
    }
  }
}
