package com.DFM.StormFront.PubSubHub.Util;

import backtype.storm.tuple.Tuple;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;




public class StormUtil {
    public static void logFail(Tuple tuple, Exception e, RedisClient redisClient) {
        String tupleMsg = getTuple(tuple);
        RedisLogUtil.logError(tupleMsg, e, redisClient);
    }

    public static void logFail(String msg, RedisClient redisClient) {
        RedisLogUtil.log(msg, redisClient, "StormFailures");
    }

    private static String getTuple(Tuple tuple) {
        return "source: " + tuple.getSourceComponent() + ", stream: " + tuple.getSourceStreamId() + ", id: " + tuple.getMessageId() + ", Task: " + tuple.getSourceTask();
    }
}
