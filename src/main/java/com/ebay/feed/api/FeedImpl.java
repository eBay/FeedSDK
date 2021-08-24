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

import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.feed.auth.CredentialLoader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebay.feed.constants.Constants;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.config.ConfigFileBasedRequest;
import com.ebay.feed.model.feed.operation.config.ConfigRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.internal.InvokeResponse;
import com.ebay.feed.model.oauth.AuthRequest;
import com.ebay.feed.util.FeedUtil;
import com.ebay.feed.util.FilterUtil;
import com.ebay.feed.validator.FeedValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

/**
 * <div>
 * Concrete implementation which consists of the core capabilities of the
 * SDK.<br>
 * <ul>
 * <li>get - To download the feed files</li>
 * <li>unzip - To unzip the gzipped files filter</li>
 * <li>filter - To apply filters to the unzipped file and create a new file with
 * the filtered contents</li>
 * </ul>
 * </div>
 *
 * @author shanganesh
 *
 */
public class FeedImpl implements Feed {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedImpl.class);
    private OkHttpClient client = null;
    private FeedUtil feedUtils = null;
    private FilterUtil filterUtils = null;
    private FeedValidator feedValidator = null;
    String credentialFilePath = "credentials.yaml";

    public FeedImpl() {
        client
                = new OkHttpClient.Builder().connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS).build();
        LOGGER.debug("Initialized http client with timeout in seconds - {} ", Constants.TIMEOUT);
        feedUtils = new FeedUtil();
        feedValidator = new FeedValidator();
        filterUtils = new FilterUtil();
    }

    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.Feed#filter(java.lang.String, com.ebay.feed.model.FeedFilterRequest)
     */
    @Override
    public com.ebay.feed.model.feed.operation.filter.Response filter(FeedFilterRequest filterRequest) {

        LOGGER.debug("Entering Feed.filter()");

        if (!feedValidator.isValidFilterRequest(filterRequest)) {
            LOGGER.debug("Null baseFilePath or filterRequest. Cannot filter. Aborting...");
            return createResponse(-1, "Null baseFilePath or filterRequest. Cannot filter. Aborting...",
                    null, filterRequest);
        }

        Path path = Paths.get(filterRequest.getInputFilePath());
        LOGGER.debug("Filter Params = {}", filterRequest.toString());

        String filteredFilePath = null;

        try {

            // update leaf categories in case of level two and three filters
            filterRequest.getLeafCategoryIds().addAll(filterUtils.calculateLeaves(filterRequest));
            filteredFilePath = filterUtils.filter(path, filterRequest);

        } catch (Exception e) {
            LOGGER.error("Exception in Feed.filter()", e);
            e.printStackTrace();
            filteredFilePath = null;
            return createResponse(-1, e.getMessage(), null, filterRequest);
        }
        LOGGER.debug("Exiting Feed.filter() and created filtered file  = {}", filteredFilePath);
        return createResponse(0, "Success", filteredFilePath, filterRequest);
    }

    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.Feed#unzip(java.lang.String, boolean)
     */
    @Override
    public com.ebay.feed.model.feed.operation.filter.Response unzip(String filePath) {

        LOGGER.debug("********* Begin Feed.unzip() with filePath = {}", filePath);

        if (filePath == null) {
            new com.ebay.feed.model.feed.operation.filter.Response(-1,
                    "FilePath cannot be null. Aborting..", null, null);
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return createResponse(-1, "Base file does not exist. Aborting...", null, null);
        }

        String unzippedFilePath = null;
        try {
            unzippedFilePath = feedUtils.unzip(path);
        } catch (Exception e) {
            LOGGER.error("Exception in Feed.unzip()", e);
            e.printStackTrace();
            unzippedFilePath = null;
            return createResponse(-1, e.getMessage(), null, null);
        }
        LOGGER.debug("Exiting Feed.unzip() and unzipped file  = {}", unzippedFilePath);

        return createResponse(Constants.SUCCESS_CODE, Constants.SUCCESS, unzippedFilePath, null);
    }

    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.Feed#get(com.ebay.feed.model.FeedRequest)
     */
    @Override
    public GetFeedResponse get(FeedRequest feedRequest, String downloadDirectory) {
        LOGGER.debug("********* using default credentialFilePath = {}", credentialFilePath);
        return get(feedRequest, downloadDirectory, credentialFilePath);
    }

    /**
     * <p>
     * Downloads the feed file as specified by the parameters in the
     * request.Based on the feedRequest values, if a file is available, then it
     * is downloaded and the method returns the file path. If the file is not
     * available, then the error details are returned.
     * </p>
     *
     * @param feedRequest Container for capturing parameters for downloading
     * feed file
     * @param downloadDirectory The local directory where the file should be
     * downloaded. Default location is the current working directory
     * @param credentialConfigFilePath credentials file absolute path, required
     * for token refresh on the fly to support resume capability while
     * downloading feeds. Default location is the current working directory
     * @return GetFeedResponse GetFeedResponse
     */
    @Override
    public GetFeedResponse get(FeedRequest feedRequest, String downloadDirectory, String credentialConfigFilePath) {

        if (null == credentialConfigFilePath || credentialConfigFilePath.isEmpty()) {
            LOGGER.debug("********* using default credentialFilePath = {}", credentialFilePath);
            credentialConfigFilePath = credentialFilePath;
        }
        LOGGER.debug("********* Begin Feed.get()");

        GetFeedResponse response = null;
        Path downloadDirectoryPath = null;

        if (feedRequest == null) {
            return new GetFeedResponse(-1, "feedRequest is null.Aborting..", null, null);
        }

        // if null, then take default path - current directory
        if (downloadDirectory == null) {
            downloadDirectoryPath = Paths.get(".").toAbsolutePath().normalize();
        } else {
            downloadDirectoryPath = Paths.get(downloadDirectory);
        }

        LOGGER.debug("feedRequest params = {}", feedRequest.toString());

        try {
            response = process(feedRequest, downloadDirectoryPath, credentialConfigFilePath);

        } catch (Exception e) {
            LOGGER.error("Exception in Feed.get()", e);
            e.printStackTrace();
            response = new GetFeedResponse(Constants.FAILURE_CODE, e.getMessage(), null, null);
        }

        LOGGER.debug("Exiting Feed.get()  {}", response.getFilePath());
        return response;
    }

    /**
     * <p>
     * The process method performs pre requisite functions, before calling the
     * feed API - Adds additional headers including range - Creates default
     * file/folder path and cleansup if already present
     * </p>
     *
     * @param feedRequest
     * @param downloadDirectory Optional local directory where files can be
     * downloaded. Default is current working directory
     * @return
     * @throws IOException
     */
    private GetFeedResponse process(FeedRequest feedRequest, Path downloadDirectory, String credentialConfigFilePath)
            throws IOException {

        LOGGER.debug("Entering Feed.process()");

        if (feedRequest.getToken() == null || feedRequest.getType() == null
                || feedRequest.getCategoryId() == null || feedRequest.getMarketplaceId() == null) {
            return new GetFeedResponse(-1, "Mandatory feedRequest parameters are null. Aborting..", null,
                    null);
        }

        // checking if date format is valid and date is within valid range
        if (!feedValidator.isValidDateFormatAndRange(feedRequest)) {
            LOGGER.debug("Date format is not valid or Date is not within valid range. Cannot get feed. Aborting...");
            return new GetFeedResponse(-1, "Date format is not valid or Date is not within valid range. Cannot get feed. Aborting...",
                    null, null);
        }

        Request.Builder requestBuilder = new Request.Builder();

        Path pathToFile
                = Paths.get(downloadDirectory.toString() + "/" + feedUtils.generateFileName(feedRequest));
        LOGGER.debug("Path to store file = {}", pathToFile);

        if (Files.exists(pathToFile)) {
            Files.delete(pathToFile);
        } else {
            Files.createDirectories(pathToFile.getParent());
        }

        Path path = Files.createFile(pathToFile);

        // generate static request
        requestBuilder = feedUtils.generateRequest(feedRequest, requestBuilder);

        // generate dynamic header    
        Long chunkSizeLimit = feedUtils.getChunkSizeLimit(feedRequest);
        requestBuilder.addHeader(Constants.RANGE_HEADER, Constants.RANGE_PREFIX + chunkSizeLimit);

        // invoke request
        return invoker(requestBuilder, path, true, chunkSizeLimit, feedRequest, credentialConfigFilePath);
    }

    /**
     * <p>
     * Invokes the feed API with the max range value of 100 MB. If the file is
     * lesser than 100 MB, then it returns the downloaded file path along with
     * the status.
     *
     * If the file is greater than 100 MB - Iteratively calls feed API, with
     * incrementing range headers - Appends content to file - Downloads entire
     * content and returns with downloaded file path.
     *
     * </p>
     *
     * @param request The API request
     * @param path Path of the downloaded or partially downloading file, where
     * contents need to be appended
     * @param isStart - THe first request sets this parameter to true. If
     * subsequent requests are required in case of 206, then this is set to
     * false.
     * @param chunkSizeLimit - This indicates the max chunkSize limit. For prod,
     * it is 100 MB and for sandbox, it is 10MB.
     * @return
     */
    private GetFeedResponse invoker(Request.Builder requestBuilder, Path path, boolean isStart, Long chunkSizeLimit, FeedRequest feedRequest, String credentialConfigFilePath) {

        LOGGER.debug("Entering Feed.invoker()");

        Request request = requestBuilder.build();

        LOGGER.debug("API request = {}", request.toString());

        InvokeResponse responseFlag = invokeIteratively(request, path);

        LOGGER.debug("First API Response = {}", responseFlag.toString());

        if (responseFlag.getStatusCode() == 200) {
            LOGGER.debug("First API Response is 200. All done..");
            return new GetFeedResponse(Constants.SUCCESS_CODE, Constants.SUCCESS, fixFilePath(path, responseFlag), null);

        } else if (responseFlag.getStatusCode() == 206) {

            LOGGER.debug("First API Response is 206. Performing iterations...");

            long requestRangeUpperLimit
                    = Long.valueOf(requestBuilder.build().header("Range").split("-")[1]) + 1;
            long responseRangeUpperLimit = Long.valueOf(responseFlag.getContentRange().split("/")[1]);

            boolean isCredentialLoaded = false;
            //config file path for loading credentials
            AuthRequest authRequest = new AuthRequest(credentialConfigFilePath, null);
            CredentialLoader credentialLoader = new CredentialLoader(authRequest);
            AccessToken accessToken = null;
            try {
                credentialLoader.loadCredentials();
                //Getting access using credentials file as we have to use this for resume capability
                accessToken = updateAccessTokenOnTheFly(credentialLoader, feedRequest, requestBuilder);
                isCredentialLoaded = true;
            } catch (Exception e) {
            }
            while (requestRangeUpperLimit <= responseRangeUpperLimit) {

                long newUpperLimit = requestRangeUpperLimit + chunkSizeLimit;
                String val = "bytes=" + requestRangeUpperLimit + "-" + newUpperLimit;

                requestBuilder.removeHeader(Constants.RANGE_HEADER);
                requestBuilder.addHeader(Constants.RANGE_HEADER, val);

                //this block will not execute if token passed direcrly. Backward compatibile
                if (isCredentialLoaded) {
                    if (checkTokenExpiry(accessToken, credentialLoader, feedRequest, requestBuilder)) {
                        return new GetFeedResponse(Constants.FAILURE_CODE, Constants.FAILURE, null, null);
                    }
                }

                responseFlag = invokeIteratively(requestBuilder.build(), path);
                //Showing download progess
                LOGGER.debug("First API Response = {}", responseFlag.toString());

                if (responseFlag == null) {
                    return new GetFeedResponse(-1, "Internal error. Please check the logs..", null, null);
                }

                requestRangeUpperLimit
                        = Long.valueOf(requestBuilder.build().header(Constants.RANGE_HEADER).split("-")[1]) + 1;
                responseRangeUpperLimit = Long.valueOf(responseFlag.getContentRange().split("/")[1]);

            }
            return new GetFeedResponse(Constants.SUCCESS_CODE, Constants.SUCCESS, fixFilePath(path, responseFlag), null);
        } else {
            LOGGER.debug("First API Response is error. Aborting...");
            return new GetFeedResponse(Constants.FAILURE_CODE, Constants.FAILURE, null, null);
        }
    }

    /**
     * 
     * @param accessToken
     * @param credentialLoader
     * @param feedRequest
     * @param requestBuilder
     * @return 
     */
    private boolean checkTokenExpiry(AccessToken accessToken, CredentialLoader credentialLoader, FeedRequest feedRequest, Request.Builder requestBuilder) {
        try {
            Date currentTime = new Date();
            //token refresh on the fly from config file to support resume capability.
            if (accessToken.getExpiresOn().before(currentTime)) {
                LOGGER.debug("Old Token expiry time = {}", accessToken.getExpiresOn());
                accessToken = updateAccessTokenOnTheFly(credentialLoader, feedRequest, requestBuilder);
                LOGGER.info("Got refresh token on the fly and New Token Expiry time = {}", accessToken.getExpiresOn());
            }
        } catch (Exception e) {
            LOGGER.info("Exception in fetching the new access token " + e.getMessage());
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Since date is optional param for getting bootstrap feed, filePath will
     * have null value(item_bootstrap-11116-null-EBAY_US.gz) This method helps
     * to rename the null value with LastModified api response header
     * </p>
     *
     * @param originalFilePath
     * @param invokeResponse
     * @return
     */
    private String fixFilePath(Path originalFilePath, InvokeResponse invokeResponse) {
        Path newFilePath = originalFilePath;
        if (originalFilePath.toString().contains("null") && !StringUtils.isEmpty(invokeResponse.getLastModified())) {
            String newPath = originalFilePath.toString().replace("null", invokeResponse.getLastModified());
            newFilePath = Paths.get(newPath);
            try {
                Files.move(originalFilePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("Unable to rename the bootstrap item feed file with date field", e);
            }
        }
        return newFilePath.toString();
    }

    /**
     * <p>
     * Invoked, only if the file size is greater than max chunk size
     * </p>
     *
     * @param request The API request
     * @param path Path of the downloaded or partially downloading file, where
     * contents need to be appended
     * @return
     */
    private InvokeResponse invokeIteratively(Request request, Path path) {

        InvokeResponse responseFlag = null;

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                LOGGER.debug("Error in API response - status = {}, body = {}", response.code(), response
                        .body().string());
                return new InvokeResponse(null, response.code());
            }

            InputStream is = response.body().byteStream();

            OutputStream outStream = new FileOutputStream(path.toString(), true);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            outStream.flush();
            outStream.close();
            is.close();

            String lastModifiedHeader = response.header(Constants.LAST_MODIFIED_DATE_HEADER);
            String lastModifiedDate = null;
            if (!StringUtils.isEmpty(lastModifiedHeader)) {
                LocalDate localDate = LocalDate.parse(lastModifiedHeader, DateTimeFormatter.RFC_1123_DATE_TIME);
                lastModifiedDate = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
            }

            responseFlag
                    = new InvokeResponse(response.header(Constants.CONTENT_RANGE_HEADER), response.code(), lastModifiedDate);

        } catch (Throwable t) {
            LOGGER.error("Exception in feed.invokeIteratively()", t);
            responseFlag = new InvokeResponse(null, 400);
        }
        return responseFlag;
    }

    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.Feed#processConfigFile(java.lang.String, java.lang.String)
     */
    @Override
    public List<com.ebay.feed.model.feed.operation.filter.Response> processConfigFile(
            String configFile, String token) {

        if (configFile == null || !feedValidator.isValidPath(configFile)) {
            LOGGER.debug("Error in Feed.process(). Config file parameter is {}", configFile);
        }

        Path path = null;

        // container for holding list of responses
        List<com.ebay.feed.model.feed.operation.filter.Response> responses = new LinkedList<>();
        GetFeedResponse getFeedResponse = null;
        com.ebay.feed.model.feed.operation.filter.Response unzipResponse = null;
        FeedRequest feedRequest = null;
        FeedFilterRequest filterRequest = null;
        try {

            // init gson
            Gson gson = new GsonBuilder().create();
            path = Paths.get(configFile);

            // read config file contents
            String configContents = new String(Files.readAllBytes(path));

            if (configContents == null || configContents.isEmpty()) {
                throw new Exception();
            }

            // init config pojo
            ConfigFileBasedRequest request = gson.fromJson(configContents, ConfigFileBasedRequest.class
            );

            if (request == null || request.getRequests() == null || request.getRequests().isEmpty()) {
                LOGGER.debug("Invalid or empty config");
                throw new Exception();
            }

            LOGGER.debug("ConfigFileBasedRequest = {}", request.toString());

            // iterate and process
            for (ConfigRequest configRequest : request.getRequests()) {

                try {

                    // inputs for each instance
                    feedRequest = configRequest.getFeedRequest();
                    filterRequest = configRequest.getFilterRequest();

                    // download and filter may be required
                    if (feedRequest != null) {

                        // set token
                        feedRequest.setToken(Constants.TOKEN_BEARER_PREFIX + token);

                        // download
                        getFeedResponse = get(feedRequest, null);

                        // success response, proceed to unzip
                        if (getFeedResponse != null && getFeedResponse.getStatusCode() == 0) {

                            // unzip
                            unzipResponse = unzip(getFeedResponse.getFilePath());

                        }
                    }

                    // if filter request is passed, then apply filters
                    if (filterRequest != null) {

                        String filePath
                                = filterRequest.getInputFilePath() == null ? unzipResponse.getFilePath()
                                : filterRequest.getInputFilePath();

                        filterRequest.setToken(token);
                        filterRequest.setInputFilePath(filePath);

                        // update level two and three leaves
                        filterRequest.getLeafCategoryIds().addAll(filterUtils.calculateLeaves(filterRequest));

                        com.ebay.feed.model.feed.operation.filter.Response filterResponse
                                = filter(filterRequest);
                        responses.add(filterResponse);
                    }
                } catch (Exception e) {
                    LOGGER.debug("Exception in feed.process() , {}", e);
                    responses.add(new com.ebay.feed.model.feed.operation.filter.Response(
                            Constants.FAILURE_CODE, "Error processing config file", null, filterRequest));
                }
            }
            LOGGER.debug("Completed processing for all items - {}", responses.toString());

        } catch (Exception e) {
            LOGGER.debug("Exception in feed.process() , {}", e);
            responses.add(new com.ebay.feed.model.feed.operation.filter.Response(
                    Constants.FAILURE_CODE, "Error processing config file", null, null));
            return responses;
        }
        return responses;
    }

    /**
     * <p>
     * Returns a new instance of response, based on the provided inputs
     * </p>
     *
     * @param statusCode
     * @param message
     * @param filePath
     * @param appliedFilters
     * @return
     */
    private com.ebay.feed.model.feed.operation.filter.Response createResponse(Integer statusCode,
            String message, String filePath, FeedFilterRequest appliedFilters) {

        LOGGER.debug("Returning response status code = {}, message = {}", statusCode, message);
        LOGGER.debug("Filtered file = {}, appliedFilters = {}", filePath,
                appliedFilters != null ? appliedFilters.toString() : null);

        return new com.ebay.feed.model.feed.operation.filter.Response(statusCode, message, filePath,
                appliedFilters);
    }

    /**
     * Getting accessToken on the fly using credential file to support resume
     * capability
     *
     * @param credentialLoader
     * @param environment
     * @param feedRequest
     * @param requestBuilder
     * @return
     */
    private AccessToken updateAccessTokenOnTheFly(CredentialLoader credentialLoader, FeedRequest feedRequest, Request.Builder requestBuilder) throws IOException {
        AccessToken accessToken = credentialLoader.getOauthResponse(feedRequest.getEnv()).getAccessToken().get();
        //using token which generated from credentials file as we don't know the expiry time of directly passed token
        if (!accessToken.getToken().equalsIgnoreCase(feedRequest.getToken())) {
            String token = Constants.TOKEN_BEARER_PREFIX + accessToken.getToken();
            requestBuilder.removeHeader(Constants.AUTHORIZATION_HEADER);
            requestBuilder.addHeader(Constants.AUTHORIZATION_HEADER, token);
        }
        return accessToken;
    }
}
