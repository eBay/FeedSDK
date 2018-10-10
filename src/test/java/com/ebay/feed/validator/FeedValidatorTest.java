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

package com.ebay.feed.validator;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;

public class FeedValidatorTest {

  FeedValidator feedValidator = new FeedValidator();

  @Test
  public void isValidPathNullTest() {
    Assert.assertFalse(feedValidator.isValidPath(null));
  }

  @Test
  public void isValidPathBlankTrimTest() {
    Assert.assertFalse(feedValidator.isValidPath(""));
  }

  //@Test
  public void isValidPathBlankTest() {
    Assert.assertFalse(feedValidator.isValidPath("  "));
  }

  // TODO : this may fail if folder is not present
  @Test
  public void isValidPathTest() {
    Assert.assertTrue(feedValidator.isValidPath("/tmp"));
  }

  @Test
  public void isValidFilterRequestNullTest() {
    Assert.assertFalse(feedValidator.isValidFilterRequest(null));
  }

  @Test
  public void isValidFilterRequestEmptyTest() {
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    Assert.assertFalse(feedValidator.isValidFilterRequest(filterRequest));
  }

  @Test
  public void isValidFilterRequestInvalidInputPathTest() {
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    filterRequest.setInputFilePath("test");
    Assert.assertFalse(feedValidator.isValidFilterRequest(filterRequest));
  }

  @Test
  public void isValidFilterRequestInvalidLevelNTest() {
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    filterRequest.setInputFilePath("/tmp");

    Set<String> levelThreeCats = new HashSet<>();
    levelThreeCats.add("1");
    filterRequest.setLevelThreeCategories(levelThreeCats);

    Assert.assertFalse(feedValidator.isValidFilterRequest(filterRequest));
  }

  @Test
  public void isValidFilterRequestValidLevelNTest() {
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    filterRequest.setInputFilePath("/tmp");

    Set<String> levelThreeCats = new HashSet<>();
    levelThreeCats.add("1");
    filterRequest.setLevelThreeCategories(levelThreeCats);

    filterRequest.setToken(Constants.TOKEN_BEARER_PREFIX + "v1...");
    filterRequest.setMarketplace("EBAY-US");
    filterRequest.setLevelOneCategory("1");

    Assert.assertTrue(feedValidator.isValidFilterRequest(filterRequest));
  }
}
