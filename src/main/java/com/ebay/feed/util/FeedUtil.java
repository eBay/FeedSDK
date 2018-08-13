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

package com.ebay.feed.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;

/**
 * <p>
 * Utils class for performing utility functions for feed impl
 * </p>
 * 
 * @author shanganesh
 *
 */
public class FeedUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeedUtil.class);


  /**
   * <p>
   * Generate URL based on the feed request values
   * </p>
   * 
   * @param feedRequest
   * @return
   */
  protected String getFinalUrl(FeedRequest feedRequest) {

    String finalUrl = null;
    FeedTypeEnum type = feedRequest.getType();
    StringBuilder bdr = new StringBuilder(Constants.FEED_API_BASE);
    switch (type) {

      case ITEM:

        bdr.append(Constants.ITEM_RESOURCE_SCOPE);
        bdr.append(feedRequest.getFeedScope());
        bdr.append(Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_CATEGORY_ID);
        bdr.append(feedRequest.getCategoryId());
        bdr.append(Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_DATE);
        bdr.append(feedRequest.getDate());
        finalUrl = bdr.toString();
        break;

      default:
        break;
    }

    LOGGER.debug("Final URL for API = {}", finalUrl);
    return finalUrl;
  }

  /**
   * <p>
   * Generate request instance for the feed API call
   * </p>
   * 
   * @param feedRequest
   * @param requestBuilder
   * @return
   */
  public Request.Builder generateRequest(FeedRequest feedRequest, Request.Builder requestBuilder) {
    // static headers
    requestBuilder.addHeader(Constants.MARKETPLACE_HEADER, feedRequest.getMarketplaceId());
    requestBuilder.addHeader(Constants.CONTENT_TYPE_HEADER, "application/json");
    requestBuilder.addHeader(Constants.ACCEPT_HEADER, "application/json");

    // url
    requestBuilder.url(getFinalUrl(feedRequest));

    // token
    requestBuilder.addHeader(Constants.AUTHORIZATION_HEADER, feedRequest.getToken());
    return requestBuilder;
  }

  /**
   * <p>
   * Generate file name for the downloaded file
   * </p>
   * 
   * @return
   */
  public String generateFileName(FeedRequest feedRequest) {

    String fileType =
        Constants.ALL_ACTIVE.equalsIgnoreCase(feedRequest.getFeedScope()) ? Constants.BOOTSTRAP
            : Constants.DAILY;

    switch (feedRequest.getType()) {
      case ITEM:
        return "item_" + fileType + "-" + feedRequest.getCategoryId() + "-" + feedRequest.getDate()
            + "-" + feedRequest.getMarketplaceId() + ".gz";
      default:
        return null;
    }
  }

  /**
   * <p>
   * Unzips a file and creates a new file and returns the path Returns null in case of errors
   * </p>
   * 
   * @param path
   * @param keepOriginal
   * @return
   * @throws InvalidExitValueException
   * @throws IOException
   * @throws InterruptedException
   * @throws TimeoutException
   */
  public String unzip(Path path) throws IOException,
      InterruptedException, TimeoutException {

    if (path == null)
      return null;

    byte[] buffer = new byte[1024];

    try {

      LOGGER.debug("Starting unzipping operation for = {}", path.toString());
      String newFilePath = (path.toString().substring(0, path.toString().length() - 3));

      GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(path.toFile()));

      FileOutputStream out = new FileOutputStream(newFilePath);

      int len;
      while ((len = gzis.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }

      gzis.close();
      out.close();

      LOGGER.debug("Completed unzipping operation. Unzipped file = {}", newFilePath);
      return newFilePath;

    } catch (IOException ex) {
      LOGGER.debug("Exception in unzip()", ex);
      ex.printStackTrace();
    }
    return null;
  }
}
