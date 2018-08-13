/*
 * Copyright 2018 eBay Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ebay.feed.model.oauth;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author shanganesh
 *
 */
public class AuthRequest {

  private String configFilePath;
  private List<String> scopes;

  public String getConfigFilePath() {
    return configFilePath;
  }

  public void setConfigFilePath(String configFilePath) {
    this.configFilePath = configFilePath;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public AuthRequest(String configFilePath, List<String> scopes) {
    super();
    this.configFilePath = configFilePath;

    if (scopes == null) {
      List<String> defaultScopes = new ArrayList<>();
      defaultScopes.add("https://api.ebay.com/oauth/api_scope");
      defaultScopes.add("https://api.ebay.com/oauth/api_scope/buy.item.feed");
      this.scopes = defaultScopes;
    } else {
      this.scopes = scopes;
    }
  }
}
