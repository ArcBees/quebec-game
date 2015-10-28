package com.philbeaudoin.quebec.server.handlers;

import javax.inject.Inject;
import javax.inject.Provider;

import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.user.UserManager;
import com.philbeaudoin.quebec.shared.action.AuthenticateWithDummyAction;
import com.philbeaudoin.quebec.shared.action.SessionInfoResult;
import com.philbeaudoin.quebec.shared.session.SessionInfoDto;

//TODO(beaudoin): Remove, only for testing.
public class AuthenticateWithDummyHandler
    implements ActionHandler<AuthenticateWithDummyAction, SessionInfoResult> {

  private final Provider<UserManager> userManager;
  
  @Inject
  AuthenticateWithDummyHandler(Provider<UserManager> userManager) {
    this.userManager = userManager;
  }

  @Override
  public SessionInfoResult execute(AuthenticateWithDummyAction action,
      ExecutionContext context) throws ActionException {
    return new SessionInfoResult(new SessionInfoDto(
        userManager.get().signIntoSessionWithDummy()));
  }

  @Override
  public Class<AuthenticateWithDummyAction> getActionType() {
    return AuthenticateWithDummyAction.class;
  }

  @Override
  public void undo(AuthenticateWithDummyAction action, SessionInfoResult result,
      ExecutionContext context)  throws ActionException {
    // Not undoable.
  }

}
