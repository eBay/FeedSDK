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
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Simple example of applying a filter on an already downlaoded file 
 * <br>
 * The filtering is performed on the unzipped file. <br>
 * - Filter feed file
 * </p>
 * 
 * @author shanganesh
 *
 */
public class FilterByGtins {

  // oauth token
  static String token = Constants.TOKEN_BEARER_PREFIX + "v^1.1#i^1#f^0#I...";

  // init feed
  static Feed feed = new FeedImpl();
  
  public static void main(String[] args) {

    // filter
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    filterRequest.setGtins(getGtins());
    // set input file
    // absolute path to the unzipped file
    filterRequest
        .setInputFilePath("item_bootstrap-1281-20180708-EBAY_US");

    Response response = feed.filter(filterRequest);
    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());

  }

  /**
   * Get the set of seller user names to filter on
   * 
   * @return
   */
  private static Set<String> getGtins() {
    Set<String> gtinSet = new HashSet<>();
    gtinSet.add("647369501375");
    return gtinSet;
  }
}
