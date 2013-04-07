package com.philbeaudoin.quebec.server.user;

import java.io.IOException;
import java.security.SecureRandom;

import javax.inject.Inject;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;

public class UserManagerImpl implements UserManager, ObjectifyServiceWrapper {

  private final ObjectifyServiceWrapper objectifyServiceWrapper;
  private final OAuthManager oauthManager;
  private final ServerSessionManager sessionManager;
  private final SecureRandom random;


  @Inject
  public UserManagerImpl(ObjectifyServiceWrapper objectifyServiceWrapper,
      OAuthManager oauthManager,
      ServerSessionManager sessionManager) {
    this.objectifyServiceWrapper = objectifyServiceWrapper;
    this.oauthManager = oauthManager;
    this.sessionManager = sessionManager;
    this.random = new SecureRandom();
  }

  @Override
  public Objectify ofy() {
    return objectifyServiceWrapper.ofy();
  }

  @Override
  public SessionInfoEntity signIntoSessionWithGoogleAuthenticationCode(String code)
      throws ActionException {

    UserInfoEntity userToSignIn = oauthManager.authenticate(code);

    if (userToSignIn.getGoogleId() == null || userToSignIn.getGoogleId().isEmpty()) {
      throw new ActionException("Invalid GoogleId when signing into session.");
    }

    // Recoup with the session's user.
    SessionInfoEntity sessionInfo = sessionManager.getSessionInfo();
    UserInfoEntity userInfo = sessionInfo.getUserInfoEntity();

    // TODO(beaudoin): Change the logic from that point forward to allow associating a Google+
    //     account to an already existing user.
    if (userInfo != null && !userToSignIn.getGoogleId().equals(userInfo.getGoogleId())) {
      // Sign out current user and sign-in the new one.
      sessionInfo = sessionManager.signOut();
      userInfo = sessionInfo.getUserInfoEntity();
      assert(userInfo == null);
    }

    try {
      userInfo = ofy().transact(new UpdateOrCreateUserWithGoogleId(userToSignIn));
    } catch(Exception e) {
      throw new ActionException(e.getMessage());
    }
    assert(userInfo != null);

    try {
      // Upgrade user info entity with the best possible Google token response.
      String tokenResponse = oauthManager.mergeTokenResponses(userInfo.getGoogleTokenResponse(),
          userToSignIn.getGoogleTokenResponse());
      userInfo.setGoogleTokenResponse(tokenResponse.toString());
    } catch (IOException e) {
      throw new ActionException("Failed to merge Google token responses. " + e.getMessage());
    }
    ofy().save().entity(userInfo).now();

    return sessionManager.attachUserInfoToSession(userInfo);
  }

  @Override
  public SessionInfoEntity signIntoSessionWithDummy() {
    // Recoup with the session's user.
    SessionInfoEntity sessionInfo = sessionManager.getSessionInfo();
    UserInfoEntity userInfo = sessionInfo.getUserInfoEntity();

    // TODO(beaudoin): Change the logic from that point forward to allow associating a Google+
    //     account to an already existing user.
    if (userInfo != null) {
      // Sign out current user and sign-in the new one.
      sessionInfo = sessionManager.signOut();
      userInfo = sessionInfo.getUserInfoEntity();
      assert(userInfo == null);
    }

    String randomString = Integer.toString(Math.abs(random.nextInt()));
    userInfo = UserInfoEntity.Create();
    userInfo.setEmail("dummy" + randomString + "@dummy.com");
    userInfo.setName("Dummy " + randomString);

    ofy().save().entity(userInfo).now();

    return sessionManager.attachUserInfoToSession(userInfo);
  }

  /**
   * Class meant to be performed in a transaction. Will either get the {@link UserInfoEntity} given
   * the provided google id. If no user correspond to the provided user id in the datastore, a new
   * one will be created.
   */
  private class UpdateOrCreateUserWithGoogleId implements Work<UserInfoEntity> {
    private final UserInfoEntity userToSignIn;
    UpdateOrCreateUserWithGoogleId(UserInfoEntity userToSignIn) {
      assert(userToSignIn.getEmail() != null);
      this.userToSignIn = userToSignIn;
    }

    @Override
    public UserInfoEntity run() {
      GoogleUserEntity googleUserEntity = ofy().load().type(GoogleUserEntity.class)
          .id(userToSignIn.getGoogleId()).get();
      UserInfoEntity userInfoEntity;
      boolean userInfoEntitymodified = false;
      if (googleUserEntity != null) {
        userInfoEntity = googleUserEntity.getUserInfoEntity();
      } else {
        userInfoEntitymodified = true;
        userInfoEntity = UserInfoEntity.CreateWithGoogleId(userToSignIn.getGoogleId());
        ofy().save().entity(userInfoEntity).now();
      }
      String email = userToSignIn.getEmail();
      if (!email.equals(userInfoEntity.getEmail())) {
        userInfoEntity.setEmail(email);
        userInfoEntitymodified = true;
      }
      String name = userToSignIn.getName();
      if (name != null && !name.equals(userInfoEntity.getName())) {
        userInfoEntity.setName(name);
        userInfoEntitymodified = true;
      }
      if (userInfoEntitymodified) {
        ofy().save().entity(userInfoEntity).now();
      }
      if (googleUserEntity == null) {
        googleUserEntity = GoogleUserEntity.Create(userToSignIn.getGoogleId(), userInfoEntity);
        ofy().save().entity(googleUserEntity).now();
      }
      return userInfoEntity;
    }
  }
}
