package com.DFM.StormFront.Interface.Publisher;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Interface.Exec.PublisherExec;
import com.DFM.StormFront.Model.NGPS.Article;
import com.DFM.StormFront.Model.NGPS.ContentSync;
import com.DFM.StormFront.Util.*;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 6/2/2016.
 */
public class NGPS_ContentSync {

    public static Map<String, Object> Publish(RedisClient redisClient,
                                              InputStream incomingData,
                                              boolean bDelete,
                                              String pubKeyRoot) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, String> pubMap = new HashMap<>();
        PublisherExec publisher = new PublisherExec();
        String finalCode = "200";
        String pubKey = "";
        String xml = "";
        String cId = "";
        ContentSync contentSync = new ContentSync();

        String incomingString = StringUtil.fromInputStream(incomingData);

        try {
            contentSync = getContentSync(incomingString);
        } catch (Exception e) {
            resultMap.put("code", "406");
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("message", e.getMessage());
            String msg = jsonobject.toString();
            resultMap.put("result", msg);
            return resultMap;
        }

        for (Article article : contentSync.articles) {
            try {
                cId = "error";
                cId = article.getCId();
                article.setDelete(bDelete);

                FileUtil.printFile(FileUtil.getLogPath(), "ContentSync", article.getCId(), "incomingString", "txt", incomingString);

                xml = article.toXml();

                String site_url = article.siteInformation.getSiteProductionUrl();
                site_url = site_url.replace("http://", "");
                site_url = site_url.replace("/", "");

                pubKey = pubKeyRoot + ":" + site_url;
                pubMap.putAll(publisher.Exec(redisClient, xml, pubKey, site_url));

                if (null == pubMap.get("code") || WebClient.isBad(Integer.parseInt(pubMap.get("code")))) {
                    finalCode = "409";
                }

                resultMap.put(cId, pubMap);

            } catch (Exception e) {
                String msg = "Publishing pubKey: " + pubKey + ", xml: " + xml;
                RedisLogUtil.logError(msg, e, redisClient);
                finalCode = "409";
                resultMap.put(cId, ExceptionUtil.getFullStackTrace(e));
            }
        }
        resultMap.put("code", finalCode);
        return resultMap;
    }

    private static ContentSync getContentSync(String incomingString) throws Exception {
        //Convert the JSON to the ContentSync object
        ContentSync contentSync = new ContentSync();
        if (JsonUtil.isWellFormed(incomingString)) {
/*
            JSONObject jo = new JSONObject(content);
            String article = jo.get("article").toString();
*/
            String xml = XmlJsonUtil.JsonToXml(incomingString);
            xml = String.format("<articles>%s</articles>", xml);
            return contentSync.fromXML(xml);
        } else if (XmlUtil.isWellFormed(incomingString)) {
            return contentSync.fromXML(incomingString);
        }
        throw new Exception("Malformed input: " + incomingString);
    }
}
