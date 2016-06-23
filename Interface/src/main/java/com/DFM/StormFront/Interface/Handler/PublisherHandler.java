package com.DFM.StormFront.Interface.Handler;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Interface.Publisher.NGPS_ContentSync;
import com.DFM.StormFront.Interface.Publisher.Saxotech_MWC;
import com.DFM.StormFront.Interface.Publisher.WordPress_HubSync;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.LogUtil;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Mick on 5/18/2016.
 */
public class PublisherHandler {
        public static Response NGPS_ContentSync(String redisType,
                                            InputStream incomingData,
                                            boolean bDelete) {

        Map<String, Object> resultMap;
        RedisClient redisClient = new RedisClient(redisType);
        String feedType = "ContentSync";
        String pubKeyRoot = "publishers:RESTful:NGPS:" + feedType;
        String code;
        String json;

        try {
            resultMap = NGPS_ContentSync.Publish(redisClient, incomingData, bDelete, pubKeyRoot);
            code = (String)resultMap.get("code");
            resultMap.remove("code");
            json = JsonUtil.toJSON(resultMap);
        } catch (Exception e) {
            RedisLogUtil.logError(e, redisClient);
            return Response.status(500)
                    .entity(ExceptionUtil.getFullStackTrace(e))
                    .build();
        }

        return Response.status(Integer.parseInt(code))
                .entity(json)
                .build();
    }

    public static Response WordPress_HubSync(String redisType,
                                             InputStream incomingData) {

        Map<String, Object> resultMap;
        RedisClient redisClient = new RedisClient(redisType);

        String source = "WordPress";
        String feedType = "HubSync";
        String pubKeyRoot = "publishers:RESTful:" + source + ":" + feedType;
        String code;
        String json;

        try {

            resultMap = WordPress_HubSync.Publish(redisClient, incomingData, source, pubKeyRoot);
            code = (String)resultMap.get("code");
            resultMap.remove("code");
            json = JsonUtil.toJSON(resultMap);
        } catch (Exception e) {
            LogUtil.log("Error: " + ExceptionUtil.getFullStackTrace(e));
            return Response.status(500)
                    .entity(ExceptionUtil.getFullStackTrace(e))
                    .build();
        }

        return Response.status(Integer.parseInt(code))
                .entity(json)
                .build();
    }


    public static Response Saxotech_MWC(String redisType,
                                            InputStream incomingData) {

        Map<String, Object> resultMap;
        RedisClient redisClient = new RedisClient(redisType);
        String feedType = "ContentSync";
        String pubKeyRoot = "publishers:RESTful:NGPS:" + feedType;
        String code;
        String json;

        try {
            resultMap = Saxotech_MWC.Publish(redisClient, incomingData, pubKeyRoot);
            code = (String)resultMap.get("code");
            resultMap.remove("code");
            json = JsonUtil.toJSON(resultMap);
        } catch (Exception e) {
            RedisLogUtil.logError(e, redisClient);
            return Response.status(500)
                    .entity(ExceptionUtil.getFullStackTrace(e))
                    .build();
        }

        return Response.status(Integer.parseInt(code))
                .entity(json)
                .build();
    }
}
