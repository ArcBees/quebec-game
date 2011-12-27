/**
 * Copyright 2011 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

/**
 * @author Philippe Beaudoin
 */
public interface Resources extends ClientBundle {

  @Source("board.jpg")
  DataResource board();

  @Source("yellow_4.png")
  DataResource tileEconomicFour();
  @Source("yellow_4_1.png")
  DataResource tileEconomicFour1();
  @Source("yellow_4_2.png")
  DataResource tileEconomicFour2();
  @Source("yellow_3.png")
  DataResource tileEconomicThree();
  @Source("yellow_3_1.png")
  DataResource tileEconomicThree1();
  @Source("yellow_3_2.png")
  DataResource tileEconomicThree2();
  @Source("yellow_3_3.png")
  DataResource tileEconomicThree3();
  @Source("yellow_3_4.png")
  DataResource tileEconomicThree4();
  @Source("yellow_2.png")
  DataResource tileEconomicTwo();
  @Source("yellow_2_1.png")
  DataResource tileEconomicTwo1();
  @Source("yellow_2_2.png")
  DataResource tileEconomicTwo2();
  @Source("yellow_1.png")
  DataResource tileEconomicOne();
  @Source("yellow_1_1.png")
  DataResource tileEconomicOne1();
  @Source("yellow_1_2.png")
  DataResource tileEconomicOne2();
  @Source("yellow_1_3.png")
  DataResource tileEconomicOne3();
  @Source("blue_4.png")
  DataResource tileCulturalFour();
  @Source("blue_4_1.png")
  DataResource tileCulturalFour1();
  @Source("blue_4_2.png")
  DataResource tileCulturalFour2();
  @Source("blue_4_3.png")
  DataResource tileCulturalFour3();
  @Source("blue_4_4.png")
  DataResource tileCulturalFour4();
  @Source("blue_3.png")
  DataResource tileCulturalThree();
  @Source("blue_3_1.png")
  DataResource tileCulturalThree1();
  @Source("blue_3_2.png")
  DataResource tileCulturalThree2();
  @Source("blue_2.png")
  DataResource tileCulturalTwo();
  @Source("blue_2_1.png")
  DataResource tileCulturalTwo1();
  @Source("blue_2_2.png")
  DataResource tileCulturalTwo2();
  @Source("blue_2_3.png")
  DataResource tileCulturalTwo3();
  @Source("blue_1.png")
  DataResource tileCulturalOne();
  @Source("blue_1_1.png")
  DataResource tileCulturalOne1();
  @Source("blue_1_2.png")
  DataResource tileCulturalOne2();
  @Source("purple_4.png")
  DataResource tileReligiousFour();
  @Source("purple_4_1.png")
  DataResource tileReligiousFour1();
  @Source("purple_4_2.png")
  DataResource tileReligiousFour2();
  @Source("purple_3.png")
  DataResource tileReligiousThree();
  @Source("purple_3_1.png")
  DataResource tileReligiousThree1();
  @Source("purple_3_2.png")
  DataResource tileReligiousThree2();
  @Source("purple_3_3.png")
  DataResource tileReligiousThree3();
  @Source("purple_2.png")
  DataResource tileReligiousTwo();
  @Source("purple_2_1.png")
  DataResource tileReligiousTwo1();
  @Source("purple_2_2.png")
  DataResource tileReligiousTwo2();
  @Source("purple_1.png")
  DataResource tileReligiousOne();
  @Source("purple_1_1.png")
  DataResource tileReligiousOne1();
  @Source("purple_1_2.png")
  DataResource tileReligiousOne2();
  @Source("purple_1_3.png")
  DataResource tileReligiousOne3();
  @Source("purple_1_4.png")
  DataResource tileReligiousOne4();
  @Source("red_4.png")
  DataResource tilePoliticFour();
  @Source("red_4_1.png")
  DataResource tilePoliticFour1();
  @Source("red_4_2.png")
  DataResource tilePoliticFour2();
  @Source("red_4_3.png")
  DataResource tilePoliticFour3();
  @Source("red_3.png")
  DataResource tilePoliticThree();
  @Source("red_3_1.png")
  DataResource tilePoliticThree1();
  @Source("red_3_2.png")
  DataResource tilePoliticThree2();
  @Source("red_2.png")
  DataResource tilePoliticTwo();
  @Source("red_2_1.png")
  DataResource tilePoliticTwo1();
  @Source("red_2_2.png")
  DataResource tilePoliticTwo2();
  @Source("red_2_3.png")
  DataResource tilePoliticTwo3();
  @Source("red_2_4.png")
  DataResource tilePoliticTwo4();
  @Source("red_1.png")
  DataResource tilePoliticOne();
  @Source("red_1_1.png")
  DataResource tilePoliticOne1();
  @Source("red_1_2.png")
  DataResource tilePoliticOne2();
  @Source("quebec.css")
  Style style();

  /**
   * @author Philippe Beaudoin
   */
  public interface Style extends CssResource {
    String fullSize();
  }
}
