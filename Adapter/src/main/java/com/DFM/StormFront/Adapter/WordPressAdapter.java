package com.DFM.StormFront.Adapter;

import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Client.WordPressClient;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 2/12/2016.
 */
public class WordPressAdapter {

    public static Map<String, String> getJson(String endpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        String body;
        try {
            body = wpc.get(endpoint);

            resultMap.put("code", "200");
            resultMap.put("body", body);
        } catch (Exception e) {
            String errMsg = "Fatal GET error for: " + endpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        return resultMap;
    }

    public static Map<String, String> getAllPages(String endpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        ArrayList<String> all = new ArrayList<>();
        JSONArray jsonArray;

        String pageArg = (endpoint.contains("?")) ? "&page=" : "?page=";
        short pageCount = 0;

        String body = "";
        try {
            while (!body.equals("[]")) {
                pageCount += 1;
                body = wpc.get(endpoint + pageArg + pageCount);
                if (!body.equals("[]")) {
                    jsonArray = JsonUtil.getArray(body);
                    jsonArray.stream().filter(obj -> obj instanceof JSONObject).forEach(obj -> {
                        String jsonElement = JsonUtil.toJSON(obj);
                        all.add(jsonElement);
                    });
                }
            }
            String sAll = String.format("[%s]", String.join(",", all));
            resultMap.put("code", "200");
            resultMap.put("body", sAll);
        } catch (Exception e) {
            String errMsg = "Fatal GETALL error for: " + endpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        return resultMap;
    }

    public static Map<String, String> postJson(String json, String postBaseEndpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap = new HashMap<>();

        String postLocation = postBaseEndpoint;
        String wpPostId = "";

        try {
            resultMap = wpc.post(postLocation, json);


            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                resultMap = wpc.post(postLocation, json);
            }

            if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
                String jsonResult = resultMap.get("result");
                jsonResult = jsonResult.replace("{\"status\":\"ok\"}", "").trim();
                wpPostId = JsonUtil.getValue(jsonResult, "id");
                if(! postBaseEndpoint.contains(wpPostId)) {
                    if (postLocation.equalsIgnoreCase(postBaseEndpoint)) {
                        postLocation = resultMap.get("location");
                    }
                    if (StringUtil.isNullOrEmpty(postLocation)) {
                        postLocation = String.format("%s%s", postBaseEndpoint, wpPostId);
                    }
                }
            } else {
                String errMsg = "Fatal post error for " + postLocation + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result") + " JSON: " + JsonUtil.toJSON(json);
                throw new Exception(errMsg);
            }
        } catch (Exception e) {
            String errMsg = "Fatal POST error for: " + postBaseEndpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        resultMap.put("wpPostId", wpPostId);
        resultMap.put("postLocation", postLocation);
        return resultMap;
    }

    public static Map<String, String> putJson(String json, String postBaseEndpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap;
        String postLocation = postBaseEndpoint;
        String wpPostId = "";

        try {
            resultMap = wpc.put(postLocation, json);

            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                resultMap = wpc.post(postLocation, json);
            }

            if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
                String jsonResult = resultMap.get("result");
                jsonResult = jsonResult.replace("{\"status\":\"ok\"}", "").trim();
                wpPostId = JsonUtil.getValue(jsonResult, "id");
                if(postLocation == postBaseEndpoint) {
                    postLocation = resultMap.get("location");
                }
                if(StringUtil.isNullOrEmpty(postLocation)) {
                    postLocation = String.format("%s%s", postBaseEndpoint, wpPostId);
                }
            } else {
                String errMsg = "Fatal post error for " + postLocation + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result") + " JSON: " + JsonUtil.toJSON(json);
                throw new Exception(errMsg);
            }

        } catch (Exception e) {
            String errMsg = "Fatal POST error for: " + postBaseEndpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        resultMap.put("wpPostId", wpPostId);
        resultMap.put("postLocation", postLocation);
        return resultMap;
    }

