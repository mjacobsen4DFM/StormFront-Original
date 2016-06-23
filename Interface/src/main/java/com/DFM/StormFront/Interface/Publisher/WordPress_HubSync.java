package com.DFM.StormFront.Interface.Publisher;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Interface.Exec.PublisherExec;
import com.DFM.StormFront.Model.WordPress.HubSync;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.FileUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.StringUtil;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 6/2/2016.
 */
public class WordPress_HubSync {

    public static Map<String, Object> Publish(RedisClient redisClient,
                                              InputStream incomingData,
                                              String source,
                                              String pubKeyRoot) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, String> publishMap = new HashMap<>();
        String finalCode = "200";


        PublisherExec publisher = new PublisherExec();
        String pubKey = "";
        String xml = "";
        String wpId = "";

        try {
            wpId = "error";
            HubSync hubSync = getHubSync(incomingData);
            String site_name = hubSync.getSite_name();
            String source_name = hubSync.getSource();
            String title = hubSync.getTitle();
            String slug = hubSync.getSlug();
            String remote_file_name = hubSync.getRemote_file_name();
            wpId = hubSync.getPost_id();

            hubSync.setPublisher(source);

            if (null == site_name || site_name.equals("")) {
                hubSync.setSite_name(source_name);
            }

            if (null == slug || slug.equals("")) {
                hubSync.setSlug(StringUtil.hyphenateString(title));
            }

            if (null == remote_file_name || remote_file_name.equals("")) {
                hubSync.setRemote_file_name(StringUtil.hyphenateString(title));
            }

            String site_url = hubSync.getSite_url();
            site_url = site_url.replace("http://", "");
            site_url = site_url.replace("/", "");

            pubKey = pubKeyRoot + ":" + site_url;

            //Get XML and images from HubSync
            xml = hubSync.toXml();

            publishMap.putAll(publisher.Exec(redisClient, xml, pubKey, site_url));


            if (null == publishMap.get("code") || WebClient.isBad(Integer.parseInt(publishMap.get("code")))) {
                finalCode = "409";
            }

            resultMap.put(wpId, publishMap);
        } catch (Exception e) {
            String msg = "Publishing pubKey: " + pubKey + ", xml: " + xml;
            RedisLogUtil.logError(msg, e, redisClient);
            finalCode = "409";
            resultMap.put(wpId, ExceptionUtil.getFullStackTrace(e));
        }

        resultMap.put("code", finalCode);
        return resultMap;
    }


    private static HubSync getHubSync(InputStream incomingData) throws Exception {
        String rawJSON = StringUtil.fromInputStream(incomingData);
        String fixJSON = StringUtil.removeUnicode(rawJSON);

        try {
            //Convert the JSON to the HubSync object
            HubSync hubSync = new HubSync();
            hubSync = hubSync.fromJSON(fixJSON, "string");

            String id = hubSync.getPost_id();
            String file_name = hubSync.getRemote_file_name();
            FileUtil.printFile(FileUtil.getLogPath(), "Hubsync", id + "_raw", file_name.replace(".xml", ""), "txt", rawJSON);
            FileUtil.printFile(FileUtil.getLogPath(), "Hubsync", id + "_fix", file_name.replace(".xml", ""), "txt", fixJSON);

            return hubSync;
        } catch (Exception e) {
            if (1 == 11) LogUtil.log("BAD JSON:" + rawJSON);
            FileUtil.printFile(FileUtil.getLogPath(), "Hubsync", "bad", "unknown", "txt", rawJSON);
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
    }
}
