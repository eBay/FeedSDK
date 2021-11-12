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
package com.ebay.feed.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.auth.CredentialLoader;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;
import com.ebay.feed.model.oauth.AuthRequest;

/**
 * <p>
 * Wrapper for exposing feed sdk capabilities using CLI
 * </p>
 *
 * @author shanganesh
 *
 */
public class FeedCli {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedCli.class);

    public static void main(String[] args) throws Exception {

        // ***Definition Stage***
        // create Options object
        Options options = new Options();

        options.addOption("help", false, "display help");

        /**
         * *
         * options for downloading the file
         */
        // date
        options.addOption("dt", true, "the date when feed file was generated");

        // snapshot_date
        options.addOption("sdt", true, "the snapshot_date when feed file was generated");

        // l1 category
        options.addOption("c1", true, "the l1 category id of the feed file");

        // scope
        options.addOption("scope", true,
                "the feed scope. Available scopes are ALL_ACTIVE or NEWLY_LISTED");

        // marketplace
        options.addOption("mkt", true,
                "the marketplace id for which feed is being request. For example - EBAY_US");

        // token
        options.addOption("token", true, "the oauth token for the consumer. Omit the word 'Bearer'");

        // env
        options.addOption("env", true, "Supported environment types are SANDBOX, PRODUCTION");

        // type
        options.addOption("type", true, "Supported feed types are ITEM, ITEM_SNAPSHOT");

        /**
         * options for filtering the files
         */
        Option filterL2
                = new Option("c2f", true, "list of l2 categories which are used to filter the feed");
        filterL2.hasArgs();
        filterL2.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterL2);

        Option filterL3
                = new Option("c3f", true, "list of l3 categories which are used to filter the feed");
        filterL3.hasArgs();
        filterL3.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterL3);

        Option filterLeaf
                = new Option("lf", true, "list of leaf categories which are used to filter the feed");
        filterLeaf.hasArgs();
        filterLeaf.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterLeaf);

        Option filterSeller
                = new Option("sellerf", true, "list of seller names which are used to filter the feed");
        filterSeller.hasArgs();
        filterSeller.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterSeller);

        Option filterLocation
                = new Option("locf", true, "list of item locations which are used to filter the feed");
        filterLocation.hasArgs();
        filterLocation.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterLocation);

        options.addOption("pricelf", true, "lower limit of the price range for items in the feed");

        options.addOption("priceuf", true, "upper limit of the price range for items in the feed");

        Option filterEpid
                = new Option("epidf", true, "list of epids which are used to filter the feed");
        filterEpid.hasArgs();
        filterEpid.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterEpid);

        Option filterInferredEpid
                = new Option("iepidf", true, "list of inferred epids which are used to filter the feed");
        filterInferredEpid.hasArgs();
        filterInferredEpid.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterInferredEpid);

        Option filterGtin
                = new Option("gtinf", true, "list of gtins which are used to filter the feed");
        filterGtin.hasArgs();
        filterGtin.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterGtin);

        Option filterItem
                = new Option("itemf", true, "list of item ids which are used to filter the feed");
        filterItem.hasArgs();
        filterItem.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(filterItem);

        /**
         * Overrides for file locations
         */
        // file location
        options.addOption("dl", "downloadlocation", true,
                "override for changing the directory where files are downloaded");

        // oauth related
        options.addOption("cl", "credentiallocation", true,
                "directory where the credentials file is located");

        options.addOption("authscopes", "oauthscopes", true, "list of scopes");

        // ***Parsing Stage***
        // Create a parser
        CommandLineParser parser = new DefaultParser();

        // parse the options passed as command line arguments
        CommandLine cmd = parser.parse(options, args);

        // ***Interrogation Stage***
        // hasOptions checks if option is present or not
        // populate feed request
        FeedRequestBuilder builder = new FeedRequestBuilder();

        if (cmd.hasOption("dt")) {
            builder.date(cmd.getOptionValue("dt"));
        }

        if (cmd.hasOption("sdt")) {
            builder.snapshotDate(cmd.getOptionValue("sdt"));
        }

        if (cmd.hasOption("c1")) {
            builder.categoryId(cmd.getOptionValue("c1"));
        }

        if (cmd.hasOption("mkt")) {
            builder.siteId(cmd.getOptionValue("mkt"));
        }

        if (cmd.hasOption("scope")) {
            builder.feedScope(cmd.getOptionValue("scope"));
        }

        if (cmd.hasOption("token")) {
            builder.token("Bearer " + cmd.getOptionValue("token"));
        }

        if (cmd.hasOption("env")) {
            builder.env(cmd.getOptionValue("env"));
        }

        if (cmd.hasOption("type")) {
            builder.type(cmd.getOptionValue("type"));
        } else {
            // Hardcoded item for backword compatibilty
            builder.type("item");
        }
        FeedRequest feedRequest = builder.build();

        // populate filter request if available
        FeedFilterRequest filterRequest = new FeedFilterRequest();

        // populate auth request if available
        String credentialFile = null;
        String optionalDownloadPath = null;
        List<String> scopes = null;

        LOGGER.info(cmd.getOptionValue("lf"));
        if (cmd.hasOption("lf")) {
            filterRequest
                    .setLeafCategoryIds(new HashSet<String>(Arrays.asList(cmd.getOptionValues("lf"))));
        }

        if (cmd.hasOption("c3f")) {
            filterRequest.setLevelThreeCategories(new HashSet<String>(Arrays.asList(cmd
                    .getOptionValues("c3f"))));
        }

        if (cmd.hasOption("c2f")) {
            filterRequest.setLevelTwoCategories(new HashSet<String>(Arrays.asList(cmd
                    .getOptionValues("c3f"))));
        }

        if (cmd.hasOption("sellerf")) {
            filterRequest.setSellerNames(new HashSet<String>(
                    Arrays.asList(cmd.getOptionValues("sellerf"))));
        }

        if (cmd.hasOption("locf")) {
            filterRequest.setItemLocationCountries(new HashSet<String>(Arrays.asList(cmd
                    .getOptionValues("locf"))));
        }

        if (cmd.hasOption("pricelf")) {
            filterRequest.setPriceLowerLimit(Double.valueOf(cmd.getOptionValue("pricelf")));
        }

        if (cmd.hasOption("priceuf")) {
            filterRequest.setPriceUpperLimit(Double.valueOf(cmd.getOptionValue("priceuf")));
        }

        if (cmd.hasOption("epidf")) {
            filterRequest.setEpids(new HashSet<String>(Arrays.asList(cmd.getOptionValues("epidf"))));
        }

        if (cmd.hasOption("iepidf")) {
            filterRequest.setInferredEpids(new HashSet<String>(Arrays.asList(cmd
                    .getOptionValues("iepidf"))));
        }

        if (cmd.hasOption("gtinf")) {
            filterRequest.setGtins(new HashSet<String>(Arrays.asList(cmd.getOptionValues("gtinf"))));
        }

        if (cmd.hasOption("itemf")) {
            filterRequest.setItemIds(new HashSet<String>(Arrays.asList(cmd.getOptionValues("itemf"))));
        }

        if (cmd.hasOption("c1")) {
            filterRequest.setLevelOneCategory(cmd.getOptionValue("c1"));
        }

        if (cmd.hasOption("token")) {
            filterRequest.setToken("Bearer " + cmd.getOptionValue("token"));
        }

        if (cmd.hasOption("mkt")) {
            filterRequest.setMarketplace(cmd.getOptionValue("mkt"));
        }
        
        if (cmd.hasOption("type")) {
            filterRequest.setType(cmd.getOptionValue("type"));
        }

        if (cmd.hasOption("cl")) {
            credentialFile = cmd.getOptionValue("cl");
        }

        if (cmd.hasOption("dl")) {
            optionalDownloadPath = cmd.getOptionValue("dl");
        }

        if (cmd.hasOption("authscopes")) {
            scopes = new ArrayList<String>(Arrays.asList(cmd.getOptionValues("authscopes")));
        }

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setOptionComparator(null);
            formatter.printHelp("Feed SDK CLI", options);
        } else if (cmd.getOptions().length > 0) {

            // create auth request
            AuthRequest authRequest = new AuthRequest(credentialFile, scopes);

            // start processing
            start(feedRequest, filterRequest, authRequest, optionalDownloadPath);
        }
    }

    /**
     * <p>
     * Begin the process of downloading and filtering feed based on the input
     * parameters
     * </p>
     *
     * @param feedRequest
     * @param filterRequest
     * @throws Exception
     */
    private static void start(FeedRequest feedRequest, FeedFilterRequest filterRequest,
            AuthRequest authRequest, String optionalDownloadPath) throws Exception {

        Feed feed = new FeedImpl();

        // if token is null, then retrieve from oauth lib and set in request
        if (feedRequest.getToken() == null) {
            CredentialLoader credentialLoader = new CredentialLoader(authRequest);
            credentialLoader.loadCredentials();
            feedRequest.setToken(credentialLoader.getOauthResponse().getAccessToken().get().getToken());
            filterRequest.setToken(credentialLoader.getOauthResponse().getAccessToken().get().getToken());
        }

        // download feed
        GetFeedResponse getFeedResponse = feed.get(feedRequest, optionalDownloadPath);

        if (getFeedResponse.getStatusCode() != 0) {
            return;
        }

        // unzip
        Response unzipResponse = feed.unzip(getFeedResponse.getFilePath());

        if (unzipResponse.getStatusCode() != 0) {
            return;
        }

        // filter
        filterRequest.setInputFilePath(unzipResponse.getFilePath());
        Response filterResponse = feed.filter(filterRequest);
        LOGGER.info("Filter response = " + filterResponse.toString());
    }

}
