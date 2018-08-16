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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;

/**
 * <p>
 * Class responsible for validating feed and filter requests
 * </p>
 * 
 * @author shanganesh
 *
 */
public class FeedValidator {

  /**
   * Check path validity
   * 
   * @param filePath Checks if the file exists on the specified path
   * @return boolean Indicates whether the file was found on the specified path
   */
  public boolean isValidPath(String filePath) {

    if (filePath == null || filePath.isEmpty())
      return false;

    Path path = Paths.get(filePath);

    return Files.exists(path);
  }

  /**
   * <p>
   * Check feed filter request
   * </p>
   * 
   * @param filterRequest Container for capturing parameters related to filtering the feed files
   * @return boolean Indicates whether all the mandatory parameters are present
   */
  public boolean isValidFilterRequest(FeedFilterRequest filterRequest) {

    if (filterRequest == null)
      return false;

    if (filterRequest.getInputFilePath() == null || filterRequest.getInputFilePath().isEmpty())
      return false;

    Path path = Paths.get(filterRequest.getInputFilePath());

    if (!Files.exists(path))
      return false;

    if (filterRequest.isEmpty())
      return false;

    if (!isValidLevelNRequest(filterRequest))
      return false;

    return true;
  }


  /**
   * Check that the mandatory parameters are present, in case filtering on level 'n' is requested
   * 
   * @return
   */
  private boolean isValidLevelNRequest(FeedFilterRequest filterRequest) {

    boolean isValid = true;
    // check if level 'n' categories are present.
    if ((filterRequest.getLevelTwoCategories() != null && !filterRequest.getLevelTwoCategories()
        .isEmpty())
        || (filterRequest.getLevelThreeCategories() != null && !filterRequest
            .getLevelThreeCategories().isEmpty())) {

      if (filterRequest.getToken() == null || filterRequest.getMarketplace() == null
          || filterRequest.getLevelOneCategory() == null)
        isValid = false;

    }
    return isValid;
  }


}
