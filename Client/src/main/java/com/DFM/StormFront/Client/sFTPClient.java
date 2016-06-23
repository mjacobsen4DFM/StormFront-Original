package com.DFM.StormFront.Client;

import com.jcraft.jsch.*;

import java.io.*;

public class sFTPClient implements Serializable {
    private String host;
    private Integer port = 22;
    private Integer timeout = 20000;
    private String username;
    private String password;

    public sFTPClient() {
    }

    public sFTPClient(String url) {
        this.host = url;
    }

    public sFTPClient(String url, String username, String password) {
        this.host = url;
        this.username = username;
        this.password = password;
    }

    public sFTPClient(String url, String username, String password, Integer timeout) {
        this.host = url;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
    }

    public void put(String workingDirectory, String localFilename, String remoteFilename) {

        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession(this.username, this.host, this.port);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(this.password);
            session.setTimeout(this.timeout);

            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(workingDirectory);

            File file = new File(localFilename);
            sftpChannel.put(new FileInputStream(file), remoteFilename);
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException | FileNotFoundException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }
}