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

import com.philbeaudoin.quebec.shared.location.Location;
import com.philbeaudoin.quebec.shared.location.LocationTopCenter;

/**
 * Information about a text box, including the message, the logical location where it should be
 * anchored as well as the object it should be pointing to if any.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class TextBoxInfo {
  private final Message message;
  private final Location anchor;
  private final Location pointTo;
  public TextBoxInfo(Message message, Location anchor, Location pointTo) {
    assert message != null && anchor != null;
    this.message = message;
    this.anchor = anchor;
    this.pointTo = pointTo;
  }
  public TextBoxInfo(Message message, Location anchor) {
    assert message != null && anchor != null;
    this.message = message;
    this.anchor = anchor;
    this.pointTo = null;
  }
  public TextBoxInfo(Message message) {
    assert message != null;
    this.message = message;
    this.anchor = new LocationTopCenter();
    this.pointTo = null;
  }
  public Message getMessage() {
    return message;
  }
  public Location getAnchor() {
    return anchor;
  }
  public Location getPointTo() {
    return pointTo;
  }
}
