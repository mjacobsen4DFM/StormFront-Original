package com.DFM.StormFront.Interface.Handler;

import com.DFM.StormFront.Adapter.NGPSAdapter;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Util.LogUtil;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 4/15/2016.
 */
public class NGPSHandler {
    //Handle calls to NGPS

    private static String _xsltPathDev = "C:\\Users\\Mick\\Documents\\Cloud\\Google Drive\\mjacobsen@denverpost.com\\Dev\\mason\\share\\Projects\\Java\\StormFront\\Resources\\xslt\\";
    private static String _xsltPathLive = "/etc/storm/transactstorm/conf/xslt/";

    private static String subscriber = "NGPS";



    public static Response PutNGPS_WordPressPost(String redisType,
                                                 InputStream incomingData) {
        String publisher = "WordPress";
        Map<String, String> resultMap = new HashMap<>();
        try {
            //String redisKey = setRedisKey(publisher, site_name, site_url)
            String site_name = "";  //hubSync.getSite_name();
            String site_url = "";  //hubSync.getSite_url();
            String remoteFilename = "remoteFilename.txt";
            //hubSync.setPublisher(publisher);
            //hubSync.setSlug(hubSync.getRemote_file_name().replace(".xml", ""));

            String redisKey = setRedisKey(subscriber, publisher, site_name, site_url);

            //Use Redis to get credentials and workingDirectory
            RedisClient redisClient = new RedisClient(redisType);
            Map<String, String> subscriberMap = redisClient.hgetAll(redisKey);
            String host = subscriberMap.get("host");
            String username = subscriberMap.get("UT");
            String password = subscriberMap.get("US");
            String workingDirectory = subscriberMap.get("WD");

            resultMap = NGPSAdapter.PutNGPS(host, username, password, workingDirectory, incomingData, remoteFilename);
        } catch (Exception e) {
            LogUtil.log("Error: " + e.getMessage());
            return Response.status(500)
                    .header("error", String.format("CMS Error: %s, Broker Error: %s", resultMap.get("error"), e.getMessage()))
                    .entity(resultMap.get("error"))
                    .build();
        }

        if (WebClient.isBad(Integer.parseInt(resultMap.get("code")))) {
            return Response.status(Integer.parseInt(resultMap.get("code")))
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



    private static String setRedisKey(String subscriber, String publisher, String site_name, String site_url) {
        String siteName = site_name.replace(" ", "");
        String siteUrl = site_url.replace("http://", "").replace("https://", "");
        return String.format("subscribers:%s:Proteus:%s:%s:%s", subscriber, publisher, siteName, siteUrl);
    }
}
