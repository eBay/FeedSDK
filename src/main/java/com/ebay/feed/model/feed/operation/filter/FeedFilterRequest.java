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

package com.ebay.feed.model.feed.operation.filter;

import java.util.HashSet;
import java.util.Set;
import com.ebay.feed.constants.Constants;

/**
 * <div>
 * Container for available filters for feed files<br>
 * <ul>
 * <li><b>levelOneCategory</b> - The level one category of the feed file, on which the filter is
 * being applied</li>
 * <li><b>marketplace</b> - The marketplace id for the feed file</li>
 * <li><b>token</b> - OAuth token</li>
 * <li></li>
 * <li><b>leafCategoryIds</b> - Set of leaf category ids</li>
 * <li></li>
 * <li><b>sellerNames</b> - Set of seller names</li>
 * <li></li>
 * <li><b>itemLocationCountries</b> - Set of item location countries in ISO 3166-1 alpha-2 format</li>
 *
 * <li></li>
 * <li><b>priceLowerLimit</b> - Lower limit of price range</li>
 * <li><b>priceUpperLimit</b> - upper limit of price range</li>
 * <li><b>levelThreeCategories</b> - Set of level three categories for filtering</li>
 * <li><b>levelTwoCategories</b> - Set of level two categories for filtering</li>
 * <li><b>epids</b> - Set of epids for filtering</li>
 * <li><b>inferredEpids</b> - Set of inferred epids for filtering</li>
 * <li><b>gtins</b> - Set of gtins for filtering</li>
 * <li><b>itemIds</b> - Set of item ids for filtering</li>
 * </ul>
 * </div>
 * 
 * @author shanganesh
 *
 */
public class FeedFilterRequest {

  /**
   * This is needed to calculate the leaf categories for level two and level three This is not used
   * to filter
   */
  private String levelOneCategory;
  private String marketplace;
  private String token;

  private Set<String> leafCategoryIds = new HashSet<>();
  private Set<String> sellerNames;
  private Set<String> itemLocationCountries;
  private Double priceLowerLimit;
  private Double priceUpperLimit;

  private Set<String> levelThreeCategories;
  private Set<String> levelTwoCategories;
  private Set<String> epids;
  private Set<String> inferredEpids;
  private Set<String> gtins;
  private Set<String> itemIds;

  // file on which to the filters are applied
  private String inputFilePath;

  public Set<String> getLeafCategoryIds() {
    return leafCategoryIds;
  }

  public void setLeafCategoryIds(Set<String> leafCategoryIds) {
    this.leafCategoryIds = leafCategoryIds;
  }

  public Set<String> getSellerNames() {
    return sellerNames;
  }

  public void setSellerNames(Set<String> sellerNames) {
    this.sellerNames = sellerNames;
  }

  public Set<String> getItemLocationCountries() {
    return itemLocationCountries;
  }

  public void setItemLocationCountries(Set<String> itemLocationCountries) {
    this.itemLocationCountries = itemLocationCountries;
  }

  public Double getPriceLowerLimit() {
    return priceLowerLimit;
  }

  public void setPriceLowerLimit(Double priceLowerLimit) {
    this.priceLowerLimit = priceLowerLimit;
  }

  public Double getPriceUpperLimit() {
    return priceUpperLimit;
  }

  public void setPriceUpperLimit(Double priceUpperLimit) {
    this.priceUpperLimit = priceUpperLimit;
  }

  public Set<String> getLevelThreeCategories() {
    return levelThreeCategories;
  }

  public void setLevelThreeCategories(Set<String> levelThreeCategories) {
    this.levelThreeCategories = levelThreeCategories;
  }

  public Set<String> getLevelTwoCategories() {
    return levelTwoCategories;
  }

  public void setLevelTwoCategories(Set<String> levelTwoCategories) {
    this.levelTwoCategories = levelTwoCategories;
  }

  public Set<String> getEpids() {
    return epids;
  }

  public void setEpids(Set<String> epids) {
    this.epids = epids;
  }

  public Set<String> getInferredEpids() {
    return inferredEpids;
  }

  public void setInferredEpids(Set<String> inferredEpids) {
    this.inferredEpids = inferredEpids;
  }

  public Set<String> getGtins() {
    return gtins;
  }

  public void setGtins(Set<String> gtins) {
    this.gtins = gtins;
  }

  public Set<String> getItemIds() {
    return itemIds;
  }

  public void setItemIds(Set<String> itemIds) {
    this.itemIds = itemIds;
  }

  public String getInputFilePath() {
    return inputFilePath;
  }

  public void setInputFilePath(String inputFilePath) {
    this.inputFilePath = inputFilePath;
  }

  /**
   * Check if this instance is empty
   * 
   * @return Returns true if the instance is empty
   */
  public boolean isEmpty() {
    boolean isEmpty = false;

    if ((leafCategoryIds == null || leafCategoryIds.isEmpty())
        && (sellerNames == null || sellerNames.isEmpty())
        && (itemLocationCountries == null || itemLocationCountries.isEmpty())
        && (priceLowerLimit == null) && (priceUpperLimit == null)
        && (itemIds == null || itemIds.isEmpty()) && (gtins == null || gtins.isEmpty())
        && (inferredEpids == null || inferredEpids.isEmpty()) && (epids == null || epids.isEmpty())
        && (levelTwoCategories == null || levelTwoCategories.isEmpty())
        && (levelThreeCategories == null || levelThreeCategories.isEmpty())) {
      isEmpty = true;
    }
    return isEmpty;
  }

  public String getLevelOneCategory() {
    return levelOneCategory;
  }

  public void setLevelOneCategory(String levelOneCategory) {
    this.levelOneCategory = levelOneCategory;
  }

  public String getMarketplace() {
    return marketplace;
  }

  public void setMarketplace(String marketplace) {
    this.marketplace = marketplace;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {

    if (token != null && !token.startsWith(Constants.TOKEN_BEARER_PREFIX))
      token = Constants.TOKEN_BEARER_PREFIX + token;

    this.token = token;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FeedFilterRequest [levelOneCategory=").append(levelOneCategory)
        .append(", marketplace=").append(marketplace).append(", leafCategoryIds=")
        .append(leafCategoryIds).append(", sellerNames=").append(sellerNames)
        .append(", itemLocationCountries=").append(itemLocationCountries)
        .append(", priceLowerLimit=").append(priceLowerLimit).append(", priceUpperLimit=")
        .append(priceUpperLimit).append(", levelThreeCategories=").append(levelThreeCategories)
        .append(", levelTwoCategories=").append(levelTwoCategories).append(", epids=")
        .append(epids).append(", inferredEpids=").append(inferredEpids).append(", gtins=")
        .append(gtins).append(", itemIds=").append(itemIds).append(", inputFilePath=")
        .append(inputFilePath).append("]");
    return builder.toString();
  }
}
