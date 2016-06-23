package com.DFM.StormFront.Client.Util;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.SystemUtil;

import java.util.Date;


public class RedisLogUtil {
    public static void logError(Exception e, RedisClient redisClient) {
        String fullStackTrace = ExceptionUtil.getFullStackTrace(e);
        logError(fullStackTrace, redisClient);
    }

    public static void logError(String msg, Exception e, RedisClient redisClient) {
        String fullStackTrace = ExceptionUtil.getFullStackTrace(e);
        logError("", msg, fullStackTrace, redisClient);
    }

    public static void logError(String tupleMsg, String friendlyMsg, String exceptionMsg, RedisClient redisClient) {
        String strTuple = (!tupleMsg.equals("")) ? "Tuple(" + tupleMsg + ");" : tupleMsg;
        String strMessage = (!friendlyMsg.equals("")) ? "Message(" + friendlyMsg + ");" : friendlyMsg;
        String strException = (!exceptionMsg.equals("")) ? "Exception(" + exceptionMsg + ");" : exceptionMsg;
        String strLogMessage = strTuple + " " + strMessage + " " + strException;
        logError("Error: " + strLogMessage.replace(");", ");\r\n"), redisClient);
    }

    public static void logError(String msg, RedisClient redisClient) {
        try {
            msg = (msg == null) ? "No error message to log." : msg;
            LogUtil.log(msg);
            String hostname = SystemUtil.getHostname();
            String time = new Date().toString().replace(":", "-");
            redisClient.set("errors:" + hostname + ":" + time, msg);
        } catch (Exception ignored) {
        }
    }

}
