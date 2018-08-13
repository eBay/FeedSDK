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


/**
 * <p>
 * Container for capturing method output. <br/>
 * <b>statusCode</b>
 * <ul>
 * <li>0 - Success</li>
 * <li>-1 - Failure</li>
 * <li>null - Failure</li>
 * </ul>
 * 
 * <br/>
 * <b>message</b>
 * <ul>
 * <li>
 * null - Failure non null value - Description of the operation result</li>
 * </ul>
 * 
 * <br/>
 * <b>filePath</b>
 * <ul>
 * null - Failure non null value - Path of the file that was created as a result of this operation
 * </ul>
 * 
 * <br/>
 * <b>appliedFilters</b> - List of all the filters that were applied
 * 
 * </p>
 * 
 * @author shanganesh
 *
 */
public class Response {

  private FeedFilterRequest appliedFilters;
  private Integer statusCode;
  private String message;
  private String filePath;

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public FeedFilterRequest getAppliedFilters() {
    return appliedFilters;
  }

  public void setAppliedFilters(FeedFilterRequest appliedFilters) {
    this.appliedFilters = appliedFilters;
  }

  /**
   * 
   * @param statusCode 0 success
   * @param message
   * @param filePath
   * @param appliedFilters
   */
  public Response(Integer statusCode, String message, String filePath,
      FeedFilterRequest appliedFilters) {
    super();
    this.appliedFilters = appliedFilters;
    this.statusCode = statusCode;
    this.message = message;
    this.filePath = filePath;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Response [appliedFilters=").append(appliedFilters).append(", statusCode=")
        .append(statusCode).append(", message=").append(message).append(", filePath=")
        .append(filePath).append("]");
    return builder.toString();
  }
}
