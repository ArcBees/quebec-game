package com.philbeaudoin.quebec.shared.user;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInfoDto implements UserInfo, IsSerializable {

  private String googleId;

  public UserInfoDto(UserInfo userInfo) {
    if (userInfo != null) {
      googleId = userInfo.getGoogleId();
    }
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private UserInfoDto() {
  }

  @Override
  public String getGoogleId() {
    return googleId;
  }
}
