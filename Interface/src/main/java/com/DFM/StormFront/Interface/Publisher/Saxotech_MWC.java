package com.DFM.StormFront.Interface.Publisher;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Interface.Exec.PublisherExec;
import com.DFM.StormFront.Util.FileUtil;
import com.DFM.StormFront.Util.StringUtil;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 6/2/2016.
 */
public class Saxotech_MWC {

    public static Map<String, Object> Publish(RedisClient redisClient,
                                              InputStream incomingData,
                                              String pubKeyRoot) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, String> pubMap = new HashMap<>();
        PublisherExec publisher = new PublisherExec();
        String finalCode = "200";
        String pubKey = "";
        String xml = "";
        String cId = "";

        try {
            getContent(incomingData);
        } catch (Exception e) {
            resultMap.put("code", "406");
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("message", e.getMessage());
            String msg = jsonobject.toString();
            resultMap.put("result", msg);
            return resultMap;
        }

        resultMap.put("code", finalCode);
        resultMap.put("result", "success");
        return resultMap;
    }

    private static Boolean getContent(InputStream incomingData) throws Exception {
        String content = StringUtil.fromInputStream(incomingData);

        FileUtil.printFile(FileUtil.getLogDir(), "SaxoTechMWC", "raw", "test", "txt", content);
        return true;
    }
}
