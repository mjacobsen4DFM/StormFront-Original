package com.DFM.StormFront.Interface.Handler;

import com.DFM.StormFront.Adapter.WordPressAdapter;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Client.WordPressClient;
import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.LogUtil;
import org.json.simple.JSONObject;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mick on 2/12/2016.
 */
public class WordPressHandler {
    //Handle calls to WordPress

    private static String _xsltPathDev = "C:\\Users\\Mick\\Documents\\Cloud\\Google Drive\\mjacobsen@denverpost.com\\Dev\\mason\\share\\Projects\\Java\\StormFront\\Resources\\xslt\\";
    private static String _xsltPathLive = "/etc/storm/transactstorm/conf/xslt/";

    private static String subscriber = "WordPress";

    public static Response PostPost(String redisType,
                                    String redisKey,
                                    String remoteEndpoint,
                                    InputStream incomingData){
        StringBuilder Builder = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line;
            while ((line = in.readLine()) != null) {
                Builder.append(line);
            }
            String json = Builder.toString();
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.postJson(json, postEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .entity(resultMap.get("error"))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .entity(resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpPostId"))
                .header("location", resultMap.get("postLocation"))
                .entity(resultMap.get("result"))
                .build();

    }

    public static Response PostMeta(String redisType,
                                    String redisKey,
                                    String remoteEndpoint,
                                    InputStream incomingData){
        StringBuilder Builder = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line;
            while ((line = in.readLine()) != null) {
                Builder.append(line);
            }
            String json = Builder.toString();
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            List<JSONObject> metaList = JsonUtil.getList(json, "meta");
            if (metaList != null) {
                for (JSONObject meta : metaList) {
                    String metaJson = JsonUtil.toJSON(meta);
                    resultMap = WordPressAdapter.postJson(metaJson, postEndpoint, wpc);

                    if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
                        return Response.status(500)
                                .header("error", resultMap.get("error"))
                                .entity(resultMap.get("error"))
                                .build();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .entity(resultMap.get("error"))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .entity(resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpPostId"))
                .header("location", resultMap.get("postLocation"))
                .entity(resultMap.get("result"))
                .build();

    }
    public static Response PutPost(String redisType,
                                    String redisKey,
                                    String remoteEndpoint,
                                    InputStream incomingData){
        StringBuilder Builder = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line;
            while ((line = in.readLine()) != null) {
                Builder.append(line);
            }
            String json = Builder.toString();
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.putJson(json, postEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .entity(resultMap.get("error"))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .entity(resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpPostId"))
                .header("location", resultMap.get("postLocation"))
                .entity(resultMap.get("result"))
                .build();

    }

    public static Response GetPost(String redisType,
                                   String redisKey,
                                   String remoteEndpoint) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.getJson(postEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpPostId"))
                .header("location", resultMap.get("postLocation"))
                .entity(resultMap.get("body"))
                .build();
    }



    public static Response PostMedia(String redisType,
                                     String redisKey,
                                     String remoteEndpoint,
                                     InputStream incomingData) {
        StringBuilder Builder = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line;
            while ((line = in.readLine()) != null) {
                Builder.append(line);
            }
            String json = Builder.toString();
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String mediaEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.postMedia(json, mediaEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpImageId"))
                .header("location", resultMap.get("mediaLocation"))
                .entity(resultMap.get("result"))
                .build();
    }



    public static Response DeleteAttributes(String redisType,
                                            String redisKey,
                                            String remoteEndpoint) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.postDelete(postEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("location", resultMap.get("location"))
                .entity(resultMap.get("result"))
                .build();
    }


    //Sets default author
    public static Response PostDefaultAuthor(String redisType,
                                             String redisKey,
                                             String remoteEndpoint){
        Map<String, String> resultMap = new HashMap<>();
        try {
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String postEndpoint = subscriberMap.get("url") + remoteEndpoint;
            String json = String.format("{\"author\":\"%s\"}", subscriberMap.get("userid"));
            WordPressClient wpc = WordPressClient.NewClient(subscriberMap);
            resultMap = WordPressAdapter.postJson(json, postEndpoint, wpc);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .build();
        }

        if(WebClient.isBad(Integer.parseInt(resultMap.get("code")))){
            return Response.status(500)
                    .header("error", resultMap.get("error"))
                    .build();
        }

        return Response.status(Integer.parseInt(resultMap.get("code")))
                .header("id", resultMap.get("wpPostId"))
                .header("location", resultMap.get("postLocation"))
                .entity(resultMap.get("result"))
                .build();

    }
    private static String setRedisKey(String subscriber, String publisher, String site_name, String site_url) {
        String siteName = site_name.replace(" ", "");
        String siteUrl = site_url.replace("http://", "").replace("https://", "");
        return String.format("subscribers:%s:Proteus:%s:%s:%s", subscriber, publisher, siteName, siteUrl);
    }
}
