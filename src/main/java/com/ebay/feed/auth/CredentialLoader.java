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
package com.ebay.feed.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ebay.api.client.auth.oauth2.CredentialUtil;
import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.feed.model.oauth.AuthRequest;

/**
 * <p>
 * Responsible for loading consumer credentials and generating token.
 *
 * In order to generate token, the {@link #loadCredentials() loadCredentials}
 * method needs to be invoked, which loads the credentials from the provided
 * config file.
 *
 * Once the credentials are loaded, the token can be obtained by invoking the
 * {@link #getOauthResponse() getOauthResponse}
 *
 * </p>
 *
 * @author shanganesh
 *
 */
public class CredentialLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialLoader.class);

    private AuthRequest authRequest;

    public CredentialLoader(AuthRequest authRequest) {
        this.authRequest = authRequest;

    }

    /**
     * <p>
     * Loads the credentials provided in the config file
     * </p>
     *
     * @throws Exception Thrown if file is not found or credentials are invalid
     */
    public void loadCredentials() throws Exception {

        if (authRequest == null) {
            throw new Exception("null AuthRequest");
        }

        LOGGER.debug("****** Begin loadCredentials with configPath = {}",
                authRequest.getConfigFilePath());
        try {

            File f = new File(authRequest.getConfigFilePath());
            FileInputStream fs = new FileInputStream(f);
            CredentialUtil.load(fs);

        } catch (Exception e) {
            LOGGER.debug("Exception in loadCredentials", e);
            throw new Exception("Could not load credentials");
        }
    }

    /**
     * <p>
     * Generates oauth token, based on the loaded credentials
     * </p>
     *
     * @return Returns the OAuthResponse which contains the token
     * @throws IOException Thrown if file is not found
     */
    public OAuthResponse getOauthResponse() throws IOException {

        LOGGER.debug("****** Begin getOauthResponse with configPath = {}",
                authRequest.getConfigFilePath());
        OAuth2Api api = new OAuth2Api();
        OAuthResponse res = api.getApplicationToken(Environment.PRODUCTION, authRequest.getScopes());
        return res;

    }

    /**
     * <p>
     * Generates oauth token, based on the loaded credentials
     * </p>
     *
     * @param environment
     * @return Returns the OAuthResponse which contains the token
     * @throws IOException Thrown if file is not found
     */
    public OAuthResponse getOauthResponse(String environment) throws IOException {

        LOGGER.debug("****** Begin getOauthResponse with configPath = {}",
                authRequest.getConfigFilePath());
        OAuth2Api api = new OAuth2Api();
        OAuthResponse res = null;
        //checking environment for token 
        if (environment.equalsIgnoreCase("SANDBOX")) {
            res = api.getApplicationToken(Environment.SANDBOX, authRequest.getScopes());
        } else {
            res = api.getApplicationToken(Environment.PRODUCTION, authRequest.getScopes());
        }
        return res;

    }
}
