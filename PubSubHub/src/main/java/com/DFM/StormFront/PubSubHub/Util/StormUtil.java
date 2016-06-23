package com.DFM.StormFront.PubSubHub.Util;

import backtype.storm.tuple.Tuple;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Util.ExceptionUtil;




public class StormUtil {
    public static void logError(Tuple tuple, Exception e, RedisClient redisClient) {
        logError(tuple, "", e, redisClient);
    }

    public static void logError(Tuple tuple, String msg, Exception e, RedisClient redisClient) {
        String tupleMsg = getTuple(tuple);
        String fullStackTrace = ExceptionUtil.getFullStackTrace(e);
        RedisLogUtil.logError(tupleMsg, msg, fullStackTrace, redisClient);
    }
    private static String getTuple(Tuple tuple) {
        return "source: " + tuple.getSourceComponent() + ", stream: " + tuple.getSourceStreamId() + ", id: " + tuple.getMessageId() + ", Task: " + tuple.getSourceTask();
    }
}
