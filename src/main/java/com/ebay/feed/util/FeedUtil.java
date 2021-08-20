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
import java.util.zip.GZIPInputStream;

import okhttp3.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.EnvTypeEnum;
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
     * @param feedRequest Container for capturing parameters related to
     * downloading and unzipping feed files
     * @return string Creates a url for invoking feed api, based on the inputs
     */
    protected String getFinalUrl(FeedRequest feedRequest) {

        String finalUrl = null;
//        FeedTypeEnum type = feedRequest.getType();
        FeedTypeEnum type = FeedTypeEnum.getFeedEnum(feedRequest.getType());

        String baseUrl = getBaseUrl(feedRequest);
        StringBuilder bdr = new StringBuilder(baseUrl);
        switch (type) {

            case ITEM:

                bdr.append(Constants.ITEM_RESOURCE_SCOPE);
                bdr.append(feedRequest.getFeedScope());
                bdr.append(Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_CATEGORY_ID);
                bdr.append(feedRequest.getCategoryId());
//                if (feedRequest.getFeedScope().equalsIgnoreCase(Constants.NEWLY_LISTED)) {
                bdr.append(Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_DATE);
                bdr.append(feedRequest.getDate());
//                }
                finalUrl = bdr.toString();
                break;

            case ITEM_SNAPSHOT:

                bdr.append(Constants.ITEM_SNAPSHOT_RESOURCE);
                bdr.append(Constants.QUERY_CATEGORY_ID);
                bdr.append(feedRequest.getCategoryId());
                bdr.append(Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_SNAPSHOT_DATE);
                bdr.append(feedRequest.getSnapshotDate());
                finalUrl = bdr.toString();
                break;

            default:
                break;
        }

        LOGGER.debug("Final URL for API = {}", finalUrl);
        return finalUrl;
    }

    private String getBaseUrl(FeedRequest feedRequest) {
        EnvTypeEnum env = EnvTypeEnum.getEnvEnum(feedRequest.getEnv());
        String baseUrl = Constants.FEED_API_PROD_BASE;
        if (env != null && env == EnvTypeEnum.SANDBOX) {
            baseUrl = Constants.FEED_API_SANDBOX_BASE;
        }
        return baseUrl;
    }

    public Long getChunkSizeLimit(FeedRequest feedRequest) {
        EnvTypeEnum env = EnvTypeEnum.getEnvEnum(feedRequest.getEnv());
        Long chunkSize = Constants.PROD_CHUNK_SIZE;
        if (env != null && env == EnvTypeEnum.SANDBOX) {
            chunkSize = Constants.SANDBOX_CHUNK_SIZE;
        }
        return chunkSize;
    }

    /**
     * <p>
     * Generate request instance for the feed API call
     * </p>
     *
     * @param feedRequest Container for capturing parameters related to
     * downloading and unzipping feed files
     * @param requestBuilder Returns a builder
     * @return RequestBuilder Returns a builder
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
     * Generates a file name based on the input parameters</p>
     *
     * @param feedRequest Container for capturing parameters related to
     * downloading and unzipping feed files
     * @return string Generates a filename based on the feed type, scope,
     * category, marketplace and date
     */
    public String generateFileName(FeedRequest feedRequest) {

        String fileType
                = Constants.ALL_ACTIVE.equalsIgnoreCase(feedRequest.getFeedScope()) ? Constants.BOOTSTRAP
                : Constants.DAILY;

        FeedTypeEnum type = FeedTypeEnum.getFeedEnum(feedRequest.getType());

        // switch (feedRequest.getType()) {
        switch (type) {
            case ITEM:
                return "item_" + fileType + "-" + feedRequest.getCategoryId() + "-" + feedRequest.getDate()
                        + "-" + feedRequest.getMarketplaceId() + ".gz";
            case ITEM_SNAPSHOT:
                return "item_snapshot-" + feedRequest.getCategoryId() + "-" + feedRequest.getSnapshotDate().replaceAll(":", "")
                        + "-" + feedRequest.getMarketplaceId() + ".gz";
            default:
                return null;
        }
    }

    /**
     * <p>
     * Unzips a file and creates a new file and returns the path Returns null in
     * case of errors
     * </p>
     *
     * @param path Path to the downloaded compressed feed file
     * @return string Path to the unzipped file
     * @throws Exception Exception
     */
    public String unzip(Path path) throws Exception {

        if (path == null) {
            return null;
        }

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
