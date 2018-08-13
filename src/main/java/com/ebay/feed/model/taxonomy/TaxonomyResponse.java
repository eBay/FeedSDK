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

package com.ebay.feed.model.taxonomy;

/**
 * 
 * @author shanganesh
 *
 */
public class TaxonomyResponse {
  private CategorySubtreeNode categorySubtreeNode;

  private String categoryTreeVersion;

  private String categoryTreeId;

  public CategorySubtreeNode getCategorySubtreeNode() {
    return categorySubtreeNode;
  }

  public void setCategorySubtreeNode(CategorySubtreeNode categorySubtreeNode) {
    this.categorySubtreeNode = categorySubtreeNode;
  }

  public String getCategoryTreeVersion() {
    return categoryTreeVersion;
  }

  public void setCategoryTreeVersion(String categoryTreeVersion) {
    this.categoryTreeVersion = categoryTreeVersion;
  }

  public String getCategoryTreeId() {
    return categoryTreeId;
  }

  public void setCategoryTreeId(String categoryTreeId) {
    this.categoryTreeId = categoryTreeId;
  }

  @Override
  public String toString() {
    return "ClassPojo [categorySubtreeNode = " + categorySubtreeNode + ", categoryTreeVersion = "
        + categoryTreeVersion + ", categoryTreeId = " + categoryTreeId + "]";
  }

}
