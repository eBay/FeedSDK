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

package com.ebay.feed.model.feed.operation.internal;

/**
 * <p>
 * Used internally for tracking iterative calls for large files
 * </p>
 * 
 * @author shanganesh
 *
 */
public class InvokeResponse {

  private String contentRange;
  private int statusCode;

  public InvokeResponse(String contentRange, int statusCode) {
    super();
    this.contentRange = contentRange;
    this.setStatusCode(statusCode);
  }

  public String getContentRange() {
    return contentRange;
  }

  public void setContentRange(String contentRange) {
    this.contentRange = contentRange;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("InvokeResponse [contentRange=").append(contentRange).append(", statusCode=")
        .append(statusCode).append("]");
    return builder.toString();
  }
}
