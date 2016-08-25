package com.DFM.StormFront.Interface.Exec;

import backtype.storm.topology.base.BaseRichBolt;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Workflow.StoryReaderBolt;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.FileUtil;
import com.DFM.StormFront.Util.SerializationUtil;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 6/2/2016.
 */
public class PublisherExec {

    public PublisherExec(){}

    public Map<String, String> Exec(RedisClient redisClient, String xml, String pubKey, String site_url) {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> conf = new HashMap<>();
        Map<String, String> keys;

        Publisher publisher;

        Map<String, Object> articleConf = new HashMap<>();
        articleConf.put("mainConf", conf);

        //force load Storm; errors without this initializer
        BaseRichBolt srb = new StoryReaderBolt();

        try {
            conf.put("redisHost", redisClient.getHost());
            conf.put("redisPort", Integer.toString(redisClient.getPort()));
            conf.put("redisTimeout", Integer.toString(redisClient.getTimeout()));
            conf.put("redisPassword", redisClient.getPassword());
            conf.put("redisDatabase", Integer.toString(redisClient.getDatabase()));
            conf.put("xsltRootPath", FileUtil.getXsltDir());

            keys = redisClient.hgetAll(pubKey);
            if(keys.size() > 0) {
                keys.put("pubKey", pubKey);
                keys.put("feedKey", pubKey);
                keys.put("originator", site_url);
                publisher = new com.DFM.StormFront.Model.Publisher(keys);

                byte[] binaryPublisher = SerializationUtil.serialize(publisher);

                articleConf.put("story", xml);
                articleConf.put("publisher", binaryPublisher);

                resultMap = StoryReaderBolt.linear(articleConf);
            } else {
                resultMap.put("code", "404");
                JSONObject jsonobject = new JSONObject();
                jsonobject.put("message", "Unknown publisher; pubkey: " + pubKey);
                String msg = jsonobject.toString();
                resultMap.put("result", msg);
            }
        } catch (Exception e) {
            RedisLogUtil.logError(e, redisClient);
            resultMap.put("code", "500");
            resultMap.put("result", ExceptionUtil.getFullStackTrace(e));
        }

        return resultMap;
    }
}
