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

import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Class responsible for validating feed and filter requests
 * </p>
 *
 * @author shanganesh
 *
 */
public class FeedValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedValidator.class);
    SimpleDateFormat sdf;
    Calendar calendar;

    /**
     * Check path validity
     *
     * @param filePath Checks if the file exists on the specified path
     * @return boolean Indicates whether the file was found on the specified
     * path
     */
    public boolean isValidPath(String filePath) {

        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        Path path = Paths.get(filePath);

        return Files.exists(path);
    }

    /**
     * <p>
     * Check feed filter request
     * </p>
     *
     * @param filterRequest Container for capturing parameters related to
     * filtering the feed files
     * @return boolean Indicates whether all the mandatory parameters are
     * present
     */
    public boolean isValidFilterRequest(FeedFilterRequest filterRequest) {

        if (filterRequest == null) {
            return false;
        }

        if (filterRequest.getInputFilePath() == null || filterRequest.getInputFilePath().isEmpty()) {
            return false;
        }

        Path path = Paths.get(filterRequest.getInputFilePath());

        if (!Files.exists(path)) {
            return false;
        }

        if (filterRequest.isEmpty()) {
            return false;
        }

        if (!isValidLevelNRequest(filterRequest)) {
            return false;
        }

        return true;
    }

    /**
     * Check that the mandatory parameters are present, in case filtering on
     * level 'n' is requested
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
                    || filterRequest.getLevelOneCategory() == null) {
                isValid = false;
            }

        }
        return isValid;
    }

    /**
     * Check that the date format is valid and date is within valid range for
     * selected feed type
     *
     * Feed Type Item date format is yyyyMMdd and range would be within previous
     * 3-14 days
     *
     * Feed Type ItemSnapshot date format is yyyy-MM-dd'T'HH:00:00.000'Z' and
     * range would be within previous 7 days
     *
     * @param feedRequest
     * @return
     */
    public boolean isValidDateFormatAndRange(FeedRequest feedRequest) {
        boolean isValid = true;
        FeedTypeEnum type = FeedTypeEnum.getFeedEnum(feedRequest.getType());

        switch (type) {
            case ITEM:
                if (Constants.NEWLY_LISTED.equalsIgnoreCase(feedRequest.getFeedScope())) {
                    try {
                        sdf = new SimpleDateFormat(Constants.QUERY_DATE_FORMAT);
                        Date feedItemRequestDate = sdf.parse(feedRequest.getDate());
                        LOGGER.debug("feedItemRequestDate : {}", feedItemRequestDate);

                        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
                        calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        LOGGER.debug("Current Time in GMT Time Zone : {}", calendar.getTime());
//                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, Constants.NEWLY_LISTED_AVAILABILITY_START_DAY);
                        Date feedItemAvailabilityStartDate = sdf.parse(sdf.format(calendar.getTime()));
                        LOGGER.debug("feedItemAvailabilityStartDate : {}", feedItemAvailabilityStartDate);

                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH, Constants.NEWLY_LISTED_AVAILABILITY_END_DAY);
                        Date feedItemAvailabilityEndDate = sdf.parse(sdf.format(calendar.getTime()));
                        LOGGER.debug("feedItemAvailabilityEndDate : {}", feedItemAvailabilityEndDate);

                        if (!isWithinRange(feedItemRequestDate, feedItemAvailabilityStartDate, feedItemAvailabilityEndDate)) {
                            isValid = false;
                        }
                        // good format and within valid range
                    } catch (ParseException ex) {
                        isValid = false;
                        LOGGER.error("Item Date format is not valid. Cannot get feed. Aborting...", ex);
                    }
                }

                break;

            case ITEM_SNAPSHOT:
                try {
                sdf = new SimpleDateFormat(Constants.QUERY_SNAPSHOT_DATE_FORMAT);
                Date feedItemSnapshotRequestDate = sdf.parse(feedRequest.getSnapshotDate());
                LOGGER.debug("feedItemSnapshotRequestDate : {}", feedItemSnapshotRequestDate);

                TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
                calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                LOGGER.debug("Current Time in GMT Time Zone : {}", calendar.getTime());
                //Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, Constants.ITEM_SNAPSHOT_AVAILABILITY_START_DAY);
                Date feedItemSnapshotAvailabilitStartDate = sdf.parse(sdf.format(calendar.getTime()));
                LOGGER.debug("feedItemSnapshotAvailabilitStartDate : {}", feedItemSnapshotAvailabilitStartDate);

                calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, Constants.ITEM_SNAPSHOT_AVAILABILITY_END_HOUR);
                Date feedItemSnapshotAvailabilitEndDate = sdf.parse(sdf.format(calendar.getTime()));
                LOGGER.debug("feedItemSnapshotAvailabilitEndDate : {}", feedItemSnapshotAvailabilitEndDate);

                if (!isWithinRange(feedItemSnapshotRequestDate, feedItemSnapshotAvailabilitStartDate, feedItemSnapshotAvailabilitEndDate)) {
                    isValid = false;
                }
                // good format and within valid range
            } catch (ParseException ex) {
                isValid = false;
                LOGGER.error("Item Snapshot Date format is not valid. Cannot get feed. Aborting...", ex);
            }
            break;

            default:
                break;
        }
        return isValid;

    }

    /**
     * Feed Type Item date range would be within previous 3-14 days
     *
     * Feed Type ItemSnapshot date range would be within previous 7 days
     *
     * @param queryRequestDate
     * @param startDate
     * @param endDate
     * @return
     */
    private boolean isWithinRange(Date queryRequestDate, Date startDate, Date endDate) {
        return !(queryRequestDate.before(startDate) || queryRequestDate.after(endDate));
    }
}
