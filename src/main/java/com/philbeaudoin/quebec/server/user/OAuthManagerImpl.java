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

package com.philbeaudoin.quebec.server.user;

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
import com.gwtplatform.dispatch.shared.ActionException;
import com.philbeaudoin.quebec.server.database.GlobalStringEntity;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.session.ServerSessionManager;
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
    String userEmail;
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
      userEmail = tokenInfo.getEmail();
    } catch (TokenResponseException e) {
      throw new ActionException("Failed to upgrade the authorization code.");
    } catch (IOException e) {
      throw new ActionException("Failed to read token data from Google. " + e.getMessage());
    }

    if (userEmail == null || userEmail.length() == 0) {
      throw new ActionException("Google account has no email address, unsupported");
    }

    // Query the user ID.
    Plus service = new Plus.Builder(transport, jsonFactory, credential)
        .setApplicationName(APPLICATION_NAME).build();

    // Get complete user's details.
    Person person;
    try {
      person = service.people().get("me").execute();
    } catch (IOException e) {
      throw new ActionException("Call to 'people' service failed. " + e.getMessage());
    }

    if (person.getId() == null) {
      throw new ActionException("Google account has null ID, unsupported");
    }

    UserInfoEntity authenticatedUser = UserInfoEntity.CreateWithGoogleId(person.getId());
    authenticatedUser.setEmail(userEmail);
    authenticatedUser.setName(person.getDisplayName());
    authenticatedUser.setGoogleTokenResponse(tokenResponse.toString());

    return authenticatedUser;
  }

  @Override
  public String mergeTokenResponses(String oldestString, String newestString)
      throws IOException {
    GoogleTokenResponse newest = jsonFactory.fromString(newestString, GoogleTokenResponse.class);
    if (newest.getRefreshToken() == null && oldestString != null) {
      GoogleTokenResponse oldest = jsonFactory.fromString(oldestString, GoogleTokenResponse.class);
      newest.setRefreshToken(oldest.getRefreshToken());
    }
    return newest.toString();
  }

  private Key<GlobalStringEntity> getClientSecretKey() {
    return Key.create(GlobalStringEntity.class, GlobalStringEntity.GOOGLE_OAUTH_CLIENT_SECRET);
  }

  private String loadClientSecret() {
    return ofy().load().key(getClientSecretKey()).get().getString();
  }

}