    public static Map<String, String> postMedia(String imageJson, String mediaBaseEndpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> intermediateMap = new HashMap<>();
        //  String mediaEndpoint = mediaBaseEndpoint + "media/";
        String mediaLocation;
        String postLocation;
        String wpPostid;
        String imageName;
        String imageFeatured;
        String imageSource;
        String imageMimetype;
        String imageCaption;
        String imageAuthor;
        String imageDate;
        String wpImageId;
        String json;
        String contentDisposition;

        //Extract image metadata
        wpPostid = JsonUtil.getValue(imageJson, "post_id");
        postLocation = JsonUtil.getValue(imageJson, "postlocation");
        imageName = JsonUtil.cleanString(JsonUtil.getValue(imageJson, "name"));
        imageFeatured = JsonUtil.getValue(imageJson, "featured");
        imageSource = JsonUtil.getValue(imageJson, "source");
        imageMimetype = JsonUtil.getValue(imageJson, "mimetype");
        imageCaption = JsonUtil.cleanString(JsonUtil.getValue(imageJson, "caption"));
        imageAuthor = JsonUtil.getValue(imageJson, "author");
        imageDate = JsonUtil.getValue(imageJson, "date");
        //fix name
        imageName = StringUtil.hyphenateString(imageName);

        //Upload the image
        if(wpc.getVersion().equalsIgnoreCase("2.0-beta12.1")) {
            contentDisposition= String.format("filename=%s", imageName);
        } else {
            contentDisposition= String.format("attachment; filename=%s", imageName);
        }
        resultMap = wpc.uploadImage(mediaBaseEndpoint, imageSource, imageMimetype, imageName, contentDisposition);

        if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
            String jsonResult = resultMap.get("result");
            jsonResult = jsonResult.replace("{\"status\":\"ok\"}", "").trim();
            wpImageId = JsonUtil.getValue(jsonResult, "id");
            mediaLocation = mediaBaseEndpoint + wpImageId;

            //Add first image as featured image for post
            if (Boolean.valueOf(imageFeatured)) {
                json = "{\"id\":" + wpPostid + ",\"featured_media\":" + wpImageId + "}";
                intermediateMap = wpc.post(postLocation, json);
                if (WebClient.isBad(Integer.parseInt(intermediateMap.get("code").trim()))) {
                    intermediateMap = wpc.post(postLocation, json);
                }

                if (WebClient.isBad(Integer.parseInt(intermediateMap.get("code").trim()))) {
                    String errMsg = "Featured Image error for: " + imageName + " Code: " + intermediateMap.get("code") + " Response: " + intermediateMap.get("result");
                    throw new Exception(errMsg);
                }
            }

            json = "{\"id\":" + wpImageId + ",\"author\":" + imageAuthor + ",\"title\": \"" + imageName + "\",\"date_gmt\": \"" + imageDate + "\",\"caption\": \"" + imageCaption + "\",\"post\":" + wpPostid + "}";

            intermediateMap = wpc.post(mediaLocation, json);
            if (WebClient.isBad(Integer.parseInt(intermediateMap.get("code").trim()))) {
                intermediateMap = wpc.post(mediaLocation, json);
                if (WebClient.isBad(Integer.parseInt(intermediateMap.get("code").trim()))) {
                    String errMsg = "Image Metadata error for: " + imageName + " Code: " + intermediateMap.get("code") + " Response: " + intermediateMap.get("result");
                    throw new Exception(errMsg);
                }
            }
        } else {
            String errMsg = "Image post error for: " + imageName + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result");
            throw new Exception(errMsg);
        }

        resultMap.put("wpImageId", wpImageId);
        resultMap.put("mediaLocation", mediaLocation);
        return resultMap;
    }

    public static Map<String, String> postDelete(String deleteEndpoint, WordPressClient wpc) throws Exception {
        Map<String, String> resultMap;
        try {
            resultMap = wpc.delete(deleteEndpoint);
            if (WebClient.isBad(Integer.parseInt(resultMap.get("code").trim()))) {
                String errMsg = "Fatal DELETE error for " + deleteEndpoint + " Code: " + resultMap.get("code") + " Response: " + resultMap.get("result");
                throw new Exception(errMsg);
            }
        } catch (Exception e) {
            String errMsg = "Fatal DELETE error for: " + deleteEndpoint + " Error: " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
        return resultMap;
    }
}
