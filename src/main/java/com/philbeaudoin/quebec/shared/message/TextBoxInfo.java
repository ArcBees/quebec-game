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

import com.philbeaudoin.quebec.shared.message.Message.MultilineText;

/**
 * Information about a text box, including the message, the logical location where it should be
 * anchored as well as the object it should be pointing to if any.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
public class TextBoxInfo {
  private final Message message;
  private final BoardLocation anchor;
  private final BoardLocation pointTo;
  public TextBoxInfo(Message message, BoardLocation anchor, BoardLocation pointTo) {
    assert message != null && anchor != BoardLocation.NONE;
    this.message = message;
    this.anchor = anchor;
    this.pointTo = pointTo;
  }
  public TextBoxInfo(MultilineText message, BoardLocation anchor) {
    assert message != null && anchor != BoardLocation.NONE;
    this.message = message;
    this.anchor = anchor;
    this.pointTo = BoardLocation.NONE;
  }
  public TextBoxInfo(Message message) {
    assert message != null;
    this.message = message;
    this.anchor = BoardLocation.TOP_CENTER;
    this.pointTo = BoardLocation.NONE;
  }
  public Message getMessage() {
    return message;
  }
  public BoardLocation getAnchor() {
    return anchor;
  }
  public BoardLocation getPointTo() {
    return pointTo;
  }
}
