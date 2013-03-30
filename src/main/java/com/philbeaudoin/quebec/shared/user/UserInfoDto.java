package com.philbeaudoin.quebec.shared.user;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInfoDto implements UserInfo, IsSerializable {

  private long id;
  private String googleId;
  private String email;
  private String name;

  public UserInfoDto(UserInfo userInfo) {
    // UserInfo must not be null. Check before calling this.
    this(userInfo.getId(), userInfo.getGoogleId(), userInfo.getEmail(), userInfo.getName());
  }

  public UserInfoDto(long id, String googleId, String email, String name) {
    this.id = id;
    this.googleId = googleId;
    this.email = email;
    this.name = name;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private UserInfoDto() {
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public String getGoogleId() {
    return googleId;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public String getName() {
    return name;
  }
}
