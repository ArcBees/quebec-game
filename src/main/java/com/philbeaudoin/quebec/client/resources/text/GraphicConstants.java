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

import com.google.gwt.i18n.client.Constants;

/**
 * Methods to generate internationalized constants from a property file. These messages generally
 * contain images or are used in parameterized messages. For simple non-parameterized text only
 * messages use {@link GraphicConstantsWithLookup}.
 */
public interface GraphicConstants extends Constants {
  String takeThisLeaderCard();
  String takeThisLeaderCardAndActivateCubes();
  String moveEitherArchitect();
  String moveArchitect();
  String sendPassiveCubesToOneOfTwoZones();
  String sendActiveCubesToOneOfTwoZones();
  String sendPassiveCubesToZone();
  String sendActiveCubesToZone();
  String sendPassiveCubesToAnyZone();
  String sendPassiveCubesToAnyZoneOrCitadel();
  String sendPassiveCubesToThisTile();
  String sendActiveCubesToThisTile();
  String sendActiveCubesToThisTileAndExecuteAction();
  String activateCubes();
  String moveOneOrTwoCubesSelectOrigin();
  String moveCubesSelectDestination();
  String selectWhereToEmptyTile();
  String moveArchitectOut();
  String removeNeutralArchitect();
  String scoreZoneWithoutScore();
  String scoreZoneWithoutCascade();
  String scoreZoneWithCascade();
  String scoreIncompleteBuildingsWithoutScore();
  String scoreIncompleteBuildings();
  String scoreActiveCubesWithoutScore();
  String scoreActiveCubes();
  String scoreBuildingsWithoutScore();
  String scoreBuildings();
}