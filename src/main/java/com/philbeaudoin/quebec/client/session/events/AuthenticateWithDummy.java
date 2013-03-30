package com.philbeaudoin.quebec.client.session.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

// TODO(beaudoin): Remove, only for testing.
public interface AuthenticateWithDummy {
  public interface Handler extends EventHandler {
    void onAuthenticateWithDummy(Event event);
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
      handler.onAuthenticateWithDummy(this);
    }
  }
}
