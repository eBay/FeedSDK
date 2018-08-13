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

package com.ebay.feed.model.feed.operation.config;

import java.util.List;

/**
 * <p>
 * For using the SDK using the config file based approach,
 * create a json file, with a structure that mirrors this class.
 * 
 * </p>
 * @author shanganesh
 *
 */
public class ConfigFileBasedRequest {

  private List<ConfigRequest> requests;

  public List<ConfigRequest> getRequests() {
    return requests;
  }

  public void setRequests(List<ConfigRequest> requests) {
    this.requests = requests;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ConfigFileBasedRequest [requests=").append(requests).append("]");
    return builder.toString();
  }
}
