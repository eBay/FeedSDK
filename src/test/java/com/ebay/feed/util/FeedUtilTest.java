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

import okhttp3.Request;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.EnvTypeEnum;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;

public class FeedUtilTest {

    FeedUtil feedUtil = new FeedUtil();

    @Test
    public void getFinalUrlInvalidTypeTest() {

        String expectedUrl
                = Constants.FEED_API_PROD_BASE + Constants.ITEM_RESOURCE_SCOPE + "ALL_ACTIVE"
                + Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_CATEGORY_ID + "1"
                + Constants.QUERY_PARAM_SEPARATOR + Constants.QUERY_DATE + "20180101";
        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");

        Assert.assertEquals(expectedUrl, feedUtil.getFinalUrl(builder.build()));
    }

    @Test
    public void generateRequestTest() {

        Request.Builder requestBuilder = new Request.Builder();

        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");
        builder.token(Constants.TOKEN_BEARER_PREFIX + "v1...");
        builder.siteId("EBAY_US");

        feedUtil.generateRequest(builder.build(), requestBuilder);

        Request request = requestBuilder.build();
        Assert.assertNotNull(request.header(Constants.AUTHORIZATION_HEADER));
        Assert.assertNotNull(request.header(Constants.MARKETPLACE_HEADER));
        Assert.assertNotNull(request.header(Constants.CONTENT_TYPE_HEADER));
        Assert.assertNotNull(request.header(Constants.ACCEPT_HEADER));
    }

    @Test
    public void generateItemSnapshotRequestTest() {

        Request.Builder requestBuilder = new Request.Builder();

        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("ITEM_SNAPSHOT");
        builder.categoryId("1");
        builder.snapshotDate("2018-08-05T02:00:00.000Z");
        builder.token(Constants.TOKEN_BEARER_PREFIX + "v1...");
        builder.siteId("EBAY_US");

        feedUtil.generateRequest(builder.build(), requestBuilder);

        Request request = requestBuilder.build();
        Assert.assertNotNull(request.header(Constants.AUTHORIZATION_HEADER));
        Assert.assertNotNull(request.header(Constants.MARKETPLACE_HEADER));
        Assert.assertNotNull(request.header(Constants.CONTENT_TYPE_HEADER));
        Assert.assertNotNull(request.header(Constants.ACCEPT_HEADER));
    }

    @Test
    public void generateFileNameSnapshotTest() {

        String expectedFileName = "item_snapshot-1281-2018-08-05T020000.000Z-EBAY_US.gz";
        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item_snapshot");
        builder.categoryId("1281");
        builder.snapshotDate("2018-08-05T020000.000Z");
        builder.siteId("EBAY_US");
        Assert.assertEquals(expectedFileName, feedUtil.generateFileName(builder.build()));
    }

    @Test
    public void generateFileNameTest() {

        String expectedFileName = "item_daily-1-20180101-EBAY_US.gz";
        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("NEWLY_LISTED");
        builder.date("20180101");
        builder.siteId("EBAY_US");
        Assert.assertEquals(expectedFileName, feedUtil.generateFileName(builder.build()));
    }

    @Test
    public void generateFileNameBootstrapTest() {

        String expectedFileName = "item_bootstrap-1-20180101-EBAY_US.gz";
        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");
        builder.siteId("EBAY_US");
        Assert.assertEquals(expectedFileName, feedUtil.generateFileName(builder.build()));
    }

    @Test
    public void getChunkSizeLimitTest() {
        // Prod
        Long expectedChunkSize
                = Constants.PROD_CHUNK_SIZE;
        FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");

        Assert.assertEquals(expectedChunkSize, feedUtil.getChunkSizeLimit(builder.build()));

        // Sandbox
        expectedChunkSize
                = Constants.SANDBOX_CHUNK_SIZE;
        builder = new FeedRequestBuilder();
        builder.type("item");
        builder.categoryId("1");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");
        builder.env(EnvTypeEnum.SANDBOX.name());
        Assert.assertEquals(expectedChunkSize, feedUtil.getChunkSizeLimit(builder.build()));
    }
}
