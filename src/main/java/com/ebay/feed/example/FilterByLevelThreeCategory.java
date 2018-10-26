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
import com.ebay.feed.api.Taxonomy;
import com.ebay.feed.api.TaxonomyImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Example showing how to download and filter feed files based on L3 category ids. The download
 * location is default - current working directory <br>
 * 
 * To filter on L3 category, the mapping between L3 to leaf category ids needs to be loaded. Once
 * the mapping is loaded, the operation translates to actually filtering the feed files on leaf
 * category ids, which belong to the L3 category id.
 * 
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
public class FilterByLevelThreeCategory {

  // oauth token : Bearer xxx
  /**
   * For filtering on level two or level three categories, the token scope should include
   * feed_scope and api_scope.
   * 
   * Filtering on level two and level three categories requires an additional call 
   * to taxonomy api
   */
  static String TOKEN =
      Constants.TOKEN_BEARER_PREFIX
          + "v^1.1#i^1#r^0#I^3...";

  // init feed
  static Feed feed = new FeedImpl();

  private static final String CATEGORY = "1";

  // TODO : Check if the date is within 14 days, before making the call
  private static final String DATE = "20180708";
  private static final String SCOPE = "ALL_ACTIVE";
  private static final String MKT = "EBAY_US";

  // init taxonomy
  static Taxonomy taxonomy = new TaxonomyImpl();

  public static void main(String[] args) {

    // create request
    FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
    builder.categoryId(CATEGORY).date(DATE).feedScope(SCOPE).siteId(MKT).token(TOKEN)
        .type(FeedTypeEnum.ITEM);

    // using null for download directory - defaults to current working directory
    GetFeedResponse getFeedResponse = feed.get(builder.build(), null);

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

    // set input file
    filterRequest.setToken(TOKEN);
    filterRequest.setMarketplace(MKT);

    // level three
    filterRequest.setLevelThreeCategories(getLevelThreeCats());

    // level two
    filterRequest.setLevelTwoCategories(getLevelTwoCats());
    filterRequest.setLevelOneCategory(CATEGORY);

    filterRequest.setInputFilePath(unzipOpResponse.getFilePath());

    Response response = feed.filter(filterRequest);

    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());

  }

  static Set<String> getLevelThreeCats() {
    Set<String> levelThreeSet = new HashSet<>();
    levelThreeSet.add("1313");
    levelThreeSet.add("13583");
    return levelThreeSet;
  }

  static Set<String> getLevelTwoCats() {
    Set<String> levelThreeSet = new HashSet<>();
    levelThreeSet.add("34");
    return levelThreeSet;
  }
}
