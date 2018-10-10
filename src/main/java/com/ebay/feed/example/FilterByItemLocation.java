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

package com.ebay.feed.example;

import java.util.HashSet;
import java.util.Set;
import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Example showing how to download and filter feed files based on item location. The download
 * location is default - current working directory <br>
 * The filtering is performed on the unzipped file. <br>
 * So the sequence of events that are followed is :- <br>
 * - Download feed file <br>
 * - Unzip feed file <br>
 * - Filter feed file
 * </p>
 * 
 * @author shanganesh
 *
 */
public class FilterByItemLocation {

  // oauth token - Bearer xxx
  static String token = Constants.TOKEN_BEARER_PREFIX + "v^1.1#i^1#p^1#r^0#f^0#I^3#t^H4sIAAAAAAAAAOVYbWwURRju9QsPbFFQIKXquQg/bHZvdq93t7f2Tq+0hIv9gpaLQCrO7s62a+92153dtgcYa0kJ/hAS0YCipEQSxWhEoQawGsV/xoAa9ZeAGlOrIoGYKNE/zt4d5VoJHPSITbxccpmZd9553vd53vk4MFDuvn/ryq1/VrhmFQ8PgIFil4udA9zlZTWVJcVVZUUgx8A1PHDfQOlgyXgdhsmEIaxG2NA1jDz9yYSGhXRnmLJNTdAhVrGgwSTCgiUJ7dHmJoFjgGCYuqVLeoLyxBrClKhAPytJQOZhkIWwlvRql3x26GHKJyqiiDjEhWSe9wdFMo6xjWIatqBmhSkOsDzNAvLt4IDA+gWWY3y+wDrKE0cmVnWNmDCAiqThCum5Zg7Wq0OFGCPTIk6oSCy6or01GmtobOmo8+b4imTz0G5By8aTW8t1GXniMGGjqy+D09ZCuy1JCGPKG8msMNmpEL0E5gbgZ1It+TguWMtynJ8TEeILksoVupmE1tVxOD2qTCtpUwFplmqlrpVRkg3xcSRZ2VYLcRFr8Dg/q2yYUBUVmWGqsT66NtrWRkUwQlY37LFp3GMnoQl76bbVDbTCBTgkI8DSIq/IQRAIZhfKeMumecpKy3VNVp2kYU+LbtUjghpNzQ3IyQ0xatVazahiOYhy7bhLOeT4dQ6pGRZtq1tzeEVJkghPunltBiZmW5apiraFJjxMHUinKExBw1BlaupgWotZ+fTjMNVtWYbg9fb19TF9PkY3u7wcAKz3keamdqkbJSFFbJ1az9ir155Aq+lQJERmYlWwUgbB0k+0SgBoXVSECwV5LpTN+2RYkam9/+rIidk7uSIKVSG8ovj9tUHABURZgTwqRIVEsiL1OjiQCFM0EWgPsowElBAtEZ3ZSWSqsuDzK5yPVxAtB0IKXRtSFFr0ywGaVRACCImiFOL/T4WSr9TbJd1AbXpClVIFEXzBxO4z5TZoWqk2EyUJC/Um1GQk5yv+K8aKnVhvXpROrd9IpI4PTJxAQ2UciTOSnvTqkOxtTteGNGpPPkZe0U4xXTbCFkEhk+Ml70kq0QhDKkXOf0qmDkkA06IkahixZNK2oJhAscJsuf/RdnvF8FRyIZlRMRE2M7SqcuYmwaS5ZXCvxJgI67ZJLlFMq3Owdug9SCPblGXqiQQy4+y0iZ5h/F7nbj4JNKn1r/KOvXDXiZmkbSmhEgltmGmRTYfV/OsaWjMratbvC3GA87PctOJanua0I3Uzj8kbCW+ljq38D//ruPl6J7/DI0XpDzvoGgGDrnfIUx54wVJ2Cbi3vGRNacmtVZiclIwKFQarXRp5XpqI6UEpA6pmcblrffXBAxtyXv7DnWDRxNvfXcLOyfkjAFRfHilj5y6sYHkWsIBc7AiH68CSy6Ol7ILSO3a994ltzP/068C+LZtLvj31V/Wzi7eAigkjl6usqHTQVXTmO/fhRW8pCysH7x6Jf1C9beSH50+vXtMzNuI5c6hj64s130f7Ws/owxc6qfXBl491PbasZmB0fXR2fMz929m7dnY/M++JB2Jvtwx8jpU/4q3PbRkzXl/2YNMvJ88+WrHiQnWi6WLtS76wJu+v07fze+0n6wPuxl0n3+zqferp8dsu9guHfhw9+0p0XJ1V+mHl7k2Hzjcf2H7y2E/fHB2qPH5hz99LX/W/wSy751yykoZH4ieOz97ov3Pc+/EXh49s3HfLa71DTXUHX4jvXriqedO2xv3vD1V9dBp0ujd/dhR4/Ds7R/euvf33msU/fznWeP6hsqEFe+Y9/GsLmv/u3N4Tq04dPcdsrxrFO5oXZej7B0TNR6yTEQAA";

  // init feed
  static Feed feed = new FeedImpl();

  private static final String CATEGORY = "11116";

  // TODO : Check if the date is within 14 days, before making the call
  private static final String DATE = "20180708";
  private static final String SCOPE = "ALL_ACTIVE";
  private static final String MKT = "EBAY-US";
  
  public static void main(String[] args) {

    // create request
    FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
    builder.categoryId(CATEGORY).date(DATE).feedScope(SCOPE).siteId(MKT).token(token)
        .type(FeedTypeEnum.ITEM);

    // using null for download directory - defaults to current working directory
    GetFeedResponse getFeedResponse = feed.get(builder.build(), null);

    // 0 denotes successful response
    if (getFeedResponse.getStatusCode() != 0) {
      System.out.println("Exception in downloading feed. Cannot proceed");
      return;
    }
    // unzip
    Response unzipOpResponse = feed.unzip(getFeedResponse.getFilePath());

    if (unzipOpResponse.getStatusCode() != 0) {
      System.out.println("Exception in unzipping feed. Cannot proceed");
      return;
    }

    // filter
    FeedFilterRequest filterRequest = new FeedFilterRequest();
    filterRequest.setToken(token);
    filterRequest.setItemLocationCountries(getItemLocationSet());
    
    // set input file
    filterRequest.setInputFilePath(unzipOpResponse.getFilePath());
    
    Response response = feed.filter(filterRequest);

    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());


  }

  /**
   * Get the set of item locations to filter on
   * 
   * @return
   */
  private static Set<String> getItemLocationSet() {
    Set<String> itemLocationSet = new HashSet<>();
    itemLocationSet.add("CN");
    return itemLocationSet;
  }

}
