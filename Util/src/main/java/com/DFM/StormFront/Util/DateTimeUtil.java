package com.DFM.StormFront.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mick on 9/19/2016.
 */
public class DateTimeUtil {

    public static String MilliSecondsToDateISO8601(long ms){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
        return MilliSecondsToDateFormat(ms, sdf);
    }

    public static String MilliSecondsToDateFormat(long ms, SimpleDateFormat sdf){
        Date resultdate = new Date(ms);
        return sdf.format(resultdate);
    }

}
