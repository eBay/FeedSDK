[![Build Status](https://travis-ci.org/eBay/FeedSDK.svg?branch=master)](https://travis-ci.org/eBay/FeedSDK)

Feed SDK
==========
Java SDK for downloading and filtering item and snapshot feed files

Table of contents
==========
* [Summary](#summary)
* [Setup](#setup)
    - [Add as maven dependency](#add-as-maven-dependency)
    - [Setting up in the local environment](#setting-up-in-the-local-environment)
* [Downloading feed files](#downloading-feed-files)
    - [Customizing download location](#customizing-download-location)
* [Unzipping feed files](#unzipping-feed-files)
* [Filtering feed files](#filtering-feed-files)
    - [Available filters](#available-filters)
    - [Combining filter criteria](#combining-filter-criteria)
    - [Filtering by level two category id](#filtering-by-level-two-category-id)
    - [Filtering by level three category id](#filtering-by-level-three-category-id)
* [Schemas](#schemas)
    - [GetFeedResponse](#getfeedresponse)
    - [Response](#response)
* [Logging](#logging)
    - [Customizing log file location](#customizing-log-file-location)
* [Usage](#usage)
    - [Using command line options](#using-command-line-options)
    - [Using config file driven approach](#using-config-file-driven-approach)
    - [Using java method calls](#using-java-method-calls)
        - [Code samples](#examples)
* [Performance](#performance)
* [Important notes](#important-notes)
* [License](#license)

# Summary

The item and snapshot feed files provide a rich set of data regarding active ebay listings. The feed files for any supported marketplace can be downloaded through the feed API.

However, since the volume of data is large, the download operation is performed in chunks, which may be cumbersome. 

The feed SDK abstracts all the complexities and provides a very simple interface to -
* [Download](#downloading-feed-files) 
* [Unzip](#unzipping-feed-files)
* [Filter](#filtering-feed-files)

# Setup

The SDK can be added as a maven dependency or the entire repository can be cloned/forked and changes can be made.

You are most welcome to collaborate and enhance the existing code base.

## Add as maven dependency

```
<!--  -->
<dependency>
    <groupId>com.ebay.api</groupId>
    <artifactId>feed-sdk</artifactId>
    <version>1.1.0-RELEASE</version>
</dependency>
```

## Setting up in the local environment

For setting up the project in your local environment
* Clone the repository
* Run mvn clean install. 
This should generate an uber jar with the following naming convention

__feed-sdk-{version}-uber.jar__

## Downloading feed files
Since the feed API supports feed files which may be as big as 5 GB, there is a capability which was built into the system to download the file in chunks of 100 MB and have resume capability in a case if token get expired.

The SDK abstracts the complexity involved in calculating the request header '__range__' based on the response header '__content-range__'.

To download a feed file which is -
* __bootstrap__ : (feed_scope = ALL_ACTIVE)
* __L1 category 1__ : (category_id = 1)
* __marketplace US__ : (X-EBAY-C-MARKETPLACE-ID: EBAY_US)

```
FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
builder.feedScope("ALL_ACTIVE")
	.categoryId("1")
 	.siteId("EBAY_US")
	.token(<TOKEN>)
	.type("ITEM");

Feed feed = new FeedImpl();

GetFeedResponse response = feed.get(builder.build(), null, null); 
 
```

### Credential File Location

The default download location is the current working directory.
The credential config file location can be changed by specifying the optional 'credentialConfigFilePath' parameter in the feed method.
For example, credentials.yaml location __sample-credentials/credentials.yaml__ -  

```

feed.get(builder.build(), null, "sample-credentials/credentials.yaml");
```
The __GetFeedResponse.filePath__ denotes the location where the file was downloaded.

### Customizing download location

The default download location is the current working directory.
The download location can be changed by specifying the optional 'downloadDirectory' parameter in the feed method.
For example, to download to the location __/tmp/feed__ - 

```

feed.get(builder.build(), "/tmp/feed");
```
---

## Unzipping feed files

Since the feed file is gzipped, it needs to be unzipped before post processing tasks can be run on it.

To unzip a downloaded feed file - 
```
Feed feed = new FeedImpl();
String unzippedFilePath = feed.unzip(filePath)

```
---
## Filtering feed files

### Available filters
The SDK provides the capability to filter the feed files based on :-
* List of leaf category ids
* List of seller usernames
* List of item locations
* Price range
* Level two Categories (the token scope should include api_scope and buy.item.feed scope)
* Level three Categories (the token scope should include api_scope and buy.item.feed scope)
* Item IDs
* EPIDs
* GTINs

On successful completion of a filter operation, a new __filtered__ file is created.

To filter a feed file on leaf category ids would require these 3 lines of code - 
```
FeedFilterRequest filterRequest = new FeedFilterRequest();
filterRequest.setLeafCategoryIds(<Set of leaf category ids>);
Response response = feed.filter(<path to unzipped feed file>, filterRequest);

```
The __Response.filePath__ denotes the location of the filtered file.

### Combining filter criteria

The SDK provides the freedom to combine the filter criteria.

To filter on leaf category ids and seller user names for listings in the price range of 1 to 100
```
FeedFilterRequest filterRequest = new FeedFilterRequest();

filterRequest.setLeafCategoryIds(<Set of leaf category IDs>);
filterRequest.setSellerNames(<Set of seller user names>);
filterRequest.setPriceLowerLimit(1);
filterRequest.setPriceUpperLimit(100);

Response response = feed.filter(<path to unzipped feed file>, filterRequest);
```

### Filtering by level two category id

```
filterRequest.setLevelTwoCategories(<Set of level two category ids>);
Response response = feed.filter(<path to unzipped feed file>, filterRequest);
```

### Filtering by level three category id

```
filterRequest.setLevelThreeCategories(<Set of level three category ids>);
Response response = feed.filter(<path to unzipped feed file>, filterRequest);
```
---
### Schemas
This section provides more detail on what information is contained within the instances that are returned from the SDK method calls.

### GetFeedResponse

The ApiResponse instance is returned from the Feed.get() method.

```
  private Integer statusCode;
  private String message;
  private String filePath;
  List<ErrorData> errors;

```

| Field name | Description 
|---|---|
| statusCode | Integer: 0 indicates a successful response. Any non zero value indicates an error
| message | String: Detailed information on the status
| filePath | String: Absolute path of the location of the resulting file
| errors | List<ErrorData>: Detailed error information


### Response 

This Response instance is returned from Feed.unzip and Feed.filter methods.

```
  private Integer statusCode;
  private String message;
  private String filePath;
```
| Field name | Description 
|---|---|
| statusCode | Integer: 0 indicates a successful response. Any non zero value indicates an error
| message | String: Detailed information on the status
| filePath | String: Absolute path of the location of the resulting file

---
## Logging

Logging is configured using slf4j and rolling log files are created in the current directory.

__Ensure that appropriate permissions are present to write to the directory__

* The current log file name is : feed-sdk-log.log
* Rolling Log files are created per day with the pattern : feed-sdk-log.{yyyy-MM-dd}.log
* Log files are retained for a period of 7 days
* Total log file size is capped at 100 MB
* Individual log file size is capped at 3 MB, at which new file will be created

### Customizing log file location

The log file location can be changed overriding the LOG_HOME variable, and passing as VM argument.
For example, to change the log file location to __/var/log__
```
-DLOG_HOME="/var/log"
```
---
## Usage

The following sections describe the different ways in which the SDK can be used

### Using command line options

All the capabilities of the SDK can be invoked using the command line.

To see the available options and filters , use '-help'
```
java -jar feed-sdk-1.1.0-RELEASE-uber.jar -help
usage: Feed SDK CLI
 -help                             display help
 -dt <arg>                         the date when feed file was generated
 -sdt <arg>                        the snapshot_date when feed file was generated
 -c1 <arg>                         the l1 category id of the feed file
 -scope <arg>                      the feed scope. Available scopes are
                                   ALL_ACTIVE or NEWLY_LISTED
 -mkt <arg>                        the marketplace id for which feed is
                                   being request. For example - EBAY_US
 -token <arg>                      the oauth token for the consumer. Omit
                                   the word 'Bearer'
 -c2f <arg>                        list of l2 categories which are used to
                                   filter the feed
 -c3f <arg>                        list of l3 categories which are used to
                                   filter the feed
 -lf <arg>                         list of leaf categories which are used
                                   to filter the feed
 -sellerf <arg>                    list of seller names which are used to
                                   filter the feed
 -locf <arg>                       list of item locations which are used
                                   to filter the feed
 -pricelf <arg>                    lower limit of the price range for
                                   items in the feed
 -priceuf <arg>                    upper limit of the price range for
                                   items in the feed
 -epidf <arg>                      list of epids which are used to filter
                                   the feed
 -iepidf <arg>                     list of inferred epids which are used
                                   to filter the feed
 -gtinf <arg>                      list of gtins which are used to filter
                                   the feed
 -itemf <arg>                      list of item ids which are used to
                                   filter the feed
 -dl,--downloadlocation <arg>      override for changing the directory
                                   where files are downloaded
 -cl,--credentiallocation <arg>    directory where the credentials file is
                                   located
 -authscopes,--oauthscopes <arg>   list of scopes
 -env <arg>   					   Environment Type. Supported Environments are SANDBOX and PRODUCTION
 -type <arg>   					   Supported feed types are ITEM and ITEM_SNAPSHOT
 

```

For example, to use the command line options to download, unzip and filter feed files

Using token For Item
```
java -jar feed-sdk-1.1.0-SNAPSHOT-uber.jar -dt 20180701 -type ITEM -c1 1281 -scope ALL_ACTIVE -lf 46310 177789 -mkt EBAY_US -c3f 177792 116381 -pricelf 2 -priceuf 100 -locf US CN -token xxx
```

Using token For ITEM_SNAPSHOT 
```
java -jar feed-sdk-1.1.0-RELEASE-uber.jar -sdt 2021-06-10T02:00:00.000Z -type ITEM_SNAPSHOT -env SANDBOX -c1 625 -mkt EBAY_US -pricelf 2 -priceuf 100 -locf US CN -token xxx
```

Using credentials file
```
java -jar feed-sdk-1.1.0-RELEASE-uber.jar -dt 20180801 -c1 11700 -scope ALL_ACTIVE -mkt EBAY_US -pricelf 2 -priceuf 100 -locf US CN -cl <absolute path of credentials file>
```

### Using config file driven approach

All the capabilities of the SDK can be leveraged via a config file.
The feed file download and filter parameters can be specified in the config file for multiple files, and SDK will process them sequentially.

The structure of the config file

```
{
	"requests": [{
		"feedRequest": {
			"categoryId": "1",
			"marketplaceId": "EBAY_US",
			"date": "20180708",
			"feedScope": "ALL_ACTIVE",
			"type": "ITEM"
		},
		"filterRequest": {
			"leafCategoryIds": ["11675", "3226"],
			"itemLocationCountries": ["US", "CN"],
			"priceLowerLimit": 10.0,
			"priceUpperLimit": 100.0
		},
		"levelThreeCategories": ["1313", "13583", "39"]
	}]
}

```
An example of using the SDK through a config file is located at 

[Config file based approach example - ConfigFileBasedExample.java](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/ConfigFileBasedExample.java)

[Example config file - 1](https://github.com/eBay/FeedSDK/tree/1.1.0/sample-config/config-file-download-unzip-filter)

[Example config file - 2](https://github.com/eBay/FeedSDK/tree/1.1.0/sample-config/config-file-filter)

### Using java method calls

Samples showing the usage of available operations and filters.

#### Examples

All the examples are located [__here__](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example)

* [Filter by item location](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByItemLocation.java)
* [Filter by leaf categories](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByLeafCategories.java)
* [Filter by price](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByPrice.java)
* [Filter by seller user names](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterBySellerUserNames.java)
* [Combining multiple filters](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/CombiningFilters.java)
* [Filter on level three category](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByLevelThreeCategory.java)
* [Filter on EPID](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByEpids.java)
* [Filter on GTIN](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByGtins.java)
* [Filter on item IDs](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByItemIds.java)
* [Filter by env](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FilterByEnv.java)
* [Feed type item_snapshot](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/FeedTypeItemSnapShot.java)
* [Multithreading usage MutliThreading](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/UsingMutliThread.java)
* [Credential file usage 'Credential'](https://github.com/eBay/FeedSDK/blob/1.1.0/src/main/java/com/ebay/feed/example/UsingCredentialFileForSandBoxEnv.java)


---
## Performance

|  Category | Type  | Size gz  |  Size unzipped |  Records | Applied Filters | Filter Time | Total Time
|---|---|---|---|---|---|---|---|
| 1 | BOOTSTRAP | 570 MB | 2.3 GB | 2.6 Million |  l3 categories, sellers | ~ 20 seconds | ~ 2 minutes
| 11450 | BOOTSTRAP | 3.87 GB | 65 GB | 47 Million | categories | ~ 2 mins | ~ 25 minutes
| 293 | BOOTSTRAP | 530 MB | 3 GB | 3 Million | sellers, item locations, price | ~ 30 seconds | ~ 1.5 minutes
| 1281 | BOOTSTRAP | 121 MB | 1.1 GB | 900,000 | leaf categories, l3 categories, item locations, price | ~ 10 seconds | ~ 30 seconds
| 11232 | BOOTSTRAP | 128 MB | 670 MB | 600,000 | leaf categories, item locations, price | ~ 10 seconds | ~ 30 seconds
| 220 | BOOTSTRAP | 700 MB | 3.5 GB | 5.5 Million | leaf categories, item locations, price | ~ 30 seconds | ~ 2 minutes

---
## Important notes 

* Ensure there is enough storage for unzipped files
* Ensure that the log and file storage directories have appropriate write permissions
* In case of failure in downloading due to network issues, the process needs to start again. There is no capability at the moment, to resume.
* The credentials need to be set in the credentials.yml to avail resume capability.

## License

Copyright 2018 eBay Inc.
Developer: Shankar Ganesh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
