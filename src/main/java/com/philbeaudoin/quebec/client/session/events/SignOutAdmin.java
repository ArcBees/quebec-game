/**
 * Copyright 2013 Philippe Beaudoin
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

package com.philbeaudoin.quebec.client.session.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SignOutAdmin {
  public interface Handler extends EventHandler {
    /**
     * Called when a client component wants to sign out as an admin in his current session.
     * @param event The {@link Event} containing the password.
     */
    void onSignOutAdmin(Event event);
  }

  public interface ErrorHandler extends EventHandler {
    /**
     * Called when signing out as an admin has failed.
     * @param event The {@link Event} containing the password.
     */
    void onSignOutAdminError(ErrorEvent event);
  }

  public static class Event extends GwtEvent<Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();


    public Event() {
    }

    @Override
    public Type<Handler> getAssociatedType() {
      return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
      handler.onSignOutAdmin(this);
    }
  }

  public static class ErrorEvent extends GwtEvent<ErrorHandler> {
    public static final Type<ErrorHandler> TYPE = new Type<ErrorHandler>();

    private final String error;

    public ErrorEvent(String error) {
      this.error = error;
    }

    @Override
    public Type<ErrorHandler> getAssociatedType() {
      return TYPE;
    }
    
    @Override
    protected void dispatch(ErrorHandler handler) {
      handler.onSignOutAdminError(this);
    }

    public String getError() {
      return error;
    }
  }
}
