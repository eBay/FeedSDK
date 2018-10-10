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

import com.ebay.feed.api.Feed;
import com.ebay.feed.api.FeedImpl;
import com.ebay.feed.constants.Constants;
import com.ebay.feed.enums.EnvTypeEnum;
import com.ebay.feed.enums.FeedTypeEnum;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.feed.FeedRequest.FeedRequestBuilder;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <p>
 * Example showing how to download and filter feed files based on environment. The download location
 * is default - current working directory <br>
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
public class FilterByEnv {


  // oauth token
  static String TOKEN = Constants.TOKEN_BEARER_PREFIX + "v^1.1#i^1#I^3#f^0#p^1#r^0#t^H4sIAAAAAAAAAOVXbWwURRju9ctULGAEKUXwskVRcPdmd+9z7R1eP6CX9Euv1AIanNuda9fu7V52ZmnPxKS5KD+MhpTgD6OBSsFIImggjdRA0hKMCIqJMZHwR0VNiOEHMYrEYOPs9lqu1UBbSiTx/lxm5p13nveZ5913XtBXWrZ2R8OOP8pd9xQO9IG+QpeLXwDKSkvWLSwqrCwpAHkGroG+1X3F2aJL1RimtLT0DMJpQ8fI3ZvSdCw5k2HGMnXJgFjFkg5TCEtEluLRpkZJ4ICUNg1iyIbGuGN1YQYhOcQHfQm/jETR76OT+oTLNiPMKAFF8fJKIgEDIRH5k3QdYwvFdEygTsKMAPggywMWhNoEIHlFCQhcyOffwrjbkYlVQ6cmHGAiDlrJ2WvmQb05UogxMgl1wkRi0Q3xlmisrr65rdqT5yuSoyFOILHw1FGtoSB3O9QsdPNjsGMtxS1ZRhgznsj4CVOdStEJMHOA7zAtCrJf9oYEkIR+EQbnhckNhpmC5OYw7BlVYZOOqYR0opLMrQilZCReRDLJjZqpi1id2/572oKamlSRGWbqa6Kbo62tTCSOEOmCGmRxt5WCJtzOxms62JDgF5CCAM+KsjfIBxRv7qBxbzmWp51Ua+iKanOG3c0GqUEUNZrOjZjHDTVq0VvMaJLYiPLthAkOvdTOM3GJFunS7WtFKUqE2xne+gYmdxNiqgmLoEkP0xccisIMTKdVhZm+6Egxp55eHGa6CElLHk9PTw/XI3KG2ekRAOA9HU2NcbkLpSDj2Nq5bturt97Aqk4oMqI7sSqRTJpi6aVSpQD0TiYigoDPG8zxPhVWZPrsPybyYvZMTYj5ShBegYrXBwIBEAyIfl6cjwyJ5ETqsXGgBMywVKDdiKQ1KCNWpjqzUshUFUn0JQUxmESs4g8lWW8omWQTPsXP8kmEAEKJhBwK/p8SZaZSj8tGGrUamipn5knw8yV2U2mFJsnUWBk6jiNNo38z1f6/hortUO9okHauzzpQ2wemTmBa5WyFc7KR8hiQftrsqW0OavdMjDwJK8N1WggTCluh1WXGm1QqEY4mijLzLeNpSAOY+Rb6cFEsmczpICffOcqk2tlF8KzO7J0DKZjKjdOMThUTVcZc2lKM25KeSt8ld1V20aDHo1eV8RcF51DA4e0yZyJsWCZ9S3EtdoFtM7qRTj9XxDQ0DZnt/G0xMZ+l9T8pqxPj4mzhxSmRyZpKqdx2t0U3y5o1R31DcndFzfvEEM8HAkHhtuKqde60LXOH68asw2swMEHKHXgJeqZ2pZEC58dnXSdA1jVMG1sQACy/DjxeWrSpuOg+BtPSwWGoKwmjl1NhkqMfaJ12XSbiulEmDVWzsNS1dcUv66/n9cMDz4OKyY64rIhfkNceg4durJTwi5aV80EeANp1eUUgbAFVN1aL+QeLlxxNfe99Pb47+s6bMa3l+Jfrr3VoS0H5pJHLVVJQnHUVCOs6T+6S9p4+cFJqW/jjaxc+NRuvPPzsqf7LaEfd8gLvxqovTgQaP7peXXflsUOjL7v6hmvIr8NNR47vWXv1tFS1dGynd+XQc08ePle2s2Lou+aflfbBD/9U3E8NDn79asmpg0+cTZ/ftXHxsuySjRcq61cdOwKOLzmQ+fiD7NV3y0MH9n1Vc/Fc1UHUUHH40BiWz5wZHXupdjRRLsqRRZWPSNGz/fq1DY+WD27dw1x6e7D6laHasWzFwcLF7684Ovq7//zw5uWVJfv3vTdivhEead07ADbBH75ds/9y0zenVrnP3f/Ab5+zDS8MuX/q8PT29x+9d7DMfOvYyF/Jz1bG1rgbLwojq3tG4Sfj1/g3gomGnKkQAAA=";

  // init feed
  static Feed feed = new FeedImpl();

  private static final String CATEGORY = "15032";

  // TODO : Check if the date is within 14 days, before making the call
  private static final String DATE = "20181005";
  private static final String SCOPE = "ALL_ACTIVE";
  private static final String MKT = "EBAY-US";
  
  public static void main(String[] args) {

    // create request
    FeedRequest.FeedRequestBuilder builder = new FeedRequestBuilder();
    builder.categoryId(CATEGORY).date(DATE).feedScope(SCOPE).siteId(MKT).token(TOKEN)
        .type(FeedTypeEnum.ITEM).env(EnvTypeEnum.SANDBOX.name());

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

    // set price lower and upper lomit
    filterRequest.setPriceLowerLimit(10.0);
    filterRequest.setPriceLowerLimit(20.0);
    // set input file
    filterRequest.setInputFilePath(unzipOpResponse.getFilePath());
    
    Response response = feed.filter(filterRequest);
    System.out.println("Filter status = " + response.getStatusCode());
    System.out.println("Filtered file = " + response.getFilePath());

  }

}
