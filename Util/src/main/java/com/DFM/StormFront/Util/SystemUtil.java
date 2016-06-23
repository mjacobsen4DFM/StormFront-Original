package com.DFM.StormFront.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Mick on 4/16/2016.
 */
public class SystemUtil {


    public static String getHostname() {
        try {
            InetAddress address;
            address = InetAddress.getLocalHost();
            return address.getHostName();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }
}
