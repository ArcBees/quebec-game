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
  String actionPurple1();
  String actionPurple2();
  String actionPurple3();
  String actionPurple4();
  String actionRed1();
  String actionRed2();
  String actionRed3();
  String actionRed4();
  String actionYellow1();
  String actionYellow2();
  String actionYellow3();
  String actionYellow4();
  String actionBlue1();
  String actionBlue2();
  String actionBlue3();
  String actionBlue4();
  String tutorialIntro();
  String tutorialPlayers();
  String tutorialGoal();
  String tutorialArchitect();
  String tutorialPassiveCubes();
  String tutorialActiveCubes();
  String tutorialFirstMove();
  String tutorialWhereToMoveArchitect();
  String tutorialPerformMoveArchitect();
  String tutorialActivateAfterMove();
  String tutorialGoToSecondPlayerFirstMove();
  String tutorialSecondPlayerFirstMove();
  String tutorialThirdPlayerFirstMove();
  String tutorialFourthPlayerFirstMove();
  String tutorialSendActiveWorkers();
  String tutorialWhereToSendWorkers();
  String tutorialThreeSpotsPerTile();
  String tutorialCostOfAContribution();
  String tutorialPerformSendCubes();
  String tutorialActionCanBeExecuted();
  String tutorialNowExecuteAction();
  String tutorialAllDistrictHaveAction();
  String tutorialDontWorry();
  String tutorialJumpAheadOneTurn();
  String tutorialContributeToOwnBuilding();
  String tutorialContributingToOwnBuilding();
  String tutorialMoveArchitectAgain();
  String tutorialAfterArchitectMove1();
  String tutorialAfterArchitectMove2();
  String tutorialAfterArchitectMove3();
  String tutorialSelectLeader();
  String tutorialPickALeader();
  String tutorialLeaderActivateCubes();
  String tutorialJumpNearTheEnd();
  String tutorialCouldMoveArchitectToEnd();
  String tutorialSendToInfluenceZone();
  String tutorialEndOfCentury();
  String tutorialReturnAllCards();
  String tutorialScoringCitadel();
  String tutorialMajorityCitadel();
  String tutorialCascadingOrder();
  String tutorialScoringReligious();
  String tutorialScoringPolitics();
  String tutorialScoringEconomic();
  String tutorialScoringCultural();
  String tutorialSecondCenturyBegins();
  String tutorialJumpToFinalScoring();
  String tutorialIntroToFinalScoring();
  String tutorialLargestBlackGroup();
  String tutorialSmallBlackGroups();
  String tutorialOthersScoreStarTokens();
  String tutorialScoreCubesOnBuildings();
  String tutorialScoreActiveCubes();
  String tutorialGameComplete();
}
