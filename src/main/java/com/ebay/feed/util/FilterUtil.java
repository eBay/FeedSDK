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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ebay.feed.api.Taxonomy;
import com.ebay.feed.api.TaxonomyImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.taxonomy.CategoryResponse;

/**
 * <p>
 * Utility class to perform filtering on feed files
 * </p>
 *
 * @author shanganesh
 *
 */
public class FilterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterUtil.class);

    /**
     * <p>
     * Apply the filters to the contents specified in the baseFilePath
     * </p>
     *
     * @param baseFilePath Path to the unzipped feed file
     * @param filterRequest Container for capturing the filter parameters
     * @return string The path of the filtered file
     * @throws Exception exception
     */
    public String filter(Path baseFilePath, FeedFilterRequest filterRequest) throws Exception {

        LOGGER.debug("******* Begin filtering on file = {} with parameters = {}", baseFilePath,
                filterRequest);

        String filteredFile = getFilteredFileName(baseFilePath, filterRequest);

        try (BufferedReader r = new BufferedReader(new FileReader(baseFilePath.toFile()));
                BufferedWriter w = new BufferedWriter(new FileWriter(filteredFile))) {

            String line = null;

            while ((line = r.readLine()) != null) {
                String[] tsv = line.split(Constants.SEPARATOR);

                // only for header
                if (evaluateHeader(tsv)) {
                    w.write(line + System.lineSeparator());
                }

                // actual listings
                if (evaluate(tsv, filterRequest)) {
                    w.write(line + System.lineSeparator());
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in FilterUtils.filter()", e);
            throw e;
        }
        return filteredFile;
    }

    /**
     *
    <div>
    Evaluate the provided conditions The column number passed as a parameter to the 'evaluateField'
    method, represents the column number in the actual file in the response.
    <ul>
    <li>4 - category id</li>
    <li>6 - seller user name</li>
    <li>21 - item location country</li>
    <li>12 - epid</li>
    <li>40 - inferred epid</li>
    <li>9 - gtin</li>
    <li>0 - item id</li>
    </ul>
    </div>
     *
     * @param line One record from the feed file
     * @param filterRequest Container for capturing the filter parameters
     * @return boolean Indicates whether any filters apply to this record
     */
    protected boolean evaluate(String[] line, FeedFilterRequest filterRequest) {
        FeedTypeEnum type = FeedTypeEnum.getFeedEnum(filterRequest.getType());
        switch (type) {
            case ITEM:
                return evaluateField(line, filterRequest.getLeafCategoryIds(), 4)
                        && evaluateField(line, filterRequest.getSellerNames(), 6)
                        && evaluateField(line, filterRequest.getItemLocationCountries(), 21)
                        && evaluateItemPrice(line, filterRequest)
                        && evaluateField(line, filterRequest.getEpids(), 12)
                        && evaluateField(line, filterRequest.getInferredEpids(), 40)
                        && evaluateField(line, filterRequest.getGtins(), 9)
                        && evaluateField(line, filterRequest.getItemIds(), 0);

            case ITEM_SNAPSHOT:
                return evaluateField(line, filterRequest.getLeafCategoryIds(), 5)
                        && evaluateField(line, filterRequest.getSellerNames(), 7)
                        && evaluateField(line, filterRequest.getItemLocationCountries(), 22)
                        && evaluateItemPrice(line, filterRequest)
                        && evaluateField(line, filterRequest.getEpids(), 13)
                        && evaluateField(line, filterRequest.getInferredEpids(), 58)
                        && evaluateField(line, filterRequest.getGtins(), 10)
                        && evaluateField(line, filterRequest.getItemIds(), 0)
                        && checkItemAvailability(line, 1);
            default:
                return false;
        }
    }

    /**
     * <p>
     * Evaluate if the line is a headerline
     * </p>
     *
     * @param line One record from the feed file
     * @return boolean Checks if the line is a header line
     */
    private boolean evaluateHeader(String[] line) {
        return line[0].contains(Constants.ITEM_ID);
    }

    /**
     * <p>
   * Evaluate if the item price based on the inputs - within a range - higher than a limit - lower
   * than given limit
     * </p>
     *
     * @param line One record from the feed file
     * @param filterRequest Container for capturing the filter parameters
     * @return boolean Checks if the price filter applies to this item
     */
    private boolean evaluateItemPrice(String[] line, FeedFilterRequest filterRequest) {

        boolean isValid = true;

        if (filterRequest == null
                || (filterRequest.getPriceLowerLimit() == null && filterRequest.getPriceUpperLimit() == null)) 
            return isValid;

        Double price = null;
        int colNo = 15;
        if (filterRequest.getType().equalsIgnoreCase(FeedTypeEnum.ITEM_SNAPSHOT.name())) {
            colNo = 16;
        }

        if (!isColumnValid(colNo, line.length)) 
            return isValid;

        try {
            price = Double.valueOf(line[colNo]);
        } catch (Exception e) {
            return false;
        }

        // between range
        if (filterRequest.getPriceLowerLimit() != null && filterRequest.getPriceUpperLimit() != null) {

            if (price < filterRequest.getPriceLowerLimit() || price > filterRequest.getPriceUpperLimit()) {
                isValid = false;

            }
        } else if (filterRequest.getPriceLowerLimit() != null) {

            if (price < filterRequest.getPriceLowerLimit()) 
                isValid = false;

        } else if (filterRequest.getPriceUpperLimit() != null) {

            if (price > filterRequest.getPriceUpperLimit()) 
                isValid = false;
        }
        return isValid;
    }

    /**
     * <p>
     * Evaluates the presence of field at 'columnNo', is present in the given set.
     *
     * This method is generic for handling filtering on 'set of string' filters.
     * </p>
     *
     * @param line One record from the feed file
     * @param filterSet Set of filters
     * @param column Column to filter on
     * @return
     */
    private boolean evaluateField(String[] line, Set<String> filterSet, Integer columnNo) {

        boolean isValid = true;

        // nothing to evaluate
        if (filterSet == null || filterSet.isEmpty() || columnNo == null)
            return isValid;

        // guard against parsing issues
        if (!isColumnValid(columnNo, line.length)) 
            return isValid;

        String field = line[columnNo];

        if (filterSet != null && !filterSet.isEmpty() && !filterSet.contains(field)) {
            isValid = false;
        }
        return isValid;

    }
    
    
    /**
     * <p>
     * Evaluates the presence of field at 'columnNo', is present in the given set.
     *
     * This method is for skipping UNAVAILABLE items
     * </p>
     *
     * @param line One record from the feed file
     * @param column Column to filter on
     * @return
     */
    private boolean checkItemAvailability(String[] line, Integer columnNo) {
        boolean isValid = true;

        // guard against parsing issues
        if (!isColumnValid(columnNo, line.length)) 
            return isValid;

        String field = line[columnNo];
        
        if(columnNo == 1 && field.equalsIgnoreCase("UNAVAILABLE"))
            isValid = false;
        
        return isValid;

    }

    /**
     * <p>
     * Generate filtered file name based on base file path and timestamp
     * </p>
     *
     * @param baseFilePath Path of the unzipped file
     * @param filterRequest Container for capturing the filter parameters
     * @return
     */
    private String getFilteredFileName(Path baseFilePath, FeedFilterRequest filterRequest) {
        Date d = new Date();
        String filteredFile = baseFilePath.toString() + "-filtered-" + d.getTime();
        return filteredFile;
    }

    /**
     * <p>
     * Calculate leaf categories if level two or level three category filters have been specified
     *
     * </p>
     *
     * @param filterRequest Container for capturing the filter parameters
     * @return Set of leaf categories
     */
    public Set<String> calculateLeaves(FeedFilterRequest filterRequest) {

        Set<String> leafCategories = new HashSet<>();
        // return null of level two and level three categories are absent.
        // no need to invoke taxonomy
        if (isLevelNAbsent(filterRequest))
            return leafCategories;

        Taxonomy taxonomy = new TaxonomyImpl();

    String categoryTreeId =
        taxonomy.getCategoryTreeId(filterRequest.getToken(), filterRequest.getMarketplace());

        if (categoryTreeId == null) {
            LOGGER.debug("Could not load category tree id. Cannot proceed...");
            return null;
        }

    CategoryResponse categoryResponse =
        taxonomy.loadLeafCategories(filterRequest.getToken(), categoryTreeId,
                        filterRequest.getLevelOneCategory());

        Set<String> levelTwoCats = filterRequest.getLevelTwoCategories();
        Set<String> levelThreeCats = filterRequest.getLevelThreeCategories();

        if (levelTwoCats != null && !levelTwoCats.isEmpty()) 
            leafCategories.addAll(getLeafCategories(categoryResponse.getLevelTwoCategoryLeaves(),
                    levelTwoCats));

        if (levelThreeCats != null && !levelThreeCats.isEmpty()) 
            leafCategories.addAll(getLeafCategories(categoryResponse.getLevelThreeCategoryLeaves(),
                    levelThreeCats));

        return leafCategories;
    }

    /**
     * <p>
     * Get map of leaf categories
     * </p>
     *
     * @param map Map of level one to children categories
     * @param catSet Set of leaf categories
     * @return Set of leaf categories
     */
    private Set<String> getLeafCategories(Map<String, Set<String>> map, Set<String> catSet) {
        Set<String> leafCategorySet = new HashSet<>();
        for (String cat : catSet) {
            leafCategorySet.addAll(map.get(cat));
        }
        return leafCategorySet;
    }

    /**
     *
     * @param colNo Column number from the feed file
     * @param len Total number of columns
     * @return boolean Indicates whether the column number is valid
     */
    private boolean isColumnValid(int colNo, int len) {
        return colNo < len;
    }

    /**
   * Taxonomy invocation required only if l2 or l3 categories are present Read this method as Level
   * 'n' absent, to signify level 2, or 3 (maybe more levels in the future)
     *
   * @return boolean - Checks presence of l2 and l3 categories. If present, returns false. If both
   *         are empty/null, returns true
     */
    private boolean isLevelNAbsent(FeedFilterRequest filterRequest) {
        return filterRequest == null
                || ((filterRequest.getLevelThreeCategories() == null || filterRequest
                .getLevelThreeCategories().isEmpty()) && (filterRequest.getLevelTwoCategories() == null || filterRequest
                .getLevelTwoCategories().isEmpty()));
    }
}
