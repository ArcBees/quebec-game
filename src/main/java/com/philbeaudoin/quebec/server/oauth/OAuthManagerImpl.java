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

package com.philbeaudoin.quebec.server.oauth;

import java.io.IOException;

import javax.inject.Inject;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.database.GlobalStringEntity;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
import com.philbeaudoin.quebec.server.session.SessionInfoEntity;
import com.philbeaudoin.quebec.server.user.GoogleUserEntity;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.session.SessionInfo;

public class OAuthManagerImpl implements OAuthManager, ObjectifyServiceWrapper {

  private static final String CLIENT_ID = "161973045206.apps.googleusercontent.com";
  private static final String APPLICATION_NAME = "Quebec Boardgame";

  private final ObjectifyServiceWrapper objectifyServiceWrapper;
  private final HttpTransport transport;
  private final JsonFactory jsonFactory;
  private final ServerSessionManager sessionManager;

  @Inject
  public OAuthManagerImpl(ObjectifyServiceWrapper objectifyServiceWrapper, HttpTransport transport,
      JsonFactory jsonFactory, ServerSessionManager sessionManager) {
    this.objectifyServiceWrapper = objectifyServiceWrapper;
    this.transport = transport;
    this.jsonFactory = jsonFactory;
    this.sessionManager = sessionManager;
  }

  @Override
  public Objectify ofy() {
    return objectifyServiceWrapper.ofy();
  }

  @Override
  public void saveClientSecret(String clientSecret) throws OperationNotAllowedException {
    SessionInfo sessionInfo = sessionManager.getSessionInfo();
    if (!sessionInfo.isAdmin() || clientSecret == null) {
      throw new OperationNotAllowedException();
    }
    GlobalStringEntity passwordEntity = new GlobalStringEntity(
        GlobalStringEntity.GOOGLE_OAUTH_CLIENT_SECRET, clientSecret);
    ofy().save().entity(passwordEntity);
  }

  @Override
  public UserInfoEntity authenticate(String code) throws ActionException {
    String clientSecret = loadClientSecret();
    if (clientSecret == null) {
      throw new ActionException("No client secret in the datastore, cannot run OAuth 2.0.");
    }

    GoogleTokenResponse tokenResponse;
    GoogleCredential credential;
    try {
      // Upgrade the authorization code into an access and refresh token.
      tokenResponse = new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory, CLIENT_ID,
          clientSecret, code, "postmessage").execute();
      // Create a credential representation of the token data.
      credential = new GoogleCredential.Builder()
          .setJsonFactory(jsonFactory)
          .setTransport(transport)
          .setClientSecrets(CLIENT_ID, clientSecret).build()
          .setFromTokenResponse(tokenResponse);

      // Check that the token is valid.
      Oauth2 oauth2 = new Oauth2.Builder(
          transport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build();
      Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(
          credential.getAccessToken()).execute();
      // If there was an error in the token info, abort.
      if (tokenInfo.containsKey("error")) {
        throw new ActionException(tokenInfo.get("error").toString());
      }
      // Make sure the token we got is for the intended user.
      if (!tokenInfo.getIssuedTo().equals(CLIENT_ID)) {
        throw new ActionException("Token's client ID does not match app's.");
      }
    } catch (TokenResponseException e) {
      throw new ActionException("Failed to upgrade the authorization code.");
    } catch (IOException e) {
      throw new ActionException("Failed to read token data from Google. " + e.getMessage());
    }

    // Query the user ID.
    Plus service = new Plus.Builder(transport, jsonFactory, credential)
        .setApplicationName(APPLICATION_NAME).build();

    // Get a list of people that this user has shared with this app.
    Person person;
    try {
      person = service.people().get("me").execute();
    } catch (IOException e) {
      throw new ActionException("Call to 'people' service failed. " + e.getMessage());
    }

    if (person.getId() == null) {
      throw new ActionException("Google account has null ID, unsupported");
    }

    // Recoup with the session's user.
    SessionInfoEntity sessionInfo = sessionManager.getSessionInfo();
    UserInfoEntity userInfo = sessionInfo.getUserInfoEntity();

    // TODO(beaudoin): Change the logic from that point forward to allow associating a Google+
    //     account to an already existing user.
    if (userInfo != null && !person.getId().equals(userInfo.getGoogleId())) {
      // Sign out current user and sign-in the new one.
      sessionInfo = sessionManager.signOut();
      userInfo = sessionInfo.getUserInfoEntity();  // Should be null.
      assert(userInfo == null);
    }

    // At that point, either no user is attached to the session or it's attached to the right
    // GoogleId.
    if (userInfo == null) {
      try {
        userInfo = ofy().transact(new GetOrCreateUserWithGoogleId(person));
      } catch(Exception e) {
        throw new ActionException(e.getMessage());
      }
      sessionManager.attachUserInfoToSession(userInfo);
    }
    assert(userInfo.getId() != null);

    try {
      // Upgrade user info entity with the best possible Google token response.
      tokenResponse = mergeTokenResponses(userInfo.getGoogleTokenResponse(), tokenResponse);
    } catch (IOException e) {
      throw new ActionException("Failed to merge Google token responses. " + e.getMessage());
    }
    userInfo.setGoogleTokenResponse(tokenResponse.toString());
    ofy().save().entity(userInfo).now();

    return userInfo;
  }


  private Key<GlobalStringEntity> getClientSecretKey() {
    return Key.create(GlobalStringEntity.class, GlobalStringEntity.GOOGLE_OAUTH_CLIENT_SECRET);
  }

  private String loadClientSecret() {
    return ofy().load().key(getClientSecretKey()).get().getString();
  }

  /**
   * Takes an old and a new Google token response and merge their information. Keeps all the
   * information from the newest response, save maybe for the refresh token. If the refresh token
   * is null in the newest response and non-null in the older response, then the one from the older
   * response is kept.
   * @param oldestString Older token response, that may have a valid refresh token. Can be null.
   * @param newest Newer token response, that may have a null refresh token. Will be modified.
   * @return The best possible Google token response.
   * @throws IOException If the json cannot be converted.
   */
  private GoogleTokenResponse mergeTokenResponses(String oldestString, GoogleTokenResponse newest)
      throws IOException {
    if (newest.getRefreshToken() == null && oldestString != null) {
      GoogleTokenResponse oldest = jsonFactory.fromString(oldestString, GoogleTokenResponse.class);
      newest.setRefreshToken(oldest.getRefreshToken());
    }
    return newest;
  }

  /**
   * Class meant to be performed in a transaction. Will either get the {@link UserInfoEntity} given
   * the provided google id. If no user correspond to the provided user id in the datastore, a new
   * one will be created.
   */
  private class GetOrCreateUserWithGoogleId implements Work<UserInfoEntity> {
    private final Person person;
    GetOrCreateUserWithGoogleId(Person person) {
      this.person = person;
    }

    @Override
    public UserInfoEntity run() {
      GoogleUserEntity googleUserEntity = ofy().load().type(GoogleUserEntity.class)
          .id(person.getId()).get();
      if (googleUserEntity != null) {
        return googleUserEntity.getUserInfoEntity();
      }
      UserInfoEntity userInfoEntity = UserInfoEntity.Create(person.getId());
      ofy().save().entity(userInfoEntity).now();
      googleUserEntity = GoogleUserEntity.Create(person.getId(), userInfoEntity);
      ofy().save().entity(googleUserEntity).now();
      return userInfoEntity;
    }
  }

}
