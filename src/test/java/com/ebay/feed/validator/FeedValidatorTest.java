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
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
        //Assert.assertTrue(feedValidator.isValidPath("/tmp"));
        String path = Paths.get("").toAbsolutePath().toString();
        Assert.assertTrue(feedValidator.isValidPath(path));
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
        //filterRequest.setInputFilePath("/tmp");
        String path = Paths.get("").toAbsolutePath().toString();
        filterRequest.setInputFilePath(path);

        Set<String> levelThreeCats = new HashSet<>();
        levelThreeCats.add("1");
        filterRequest.setLevelThreeCategories(levelThreeCats);

        filterRequest.setToken(Constants.TOKEN_BEARER_PREFIX + "v1...");
        filterRequest.setMarketplace("EBAY_US");
        filterRequest.setLevelOneCategory("1");

        Assert.assertTrue(feedValidator.isValidFilterRequest(filterRequest));
    }

    @Test
    public void isValidDateFormatAndRangeFeedItemScopeALLACTIVETest() {

        FeedRequest.FeedRequestBuilder builder = new FeedRequest.FeedRequestBuilder();
        builder.type("item");
        builder.feedScope("ALL_ACTIVE");
        builder.date("20180101");
        Assert.assertTrue(feedValidator.isValidDateFormatAndRange(builder.build()));
    }

    @Test
    public void isValidDateFormatAndRangeFeedItemScopeNEWLYLISTEDTest() {

        FeedRequest.FeedRequestBuilder builder = new FeedRequest.FeedRequestBuilder();
        builder.type("item");
        builder.feedScope("NEWLY_LISTED");
        //builder.date("20210121");
        builder.date(getFeedDate("item"));
        Assert.assertTrue(feedValidator.isValidDateFormatAndRange(builder.build()));
    }

    private String getFeedDate(String feedType) {
        SimpleDateFormat sdf;
        if (feedType.equalsIgnoreCase("item")) {
            sdf = new SimpleDateFormat(Constants.QUERY_DATE_FORMAT);
        } else {
            sdf = new SimpleDateFormat(Constants.QUERY_SNAPSHOT_DATE_FORMAT);
        }
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.add(Calendar.DAY_OF_MONTH, -5);
        String date = sdf.format(calendar.getTime());

        return date;
    }

    @Test
    public void isValidDateFormatAndRangeFeedItemSnapshotTest() {

        FeedRequest.FeedRequestBuilder builder = new FeedRequest.FeedRequestBuilder();
        builder.type("item_snapshot");
        //builder.snapshotDate("2021-01-26T12:00:00.000Z");
        builder.snapshotDate(getFeedDate("item_snapshot"));
        Assert.assertTrue(feedValidator.isValidDateFormatAndRange(builder.build()));
    }

    @Test
    public void isValidDateFormatAndRangeFeedItemScopeNEWLYLISTEDInvalidDateTest() {

        FeedRequest.FeedRequestBuilder builder = new FeedRequest.FeedRequestBuilder();
        builder.type("item");
        builder.feedScope("NEWLY_LISTED");
        builder.date("20180101");
        Assert.assertFalse(feedValidator.isValidDateFormatAndRange(builder.build()));
    }

    @Test
    public void isValidDateFormatAndRangeFeedItemSnapshotInvalidSnapshotDateTest() {

        FeedRequest.FeedRequestBuilder builder = new FeedRequest.FeedRequestBuilder();
        builder.type("item_snapshot");
        builder.snapshotDate("2018-08-05T02:00:00.000Z");
        Assert.assertFalse(feedValidator.isValidDateFormatAndRange(builder.build()));
    }
}
