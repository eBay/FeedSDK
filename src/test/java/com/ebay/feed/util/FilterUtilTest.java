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

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;

public class FilterUtilTest {

    FilterUtil filterUtil = new FilterUtil();

    @Test
    public void evaluateNegativeTest() {

        FeedFilterRequest request = new FeedFilterRequest();
        request.setLeafCategoryIds(getFilterSet());;
        Assert.assertTrue(filterUtil.evaluate(getLine("123"), request));

    }

    @Test
    public void evaluatePositiveTest() {

        FeedFilterRequest request = new FeedFilterRequest();
        request.setLeafCategoryIds(getFilterSet());;
        Assert.assertFalse(filterUtil.evaluate(getLine("456"), request));

    }

    private Set<String> getFilterSet() {
        Set<String> filterSet = new HashSet<>();
        filterSet.add("123");
        return filterSet;
    }

    private String[] getLine(String filterValue) {

        String[] arr = new String[50];

        for (int i = 0; i < 50; i++) {
            arr[i] = filterValue;
        }
        return arr;
    }

}
