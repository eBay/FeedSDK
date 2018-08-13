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

package com.ebay.feed.model.error;

/**
 * <p>
 * Container for handling error information
 * </p>
 * 
 * @author shanganesh
 *
 */
public class ErrorData {

  private Integer errorId;
  private String domain;
  private String category;
  private String message;
  private String longMessage;

  public Integer getErrorId() {
    return errorId;
  }

  public void setErrorId(Integer errorId) {
    this.errorId = errorId;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLongMessage() {
    return longMessage;
  }

  public void setLongMessage(String longMessage) {
    this.longMessage = longMessage;
  }


}
