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

package com.ebay.feed.model.feed.download;

import java.util.List;
import com.ebay.feed.model.error.ErrorData;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <div>
 * Container for capturing method output. <br>
 * <b>statusCode</b>
 * <ul>
 * <li>0 - Success</li>
 * <li>-1 - Failure</li>
 * <li>null - Failure</li>
 * </ul>
 * <br>
 * <b>message</b>
 * <ul>
 * <li>
 * null - Failure non null value - Description of the operation result</li>
 * </ul>
 * <br>
 * <b>filePath</b>
 * <ul>
 *     <li>
 * null - Failure non null value - Path of the file that was created as a result of this operation
 * </li>
 * </ul>
 * <br>
 * <b>appliedFilters</b> - List of all the filters that were applied
 * <br>
 * <b>errors</b> - List of errors returned by the API
 * </div>
 * 
 * @author shanganesh
 *
 */
public class GetFeedResponse extends Response {

  public GetFeedResponse(Integer statusCode, String message, String filePath, List<ErrorData> errors) {
    super(statusCode, message, filePath, null);
    this.errors = errors;
  }

  private List<ErrorData> errors;

  public List<ErrorData> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorData> errors) {
    this.errors = errors;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("FeedApiResponse [errors=").append(errors).append("]");
    return builder.toString();
  }

}
