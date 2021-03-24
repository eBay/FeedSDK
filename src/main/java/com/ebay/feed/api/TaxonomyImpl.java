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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.model.feed.operation.taxonomy.CategoryResponse;
import com.ebay.feed.model.taxonomy.CategorySubtreeNode;
import com.ebay.feed.model.taxonomy.TaxonomyResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * <div>
 * Taxonomy implementation which consists of the following capabilities
 * <ul>
 * <li>
 * <b>getCategoryTreeId</b> - Get the category tree id for the marketplace. For
 * EBAY_US, it returns 0</li>
 * <li>
 * <b>loadLeafCategories</b> - Creates a map between level two categories to
 * leaf categories, and level three categories to leaf categories. Useful when
 * filtering on level two or level three categories</li>
 *
 * </ul>
 * </div>
 *
 * @author shanganesh
 *
 */
public class TaxonomyImpl implements Taxonomy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyImpl.class);
    private OkHttpClient client = null;
    private Gson gson = null;

    public TaxonomyImpl() {

        client
                = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

        gson = new Gson();
    }


    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.ITaxonomy#getCategoryTreeId(java.lang.String, java.lang.String)
     */
    @Override
    public String getCategoryTreeId(String token, String marketplaceId) {

        LOGGER.debug("Entering Taxonomy.getCategoryTreeId() with marketplaceId = {}", marketplaceId);
        String categoryTreeId = null;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(Constants.TAXONOMY_DEFAULT + marketplaceId);
        requestBuilder.addHeader(Constants.AUTHORIZATION_HEADER, token);

        try ( Response response = client.newCall(requestBuilder.build()).execute()) {

            if (response.isSuccessful()) {
                LOGGER.debug("Response Taxonomy.getCategoryTreeId() with marketplaceId = {}",
                        response.code());
                JsonParser parser = new JsonParser();
                JsonObject o = parser.parse(response.body().string()).getAsJsonObject();
                categoryTreeId = o.get("categoryTreeId").getAsString();
                LOGGER.debug("Obtained category tree id = {}", categoryTreeId);
            } else {
                throw new Exception(response.body().string());
            }
        } catch (Exception e) {
            LOGGER.error("Exception in getCategoryTreeId() {}", e);
        }
        return categoryTreeId;
    }

    /*
   * (non-Javadoc)
   * 
   * @see com.ebay.feed.api.ITaxonomy#loadLeafCategories(java.lang.String, java.lang.String,
   * java.lang.String)
     */
    @Override
    public CategoryResponse loadLeafCategories(String token, String categoryTreeId, String categoryId) {

        LOGGER.debug(
                "Entering Taxonomy.loadLeafCategories() with categoryTreeId = {}, L1 categoryId = {}",
                categoryTreeId, categoryId);

        TaxonomyResponse taxonomyResponse = null;
        CategoryResponse categoryResponse = new CategoryResponse();;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(Constants.TAXONOMY_CATEGORY_SUBTREE_BASE + categoryTreeId
                + Constants.TAXONOMY_CATEGORY_SUBTREE_QUERY + categoryId);
        requestBuilder.addHeader(Constants.AUTHORIZATION_HEADER, token);

        try ( Response response = client.newCall(requestBuilder.build()).execute()) {

            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                taxonomyResponse = gson.fromJson(responseStr, TaxonomyResponse.class);

                // parse and load in map
                LOGGER.debug("Successful taxonomy api response. Loading map...");
                categoryResponse.setLevelThreeCategoryLeaves(getLevelThreeLeafCategories(taxonomyResponse));
                categoryResponse.setLevelTwoCategoryLeaves(getLevelTwoLeafCategories(taxonomyResponse));

                LOGGER.debug(taxonomyResponse.toString());
            } else {
                throw new Exception(response.body().string());
            }
        } catch (Exception e) {
            LOGGER.error("Error loading leaf categories", e);
        }
        return categoryResponse;
    }

    /**
     * <p>
     * Loads the relationship between level three and leaf categories in a map
     * </p>
     *
     * @param taxonomyResponse The response from calling taxonomy API
     * @return Map of string -> set of string. The key represents the level
     * three category, the set of string represents the leaf categories
     * associated with the level three category
     */
    private Map<String, Set<String>> getLevelThreeLeafCategories(TaxonomyResponse taxonomyResponse) {

        Set<String> leafCategories = null;
        Map<String, Set<String>> levelThreeCategoryMap = new HashMap<>();
        try {

            // L1
            CategorySubtreeNode node = taxonomyResponse.getCategorySubtreeNode();

            // L2
            CategorySubtreeNode[] l2Nodes = node.getChildCategoryTreeNodes();
            for (CategorySubtreeNode l2CategoryNode : l2Nodes) {

                if (l2CategoryNode.getLeafCategoryTreeNode() != null
                        && l2CategoryNode.getLeafCategoryTreeNode().equals(Constants.TRUE)) {
                    continue;
                }

                CategorySubtreeNode[] l3Nodes = l2CategoryNode.getChildCategoryTreeNodes();

                for (CategorySubtreeNode l3Node : l3Nodes) {

                    if (l3Node == null) {
                        continue;
                    }

                    LOGGER.debug("l3 category = {}", l3Node.getCategory().getCategoryId());
                    leafCategories = new HashSet<>();
                    getLeaf(l3Node, leafCategories);
                    LOGGER.debug("leaf size = {}", leafCategories.size());
                    LOGGER.debug("leaf categories = {}", leafCategories.toString());
                    levelThreeCategoryMap.put(l3Node.getCategory().getCategoryId(), leafCategories);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exception in getLevelThreeLeafCategories() ", e);
        }

        return levelThreeCategoryMap;
    }

    /**
     * <p>
     * Loads the relationship between level two and leaf categories in a map
     * </p>
     *
     * @param taxonomyResponse The response from calling taxonomy API
     * @return Map of string -> set of string. The key represents the level
     * three category, the set of string represents the leaf categories
     * associated with the level three category
     */
    private Map<String, Set<String>> getLevelTwoLeafCategories(TaxonomyResponse taxonomyResponse) {

        Set<String> leafCategories = null;
        Map<String, Set<String>> levelTwoCategoryMap = new HashMap<>();
        try {

            // L1
            CategorySubtreeNode node = taxonomyResponse.getCategorySubtreeNode();

            // L2
            CategorySubtreeNode[] l2Nodes = node.getChildCategoryTreeNodes();
            for (CategorySubtreeNode l2Node : l2Nodes) {

                if (l2Node == null) {
                    continue;
                }

                LOGGER.debug("l2 category = {}", l2Node.getCategory().getCategoryId());
                leafCategories = new HashSet<>();
                getLeaf(l2Node, leafCategories);
                LOGGER.debug("leaf size = {}", leafCategories.size());
                LOGGER.debug("leaf categories = {}", leafCategories.toString());
                levelTwoCategoryMap.put(l2Node.getCategory().getCategoryId(), leafCategories);
            }

        } catch (Exception e) {
            LOGGER.error("Exception in getLevelTwoLeafCategories() ", e);
        }

        return levelTwoCategoryMap;
    }

    /**
     * <p>
     * Recursively traverses through nodes to find out the leaf node and adds to
     * list
     * </p>
     *
     * @param node Subtree node
     * @return Set of leaf category ids
     */
    private Set<String> getLeaf(CategorySubtreeNode node, Set<String> leafSet) throws Exception {

        try {

            if (node == null) {
                return leafSet;
            }

            if (node != null && node.getLeafCategoryTreeNode() != null
                    && node.getLeafCategoryTreeNode().equals(Constants.TRUE)) {
                leafSet.add(node.getCategory().getCategoryId());
            }

            if (node.getChildCategoryTreeNodes() != null && node.getChildCategoryTreeNodes().length == 0) {
                return leafSet;
            }

            if (node.getLeafCategoryTreeNode() == null
                    || node.getLeafCategoryTreeNode().equals(Constants.FALSE)) {
                for (CategorySubtreeNode category : node.getChildCategoryTreeNodes()) {
                    if (category.getLeafCategoryTreeNode() != null
                            && category.getLeafCategoryTreeNode().equals(Constants.TRUE)) {
                        leafSet.add(category.getCategory().getCategoryId());
                    } else {
                        getLeaf(category, leafSet);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exception in getLeaf()", e);
            throw e;
        }

        return leafSet;
    }

}
