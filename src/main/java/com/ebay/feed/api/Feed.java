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

import java.util.List;
import com.ebay.feed.model.feed.download.GetFeedResponse;
import com.ebay.feed.model.feed.operation.feed.FeedRequest;
import com.ebay.feed.model.feed.operation.filter.FeedFilterRequest;
import com.ebay.feed.model.feed.operation.filter.Response;

/**
 * <div>
 * Feed interface which consists of the core capabilities of the SDK.<br>
 * <ul>
 * <li>get - To download the feed files</li>
 * <li>unzip - To unzip the gzipped files filter</li>
 * <li>filter - To apply filters to the unzipped file and create a new file with the filtered
 * contents</li>
 * </ul>
 * </div>
 * 
 * @author shanganesh
 *
 */
public interface Feed {

  /**
   * <p>
   * Filters the contents of an unzipped feed file
   * </p>
   * 
   * @param filterRequest <div>
   *        Consists of all the parameters that are required for filtering.<br>
   * 
   *        In case of filtering by level two or level three categories, it is mandatory to pass
   *        <ul>
   *        <li>level one category</li>
   *        <li>marketplace</li>
   *        <li>token</li>
   *        </ul>
   *        </div>
   * @return Response Response
   */
  public Response filter(FeedFilterRequest filterRequest);

  /**
   * <p>
   * Unzip downloaded feed file and create a new file
   * </p>
   * 
   * @param filePath The absolute path to the gzipped feed file
   * @return Response Response
   */
  public Response unzip(String filePath);


  /**
   * <p>
   * Downloads the feed file as specified by the parameters in the request. Based on the feedRequest
   * values, if a file is available, then it is downloaded and the method returns the file path.
   * 
   * If the file is not available, then the error details are returned.
   * </p>
   * 
   * @param feedRequest Container for capturing parameters for downloading feed file
   * @param downloadDirectory The local directory where the file should be downloaded. Default location is
   * the current working directory
   * @return GetFeedResponse GetFeedResponse
   */
  public GetFeedResponse get(FeedRequest feedRequest, String downloadDirectory);

  /**
   * <pre>
   * Given a config file, this method parses the input, and performs necessary actions,
   * as specified in the config file.
   * 
   * The config file should consist of json and follow the structure :-
   * {
   * 
   *  "requests": [
   *    {
   *        "feedRequest": {
   *        
   *        },
   *        "filterRequest": {
   *        
   *        }
   *    }
   *    
   *  ]
   * }
   * 
   * 
   * If the original file is already downloaded, then the feedRequest becomes optional,
   * and the unzipped file path can be provided in the 'inputFilePath' field in the 
   * filter request,
   * along with the other filter characteristics.
   * 
   * If the entire workflow needs to be performed i.e - download, unzip and filter,
   * then both feedRequest and filterRequest are required.
   * The inputFilePath can be omitted in this case.
   * </pre>
   * 
   * @param configFile The absolute path of the file, which consists of a list of feed request or filter 
   * request or both
   * @param token The oAuth token with the appropriate scope Eg - Bearer vxxx
   * @return List of type Response
   */
  public List<com.ebay.feed.model.feed.operation.filter.Response> processConfigFile(
      String configFile, String token);
}
