package com.DFM.StormFront.Adapter;

import com.DFM.StormFront.Client.ElasticSearchClient;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.LogUtil;

import java.util.Map;

/**
 * Created by Mick on 2/12/2016.
 */
public class ElasticSearchAdapter {

    public static Map<String, String> getJson(String endpoint, ElasticSearchClient elasticSearchClient) throws Exception {
        try {
            return elasticSearchClient.get(endpoint);
        } catch (Exception e) {
            String errMsg = "Fatal GET error for: " + endpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
    }

/*
    public static Map<String, String> postJson(String json, String endpoint, ElasticSearchClient elasticSearchClient) throws Exception {
        Map<String, String> resultMap;
        String esIndexId = "";

        try {
            resultMap = elasticSearchClient.post(endpoint, json);

            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                resultMap = elasticSearchClient.post(endpoint, json);
            }

            if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
                esIndexId = JsonUtil.getValue(resultMap.get("result"), "id");
            } else {
                String errMsg = "Fatal post error for " + endpoint + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result") + " JSON: " + JsonUtil.toJSON(json);
                throw new Exception(errMsg);
            }
        } catch (Exception e) {
            String errMsg = "Fatal POST error for: " + endpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        resultMap.put("esIndexId", esIndexId);
        return resultMap;
    }
*/

    public static Map<String, String> putJson(String json, String endpoint, ElasticSearchClient elasticSearchClient) throws Exception {
        Map<String, String> resultMap;
        String esIndexId;

        try {
            resultMap = elasticSearchClient.put(endpoint, json);

            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                resultMap = elasticSearchClient.put(endpoint, json);
            }

            if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
                esIndexId = JsonUtil.getValue(resultMap.get("result"), "_id");
            } else {
                String errMsg = "Fatal post error for " + endpoint + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result") + " JSON: " + JsonUtil.toJSON(json);
                throw new Exception(errMsg);
            }

        } catch (Exception e) {
            String errMsg = "Fatal PUT error for: " + endpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        resultMap.put("esIndexId", esIndexId);
        return resultMap;
    }

    public static Map<String, String> delete(String deleteEndpoint, ElasticSearchClient elasticSearchClient) throws Exception {
        Map<String, String> resultMap;
        try {
            resultMap = elasticSearchClient.delete(deleteEndpoint);
            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                if(404 == Integer.parseInt(resultMap.get("code").trim())){
                    String msg = "Missing item for DELETE for " + deleteEndpoint + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result");
                    LogUtil.log(msg);
                } else {
                    String errMsg = "Fatal DELETE error for " + deleteEndpoint + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result");
                    throw new Exception(errMsg);
                }
            }
        } catch (Exception e) {
            String errMsg = "Fatal DELETE error for: " + deleteEndpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        return resultMap;
    }
}
