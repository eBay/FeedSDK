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

package com.ebay.feed.model.feed.operation.taxonomy;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Consists of mapping between level two/three categories to leaf categories
 * </p>
 * @author shanganesh
 *
 */
public class CategoryResponse {

  private Map<String, Set<String>> levelThreeCategoryLeaves;
  private Map<String, Set<String>> levelTwoCategoryLeaves;

  public Map<String, Set<String>> getLevelThreeCategoryLeaves() {
    return levelThreeCategoryLeaves;
  }

  public void setLevelThreeCategoryLeaves(Map<String, Set<String>> levelThreeCategoryLeaves) {
    this.levelThreeCategoryLeaves = levelThreeCategoryLeaves;
  }

  public Map<String, Set<String>> getLevelTwoCategoryLeaves() {
    return levelTwoCategoryLeaves;
  }

  public void setLevelTwoCategoryLeaves(Map<String, Set<String>> levelTwoCategoryLeaves) {
    this.levelTwoCategoryLeaves = levelTwoCategoryLeaves;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CategoryResponse [levelThreeCategoryLeaves=").append(levelThreeCategoryLeaves)
        .append(", levelTwoCategoryLeaves=").append(levelTwoCategoryLeaves).append("]");
    return builder.toString();
  }
}
