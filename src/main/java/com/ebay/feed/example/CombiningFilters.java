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

import java.util.HashSet;
import java.util.Set;
import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Example showing how to download and filter feed files based on a combination of filter criteria
 * The download location is default - current working directory <br>
 * This example downloads the bootstrap feed file for L1 category : 1 (Collectibles) and filters on
 * :-<br>
 * - leaf categories <br>
 * - price <br>
 * - item location <br>
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
public class CombiningFilters {

  // oauth token with Bearer prefix
  static String token = Constants.TOKEN_BEARER_PREFIX + "v^1.1#i^1#f^0#I^3....";

  // init feed
  static Feed feed = new FeedImpl();

  private static final String CATEGORY = "1";
  
  // TODO : Check if the date is within 14 days, before making the call
  private static final String DATE = "20180805";
  private static final String SCOPE = "ALL_ACTIVE";
  private static final String MKT = "EBAY_US";
  
  public static void main(String[] args) {

    // create request
    FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
    builder.categoryId(CATEGORY).date(DATE).feedScope(SCOPE).siteId(MKT).token(token)
        .type(FeedTypeEnum.ITEM);

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

    // filter for leaf category ids
    filterRequest.setLeafCategoryIds(getLeafCategorySet());

    // filter for price
    filterRequest.setPriceLowerLimit(10.0);
    filterRequest.setPriceUpperLimit(50.0);

    // filter for item location
    filterRequest.setItemLocationCountries(getItemLocationSet());

    // set input file
    filterRequest.setInputFilePath(unzipOpResponse.getFilePath());
    
    Response response = feed.filter(filterRequest);

    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());

  }

  /**
   * Get the set of leaf category ids to filter on
   * 
   * @return
   */
  private static Set<String> getLeafCategorySet() {
    Set<String> leafCategorySet = new HashSet<>();
    leafCategorySet.add("11675");
    leafCategorySet.add("3226");
    return leafCategorySet;
  }

  /**
   * Get the set of item locations to filter on
   * 
   * @return
   */
  private static Set<String> getItemLocationSet() {
    Set<String> itemLocationSet = new HashSet<>();
    itemLocationSet.add("CN");
    return itemLocationSet;
  }

}
