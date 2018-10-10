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


package com.ebay.feed.model.feed.operation.feed;

import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;

/**
 * <div>
  Request parameters for invoking feed API <br>
  <ul>
  <li><b>categoryId</b> - The level one category id of the feed file</li>
  <li><b>marketplaceId</b> - The marketplace id for the feed file Eg EBAY-US</li>
  <li><b>date</b> - The date parameter for the feed file. Optional for bootstrap feed files</li>
  <li><b>feedScope</b> - Scope of the feed file - ALL_ACTIVE or NEWLY_LISTED</li>
  <li><b>token</b> - OAuth token</li>
  <li><b>type</b> - Currently supported - ITEM</li>
  </ul>
  </div>
 * @author shanganesh
 *
 */
public class FeedRequest {

  private String categoryId;
  private String marketplaceId;
  private String date;
  private String snapshotDate;
  private String feedScope;
  private String token;
  private FeedTypeEnum type;
  private String env;

  private FeedRequest(FeedRequestBuilder builder) {
    this.categoryId = builder.categoryId;
    this.marketplaceId = builder.marketplaceId;
    this.date = builder.date;
    this.feedScope = builder.feedScope;
    this.token = builder.token;
    this.snapshotDate = builder.snapshotDate;
    this.type = builder.type;
    this.env = builder.env;
  }


  public String getCategoryId() {
    return categoryId;
  }


  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }


  public String getMarketplaceId() {
    return marketplaceId;
  }


  public void setMarketplaceId(String marketplaceId) {
    this.marketplaceId = marketplaceId;
  }


  public String getDate() {
    return date;
  }


  public void setDate(String date) {
    this.date = date;
  }


  public String getSnapshotDate() {
    return snapshotDate;
  }


  public void setSnapshotDate(String snapshotDate) {
    this.snapshotDate = snapshotDate;
  }


  public String getFeedScope() {
    return feedScope;
  }


  public void setFeedScope(String feedScope) {
    this.feedScope = feedScope;
  }


  public String getToken() {
    return token;
  }


  public void setToken(String token) {
    
    if(token != null && !token.startsWith(Constants.TOKEN_BEARER_PREFIX))
      token = Constants.TOKEN_BEARER_PREFIX + token;
    
    this.token = token;
  }


  public FeedTypeEnum getType() {
    return type;
  }


  public void setType(FeedTypeEnum type) {
    this.type = type;
  }

  public String getEnv() {
	 return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }
	  
  public static class FeedRequestBuilder {
    private String categoryId;
    private String marketplaceId;
    private String date;
    private String feedScope;
    private String token;
    private String snapshotDate;
    private FeedTypeEnum type;
    private String env;
    
    public FeedRequestBuilder categoryId(final String categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public FeedRequestBuilder siteId(final String marketplaceId) {
      this.marketplaceId = marketplaceId;
      return this;
    }

    public FeedRequestBuilder date(final String date) {
      this.date = date;
      return this;
    }

    public FeedRequestBuilder feedScope(final String feedScope) {
      this.feedScope = feedScope;
      return this;
    }

    public FeedRequestBuilder token(final String token) {
      this.token = token;
      
      if(token != null && !token.startsWith(Constants.TOKEN_BEARER_PREFIX))
        this.token = Constants.TOKEN_BEARER_PREFIX + token;
      
      return this;
    }

    public FeedRequestBuilder snapshotDate(final String snapshotDate) {
      this.snapshotDate = snapshotDate;
      return this;
    }

    public FeedRequestBuilder type(final FeedTypeEnum type) {
      this.type = type;
      return this;
    }
    
    public FeedRequestBuilder env(final String env) {
        this.env = env;
        return this;
    }

    public FeedRequest build() {
      return new FeedRequest(this);
    }
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FeedRequest [categoryId=").append(categoryId).append(", marketplaceId=")
        .append(marketplaceId).append(", date=").append(date).append(", snapshotDate=")
        .append(snapshotDate).append(", feedScope=").append(feedScope).append(", token=")
        .append(token).append(", type=").append(type).append(", env=").append(env).append("]");
    return builder.toString();
  }
}
