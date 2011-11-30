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

/*
  @Source("ronds__0000s_0000_Calque-60.png")
  @Source("ronds__0000s_0001_Calque-59.png")
  @Source("ronds__0000s_0002_Calque-58.png")
  @Source("ronds__0000s_0003_Calque-57.png")
  @Source("ronds__0000s_0004_Calque-56.png")
  @Source("ronds__0000s_0005_Calque-55.png")
  @Source("ronds__0000s_0006_Calque-54.png")
  @Source("ronds__0000s_0007_Calque-53.png")
  @Source("ronds__0000s_0008_Calque-52.png")
  @Source("ronds__0000s_0009_Calque-51.png")
  @Source("ronds__0000s_0010_Calque-50.png")
  @Source("ronds__0000s_0011_Calque-49.png")
  @Source("ronds__0000s_0012_Calque-48.png")
  @Source("ronds__0000s_0013_Calque-47.png")
  @Source("ronds__0000s_0014_Calque-42.png")
  @Source("ronds__0000s_0015_Calque-41.png")
  @Source("ronds__0000s_0016_Calque-40.png")
  @Source("ronds__0000s_0017_Calque-39.png")
  @Source("ronds__0000s_0018_Calque-38.png")
  @Source("ronds__0000s_0019_Calque-37.png")
  @Source("ronds__0000s_0020_Calque-36.png")
  @Source("ronds__0000s_0021_Calque-35.png")
  @Source("ronds__0000s_0022_Calque-34.png")
  @Source("ronds__0000s_0023_Calque-33.png")
  @Source("ronds__0000s_0024_Calque-32.png")
  @Source("ronds__0000s_0025_Calque-31.png")
  @Source("ronds__0000s_0026_Calque-30.png")
  @Source("ronds__0000s_0027_Calque-29.png")
  @Source("ronds__0000s_0028_Calque-28.png")
  @Source("ronds__0000s_0029_Calque-22.png")
  @Source("ronds__0000s_0030_Calque-21.png")
  @Source("ronds__0000s_0031_Calque-20.png")
  @Source("ronds__0000s_0032_Calque-19.png")
  @Source("ronds__0000s_0033_Calque-18.png")
  @Source("ronds__0000s_0034_Calque-17.png")
  @Source("ronds__0000s_0035_Calque-16.png")
  @Source("ronds__0000s_0036_Calque-15.png")
  @Source("ronds__0000s_0037_Calque-14.png")
  @Source("ronds__0000s_0038_Calque-13.png")
  @Source("ronds__0000s_0039_Calque-12.png")
  @Source("ronds__0000s_0040_Calque-11.png")
  @Source("ronds__0000s_0041_Calque-10.png")
  @Source("ronds__0000s_0042_Calque-9.png")
  @Source("ronds__0000s_0043_Calque-8.png") */
  @Source("ronds__0000s_0044_Calque-46.png")
  DataResource tileEconomicFour();
  @Source("ronds__0000s_0045_Calque-27.png")
  DataResource tileEconomicThree();
  @Source("ronds__0000s_0046_Calque-23.png")
  DataResource tileEconomicTwo();
  @Source("ronds__0000s_0047_Calque-7.png")
  DataResource tileEconomicOne();
  @Source("ronds__0000s_0048_Calque-44.png")
  DataResource tileCulturalFour();
  @Source("ronds__0000s_0049_Calque-26.png")
  DataResource tileCulturalThree();
  @Source("ronds__0000s_0050_Calque-4.png")
  DataResource tileCulturalTwo();
  @Source("ronds__0000s_0051_Calque-3.png")
  DataResource tileCulturalOne();
  @Source("ronds__0000s_0052_Calque-43.png")
  DataResource tileReligiousFour();
  @Source("ronds__0000s_0053_Calque-25.png")
  DataResource tileReligiousThree();
  @Source("ronds__0000s_0054_Calque-6.png")
  DataResource tileReligiousTwo();
  @Source("ronds__0000s_0055_Calque-1.png")
  DataResource tileReligiousOne();
  @Source("ronds__0000s_0056_Calque-45.png")
  DataResource tilePoliticFour();
  @Source("ronds__0000s_0057_Calque-24.png")
  DataResource tilePoliticThree();
  @Source("ronds__0000s_0058_Calque-5.png")
  DataResource tilePoliticTwo();
  @Source("ronds__0000s_0059_Calque-2.png")
  DataResource tilePoliticOne();
  @Source("quebec.css")
  Style style();

  /**
   * @author Philippe Beaudoin
   */
  public interface Style extends CssResource {
    String fullSize();
  }
}
