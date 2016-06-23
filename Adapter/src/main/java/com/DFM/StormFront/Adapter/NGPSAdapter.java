package com.DFM.StormFront.Adapter;

import com.DFM.StormFront.Client.uFTPClient;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Mick on 4/15/2016.
 */
public class NGPSAdapter {
    private static String _xsltPathDev = "C:\\Users\\Mick\\Documents\\Cloud\\Google Drive\\mjacobsen@denverpost.com\\Dev\\mason\\share\\Projects\\StormFront\\PubSubHub\\src\\xslt\\";
    private static String _xsltPathLive = "/etc/storm/transactstorm/conf/xslt/";


    public static Map<String, String> PutNGPS(
            String host,
            String username,
            String password,
            String workingDirectory,
            InputStream uploadStream,
            String remoteFilename) throws Exception {

        //Send to NGPS
        return sendFile(host, username, password, workingDirectory, uploadStream, remoteFilename);
    }

    private static Map<String, String> sendFile(String host, String username, String password, String workingDirectory, InputStream uploadStream, String remoteFilename){
        //Configure FTP client
        uFTPClient uFtp = new uFTPClient(host, username, password);

        //Send data to NGPS
        return uFtp.put(workingDirectory, uploadStream, remoteFilename);

    }
}
