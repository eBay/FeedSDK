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

package com.ebay.feed.constants;

public class Constants {

  /**
   * Base path for the feed api
   */
  public static final String FEED_API_BASE = "https://api.ebay.com/buy/feed/v1_beta/";
  
  // max content that can be downloaded in one request, in bytes
  public static final Long CHUNK_SIZE = 104857600L;
  
  // timeout for http client
  public static final Integer TIMEOUT = 30;

  // api related constants
  public static final String AUTHORIZATION_HEADER = "Authorization";
  
  // taxonomy
  public static final String TAXONOMY_DEFAULT =
      "https://api.ebay.com/commerce/taxonomy/v1_beta/get_default_category_tree_id?marketplace_id=";
  
  public static final String TAXONOMY_CATEGORY_SUBTREE_BASE = "https://api.ebay.com/commerce/taxonomy/v1_beta/category_tree/";
  public static final String TAXONOMY_CATEGORY_SUBTREE_QUERY = "/get_category_subtree?category_id=";
  
  // status and util related constants
  public static final String SUCCESS = "Success";
  public static final Integer SUCCESS_CODE = 0;
  public static final String FAILURE = "Failure";
  public static final Integer FAILURE_CODE = -1;
  public static final String RANGE_PREFIX = "bytes=0-";
  public static final String RANGE_HEADER = "Range";
  public static final String CONTENT_RANGE_HEADER = "Content-Range";
  public static final String LAST_MODIFIED_DATE_HEADER = "Last-Modified";
  public static final String TOKEN_BEARER_PREFIX = "Bearer ";
  public static final String TRUE = "true";
  public static final String FALSE = "false";
  public static final String ITEM_RESOURCE_SCOPE = "item?feed_scope=";
  public static final String QUERY_PARAM_SEPARATOR = "&";
  public static final String QUERY_CATEGORY_ID = "category_id=";
  public static final String QUERY_DATE = "date=";
  public static final String MARKETPLACE_HEADER = "X-EBAY-C-MARKETPLACE-ID";
  public static final String CONTENT_TYPE_HEADER = "Content-type";
  public static final String ACCEPT_HEADER = "Accept";

  public static final String ALL_ACTIVE = "ALL_ACTIVE";
  
  public static final String BOOTSTRAP = "bootstrap";
  public static final String DAILY = "daily";
  public static final String SEPARATOR = "\\t";
  public static final String ITEM_ID = "ItemId";
  
}
