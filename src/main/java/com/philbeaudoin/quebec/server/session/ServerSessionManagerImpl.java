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

package com.philbeaudoin.quebec.server.session;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.VoidWork;
import com.gwtplatform.dispatch.server.guice.SecureRandomSingleton;
import com.philbeaudoin.quebec.client.session.ClientSessionManager;
import com.philbeaudoin.quebec.server.database.GlobalStringEntity;
import com.philbeaudoin.quebec.server.database.ObjectifyServiceWrapper;
import com.philbeaudoin.quebec.server.exceptions.OperationNotAllowedException;
import com.philbeaudoin.quebec.server.user.UserInfoEntity;
import com.philbeaudoin.quebec.shared.Constants;
import com.philbeaudoin.quebec.shared.session.SessionInfo;

/**
 * Implementation of {@link ClientSessionManager}.
 *
 * @author Philippe Beaudoin <philippe.beaudoin@gmail.com>
 */
@RequestScoped
public class ServerSessionManagerImpl implements ServerSessionManager, ObjectifyServiceWrapper {

  private final static int MAX_SESSION_DURATION_S = 30 * 24 * 60 * 60;  // 30 Days.

  private final ObjectifyServiceWrapper objectifyServiceWrapper;
  private final SecureRandom random;
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final HashFunction hashFunction;

  @Inject
  public ServerSessionManagerImpl(ObjectifyServiceWrapper objectifyServiceWrapper,
      SecureRandomSingleton random, HttpServletRequest request, HttpServletResponse response) {
    this.objectifyServiceWrapper = objectifyServiceWrapper;
    this.random = random;
    this.request = request;
    this.response = response;
    hashFunction = Hashing.md5();
  }

  @Override
  public Objectify ofy() {
    return objectifyServiceWrapper.ofy();
  }

  @Override
  public SessionInfoEntity getSessionInfo() {
    return retrieveOrCreateSessionInfo();
  }

  @Override
  public SessionInfoEntity getSessionInfoEntityGivenAdminPassword(String inputPassword) {
    SessionInfoEntity entity = retrieveOrCreateSessionInfo();

    String saltString = loadSalt();
    String adminPasswordString = loadAdminPassword(saltString);
    String hashedInputPassword = hashPassword(inputPassword, saltString);
    boolean admin = hashedInputPassword.equals(adminPasswordString);

    if (admin != entity.isAdmin()) {
      entity.setAdmin(admin);
      ofy().save().entity(entity);
    }

    return entity;
  }

  @Override
  public SessionInfoEntity attachUserInfoToSession(UserInfoEntity userInfoEntity) {
    assert(userInfoEntity.getId() != null);

    SessionInfoEntity entity = retrieveOrCreateSessionInfo();
    entity.setUserInfoEntity(userInfoEntity);
    ofy().save().entity(entity).now();

    return entity;
  }

  @Override
  public SessionInfoEntity signOut() {
    SessionInfoEntity entity = retrieveOrCreateSessionInfo();

    entity.clear();
    ofy().save().entity(entity);

    return entity;
  }

  @Override
  public SessionInfoEntity signOutAdmin() {
    SessionInfoEntity entity = retrieveOrCreateSessionInfo();

    if (entity.isAdmin()) {
      entity.setAdmin(false);
      ofy().save().entity(entity);
    }

    return entity;
  }

  @Override
  public void saveAdminPassword(String inputPassword) throws OperationNotAllowedException {
    SessionInfo sessionInfo = getSessionInfo();
    if (!sessionInfo.isAdmin() || inputPassword == null) {
      throw new OperationNotAllowedException();
    }
    GlobalStringEntity passwordEntity = new GlobalStringEntity(GlobalStringEntity.ADMIN_PASSWORD_ID,
        hashPassword(inputPassword, loadSalt()));
    ofy().save().entity(passwordEntity);
  }

  @Override
  public void saveAdminPasswordAndSalt(String inputPassword, String salt)
      throws OperationNotAllowedException{
    SessionInfo sessionInfo = getSessionInfo();
    if (!sessionInfo.isAdmin() || inputPassword == null || salt == null) {
      throw new OperationNotAllowedException();
    }

    final GlobalStringEntity saltEntity = new GlobalStringEntity(
        GlobalStringEntity.SALT_ID, salt);
    final GlobalStringEntity passwordEntity = new GlobalStringEntity(
        GlobalStringEntity.ADMIN_PASSWORD_ID, hashPassword(inputPassword, salt));
    ofy().transact(new VoidWork() {
      @Override
      public void vrun() {
        ofy().save().entity(saltEntity);
        ofy().save().entity(passwordEntity);
      }
    });
  }

  private Key<GlobalStringEntity> getAdminPasswordKey() {
    return Key.create(GlobalStringEntity.class, GlobalStringEntity.ADMIN_PASSWORD_ID);
  }

  private Key<GlobalStringEntity> getSaltKey() {
    return Key.create(GlobalStringEntity.class, GlobalStringEntity.SALT_ID);
  }

  private String loadAdminPassword(String saltString) {
    GlobalStringEntity adminPassword = ofy().load().key(getAdminPasswordKey()).get();
    return adminPassword != null ? adminPassword.getString() : hashPassword("", saltString);
  }

  private String loadSalt() {
    GlobalStringEntity salt = ofy().load().key(getSaltKey()).get();
    return salt != null ? salt.getString() : "";
  }

  private String hashPassword(String inputPassword, String saltString) {
    return hashFunction.hashString(saltString + inputPassword).toString();
  }

  private SessionInfoEntity retrieveOrCreateSessionInfo() {
    Cookie sessionCookie = getSessionCookie();
    if (sessionCookie != null) {
      SessionInfoEntity entity = ofy().load().type(SessionInfoEntity.class).id(
          sessionCookie.getValue()).get();

      if (entity != null) {
        // TODO(beaudoin): Check if the cookie has expired. If so, clear it.
        return entity;
      }
    }

    sessionCookie = new Cookie(Constants.sessionCookieName,
        new BigInteger(130, random).toString(32));
    sessionCookie.setMaxAge(MAX_SESSION_DURATION_S);
    sessionCookie.setPath("/");
    response.addCookie(sessionCookie);

    SessionInfoEntity entity = new SessionInfoEntity(sessionCookie.getValue());
    ofy().save().entity(entity);

    return entity;
  }

  private Cookie getSessionCookie() {
    if (request.getCookies() == null)
      return null;
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(Constants.sessionCookieName)) {
        return cookie;
      }
    }
    return null;
  }
}
