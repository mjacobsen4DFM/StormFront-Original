package com.DFM.StormFront.Client;

import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 4/15/2016.
 */
public class uFTPClient {
    private String host;
    private String username;
    private String password;

    public Integer port = 21;
    public Integer timeout = 20000;

    public uFTPClient() {
    }

    public uFTPClient(String url) {
        this.host = url;
    }

    public uFTPClient(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public Map<String, String> put(String workingDirectory, String localFilename, String remoteFilename) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            File localFile = new File(localFilename);
            InputStream inputStream = new FileInputStream(localFile);
            return this.put(workingDirectory,inputStream,remoteFilename);
        } catch (IOException e) {
            LogUtil.log("Error: " + e.getMessage());
            resultMap.put("code", "500");
            resultMap.put("result", ExceptionUtil.getFullStackTrace(e));
            return resultMap;
        }
    }

    public Map<String, String> put(String workingDirectory, InputStream inputStream, String remoteFilename) {
        Map<String, String> resultMap = new HashMap<>();
        boolean status;
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(this.host, this.port);
            ftpClient.login(this.username, this.password);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(workingDirectory);

            OutputStream outputStream = ftpClient.storeFileStream(remoteFilename);
            byte[] bytesIn = new byte[8192];
            int read;

            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();

            status = ftpClient.completePendingCommand();

            String json = String.format("{\"directory\":\"%s\",\"filename\":\"%s\"}", workingDirectory, remoteFilename);
            resultMap.put("result", json);

            if(status){
                resultMap.put("code", "200");
            } else {
                resultMap.put("code", "500");
            }

        } catch (IOException e) {
            LogUtil.log("Error: " + e.getMessage());
            resultMap.put("code", "500");
            resultMap.put("result", ExceptionUtil.getFullStackTrace(e));
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                resultMap.put("code", "500");
                resultMap.put("result", ExceptionUtil.getFullStackTrace(e));
            }
        }

        return resultMap;
    }



}
