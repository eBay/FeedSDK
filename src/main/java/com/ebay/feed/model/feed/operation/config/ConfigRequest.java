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

import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;

/**
 * <p>
 * Container for capturing feed and filter requests
 * </p>
 * @author shanganesh
 *
 */
public class ConfigRequest {

  private FeedRequest feedRequest;
  private FeedFilterRequest filterRequest;

  public FeedRequest getFeedRequest() {
    return feedRequest;
  }

  public void setFeedRequest(FeedRequest feedRequest) {
    this.feedRequest = feedRequest;
  }

  public FeedFilterRequest getFilterRequest() {
    return filterRequest;
  }

  public void setFilterRequest(FeedFilterRequest filterRequest) {
    this.filterRequest = filterRequest;
  }

}
