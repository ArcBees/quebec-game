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

package com.philbeaudoin.quebec.client.resources.text;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * Methods to generate internationalized constants from a property file. These can be looked up
 * from method name, making it easier to pass a message from the shared package to the client
 * package.
 */
public interface GraphicConstantsWithLookup extends ConstantsWithLookup {
  String moveYourArchitect();
  String moveYourArchitectToThisTile();
  String skip();
  String continueMsg();
  String selectStarTokenToIncrease();
  String selectSpotToFill();
  String scoringPhaseBegins();
  String prepareNextCentury();
  String selectActionToExecute();
  String gameCompleted();
  String religiousLeaderDescription();
  String politicLeaderDescription();
  String economicLeaderDescription();
  String cultural23LeaderDescription();
  String cultural45LeaderDescription();
  String citadelLeaderDescription();
  String tutorialIntro();
  String tutorialPlayers();
  String tutorialGoal();
  String tutorialArchitect();
  String tutorialPassiveCubes();
  String tutorialActiveCubes();
  String tutorialFirstMove();
}
