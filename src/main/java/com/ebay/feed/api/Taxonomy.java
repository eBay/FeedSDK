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

package com.ebay.feed.api;

import java.io.IOException;
import com.ebay.feed.model.feed.operation.taxonomy.CategoryResponse;


/**
 * <p>
 * Taxonomy interface which consists of the following capabilities
 * <ul>
 * <li>
 * <b>getCategoryTreeId</b> - Get the category tree id for the marketplace. For EBAY-US, it returns 0
 * </li>
 * <li>
 * <b>loadLeafCategories</b> - Creates a map between level two categories to leaf categories,
 * and level three categories to leaf categories. Useful when filtering on level two or level three
 * categories
 * </li>
 *  
 * </ul>
 * </p>
 * @author shanganesh
 *
 */
public interface Taxonomy {

  /**
   * <p>
   * Returns the category tree id for the marketplace
   * 
   * </p>
   * 
   * @param token OAUth token
   * @param marketplaceId A string representing the marketplace : EBAY-US
   * @return
   * @throws IOException
   */
  public String getCategoryTreeId(String token, String marketplaceId);

  /**
   * <p>
   * 
   * Loads the mapping between all level 'n' categories to leaf categories.
   * Currently supported values for 'n' are 2,3
   * 
   * </p>
   * 
   * @param token OAUth token
   * @param categoryTreeId The category tree id for the marketplace
   * @param categoryId - Level one category id
   * @return
   * @throws IOException
   */
  public CategoryResponse loadLeafCategories(String token, String categoryTreeId, String categoryId);

}
