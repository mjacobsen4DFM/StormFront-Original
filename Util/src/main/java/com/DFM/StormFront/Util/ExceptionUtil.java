package com.DFM.StormFront.Util;

import org.apache.commons.lang.exception.ExceptionUtils;
/**
 * Created by Mick on 4/16/2016.
 */
public class ExceptionUtil {
    private static ExceptionUtils exceptionUtils = new ExceptionUtils();

    public static String getFullStackTrace(Exception e) {
        return "error: " + e + ", message: " + e.getMessage() + ", StackTrace: " + exceptionUtils.getFullStackTrace(e);
    }
}
