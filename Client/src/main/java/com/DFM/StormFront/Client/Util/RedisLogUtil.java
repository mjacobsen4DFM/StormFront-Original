package com.DFM.StormFront.Client.Util;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.SystemUtil;

import java.util.Date;


public class RedisLogUtil {
    public static void logError(Exception e, RedisClient redisClient) {
        logError("", e, redisClient);
    }

    public static void logError(String msg, Exception e, RedisClient redisClient) {
        String fullStackTrace = ExceptionUtil.getFullStackTrace(e);
        logError(msg, fullStackTrace, redisClient);
    }

    private static void logError(String friendlyMsg, String exceptionMsg, RedisClient redisClient) {
        String strLogMessage = compileMessage(friendlyMsg, exceptionMsg);
        logError("Warning: " + strLogMessage.replace(");", ");\r\n"), redisClient);
    }

    private static void logError(String msg, RedisClient redisClient) {
        log(msg, redisClient, "errors");
    }

    public static void logWarning(Exception e, RedisClient redisClient) {
        logWarning("", e, redisClient);
    }

    public static void logWarning(String msg, Exception e, RedisClient redisClient) {
        String fullStackTrace = ExceptionUtil.getFullStackTrace(e);
        logWarning(msg, fullStackTrace, redisClient);
    }

    private static void logWarning(String friendlyMsg, String exceptionMsg, RedisClient redisClient) {
        String strLogMessage = compileMessage(friendlyMsg, exceptionMsg);
        logWarning("Warning: " + strLogMessage.replace(");", ");\r\n"), redisClient);
    }

    private static void logWarning(String msg, RedisClient redisClient) {
        log(msg, redisClient, "warnings");
    }

    public static void logInfo(String msg, RedisClient redisClient) {
        log(msg, redisClient, "info");
    }

    public static void log(String msg, RedisClient redisClient, String level) {
        try {
            msg = (msg == null) ? "No message to log." : msg;
            LogUtil.log(msg);
            String hostname = SystemUtil.getHostname();
            String time = new Date().toString().replace(":", "-");
            redisClient.set(level +":" + hostname + ":" + time, msg);
        } catch (Exception ignored) {
        }
    }

    private static String compileMessage(String friendlyMsg, String exceptionMsg){
        String strMessage = (!friendlyMsg.equals("")) ? "Message(" + friendlyMsg + ");" : friendlyMsg;
        String strException = (!exceptionMsg.equals("")) ? "Exception(" + exceptionMsg + ");" : exceptionMsg;
        return strMessage + " " + strException;
    }

}
