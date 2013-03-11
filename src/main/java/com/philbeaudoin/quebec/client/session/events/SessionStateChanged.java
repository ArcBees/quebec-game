package com.philbeaudoin.quebec.client.session.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.philbeaudoin.quebec.shared.session.SessionInfo;

public interface SessionStateChanged {
  public interface Handler extends EventHandler {
    /**
     * Called when the session state has been observed to change.
     * @param event The {@link Event} containing the session info.
     */
    void onSessionStateChanged(Event event);
  }
  
  public static class Event extends GwtEvent<Handler> {
    public static final Type<Handler> TYPE = new Type<Handler>();

    private final SessionInfo sessionInfo;

    public Event(SessionInfo sessionInfo) {
      this.sessionInfo = sessionInfo;
    }

    @Override
    public Type<Handler> getAssociatedType() {
      return TYPE;
    }
    
    @Override
    protected void dispatch(Handler handler) {
      handler.onSessionStateChanged(this);
    }

    public SessionInfo getSessionInfo() {
      return sessionInfo;
    }
  }
}
