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

import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.EnvTypeEnum;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Example showing how to download and filter feed files based on environment. The download location
 * is default - current working directory <br>
 * The filtering is performed on the unzipped file. <br>
 * So the sequence of events that are followed is :- <br>
 * - Download feed file <br>
 * - Unzip feed file <br>
 * - Filter feed file
 * </p>
 * 
 * @author shanganesh
 *
 */
public class FilterByEnv {


  // oauth token
  static String TOKEN = Constants.TOKEN_BEARER_PREFIX + "v^1.1#i^1#p^1#r^0#I...";

  // init feed
  static Feed feed = new FeedImpl();

  private static final String CATEGORY = "15032";

  // TODO : Check if the date is within 14 days, before making the call
  private static final String DATE = "20181005";
  private static final String SCOPE = "ALL_ACTIVE";
  private static final String MKT = "EBAY_US";
  
  public static void main(String[] args) {

    // create request
    FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
    builder.categoryId(CATEGORY).date(DATE).feedScope(SCOPE).siteId(MKT).token(TOKEN)
        .type(FeedTypeEnum.ITEM).env(EnvTypeEnum.SANDBOX.name());

    // using null for download directory - defaults to current working directory
    GetFeedResponse getFeedResponse = feed.get(builder.build(), null);

    // 0 denotes successful response
    if (getFeedResponse.getStatusCode() != 0) {
      System.out.println("Exception in downloading feed. Cannot proceed");
      return;
    }
    // unzip
    Response unzipOpResponse = feed.unzip(getFeedResponse.getFilePath());

    if (unzipOpResponse.getStatusCode() != 0) {
      System.out.println("Exception in unzipping feed. Cannot proceed");
      return;
    }
    
    // filter
    FeedFilterRequest filterRequest = new FeedFilterRequest();

    // set price lower and upper lomit
    filterRequest.setPriceLowerLimit(10.0);
    filterRequest.setPriceLowerLimit(20.0);
    // set input file
    filterRequest.setInputFilePath(unzipOpResponse.getFilePath());
    
    Response response = feed.filter(filterRequest);
    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());

  }

}
