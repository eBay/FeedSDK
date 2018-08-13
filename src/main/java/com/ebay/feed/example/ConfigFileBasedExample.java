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

package com.ebay.feed.example;

import java.util.List;
import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.auth.CredentialLoader;
import com.ebay.feed.model.feed.operation.filter.Response;
import com.ebay.feed.model.oauth.AuthRequest;

public class ConfigFileBasedExample {

  // credentials file absolute path
  static String credentialFilePath = "credentials.yaml";

  // init feed
  static Feed feed = new FeedImpl();

  public static void main(String[] args) throws Exception {

    // null scopes result in default values being used
    AuthRequest authRequest = new AuthRequest(credentialFilePath, null);
    
    // load credentials and generate token
    CredentialLoader credentialLoader = new CredentialLoader(authRequest);
    credentialLoader.loadCredentials();
    String token = credentialLoader.getOauthResponse().getAccessToken().get().getToken();
    
    // expects path to the config file. The config file should be a json with the 
    // structure mirroring the pojo ConfigFileBasedRequest.java
    String configFilePath = "sample-config/config-file-download-unzip-filter";
    List<Response> responses = feed.processConfigFile(configFilePath, token);

    for (Response response : responses) {
      System.out.println(response.toString());
    }
  }

}
