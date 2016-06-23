package com.DFM.StormFront.Client;

import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WebClient {
    private String url;
    private Integer port;
    private Integer timeout;
    private String username;
    private String password;

    public WebClient() {
    }

    public WebClient(String url) {
        this.url = url;
        this.port = 80;
        this.timeout = 20000;
        this.username = "";
        this.password = "";
    }

    public WebClient(String url, Integer port, Integer timeout, String username, String password) {
        this.url = url;
        this.port = port;
        this.timeout = timeout;
        this.username = username;
        this.password = password;
    }

    public WebClient(Publisher publisher) {
        this.url = publisher.getUrl();
        this.port = 80;
        this.timeout = 20000;
        if (publisher.getUsername() != null) {
            this.username = publisher.getUsername();
            this.password = publisher.getPassword();
        }
    }

    public WebClient(String url, String username, String password) {
        this.url = url;
        this.port = 80;
        this.timeout = 20000;
        this.username = username;
        this.password = password;
    }

    public static Boolean isOK(Integer code) {
        return code >= 200 && code < 300;
    }

    public static Boolean isBad(Integer code) {
        return !isOK(code);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get() throws IOException {
        BufferedReader reader = null;
        HttpResponse response = null;
        String result = "";
        try {
            String credentials = this.username + ":" + this.password;
            String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
            while ((credentials64.length() % 4) > 0) {
                credentials64 += "=";
            }
            String authorizationString = "Basic " + credentials64;

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(this.url);
            request.setHeader("Authorization", authorizationString);

            response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LogUtil.log("GET ERROR: " + ExceptionUtil.getFullStackTrace(e) + " for " + this.url);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
                if (null != response) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (Exception e) {
                LogUtil.log("GET ERROR (final): " + ExceptionUtil.getFullStackTrace(e) + " for " + this.url);
                Logger.getLogger(WebClient.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        return result;
    }

    public HashMap<String, String> get(String url, HashMap<String, String> headers) throws IOException {
        this.setUrl(url);
        return this.get(headers);
    }


    public HashMap<String, String> get(HashMap<String, String> headers) throws IOException {
        HashMap<String, String> resultMap = new HashMap<>();
        BufferedReader reader = null;
        HttpResponse response = null;
        String result = "";
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(this.getUrl());
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(new BasicHeader(header.getKey(), header.getValue()));
            }

            response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LogUtil.log("GET ERROR: " + ExceptionUtil.getFullStackTrace(e) + " for " + this.url);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                LogUtil.log("GET ERROR (final): " + ExceptionUtil.getFullStackTrace(e) + " for " + this.url);
                Logger.getLogger(WebClient.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        resultMap.put("code", "200");
        resultMap.put("result", result);
        return resultMap;
    }

    public HashMap<String, String> put(String url, HashMap<String, String> headers, String json) throws IOException {
        this.setUrl(url);
        return this.put(headers, json);
    }

    public HashMap<String, String> put(HashMap<String, String> headers, String json) throws IOException {
        HashMap<String, String> resultMap = new HashMap<>();
        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        //if (1==11) LogUtil.log("PUT: " + url);
        HttpPut put = new HttpPut(this.url);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            put.setHeader(new BasicHeader(header.getKey(), header.getValue()));
        }
        //if (1==11) LogUtil.log("JSON: " + json);
        StringEntity input = new StringEntity(json);
        put.setEntity(input);
        HttpResponse response = client.execute(put);
        StatusLine sl = response.getStatusLine();
        if (sl.getStatusCode() >= 200 && sl.getStatusCode() < 300) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        } else {
            result = sl.getReasonPhrase();
        }
        resultMap.put("code", Integer.toString(sl.getStatusCode()));
        resultMap.put("result", result);
        return resultMap;
    }

    public HashMap<String, String> post(String endpoint, String json) throws IOException {
        String result = "";
        String url = endpoint;
        if (2 == 2) LogUtil.log("No Creds URL: " + url);
        String credentials = this.getUsername() + ":" + this.getPassword();
        //if (1==11) LogUtil.log("credentials: " + credentials);
        String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
        while ((credentials64.length() % 4) > 0) {
            credentials64 += "=";
        }
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Basic " + credentials64);
        post.setHeader(new BasicHeader("Content-Type", "application/json"));
        post.setHeader(new BasicHeader("Accept", "application/json"));
        //if (1==11) LogUtil.log("JSON: " + json);
        StringEntity input = new StringEntity(json);
        post.setEntity(input);
        HttpResponse response = client.execute(post);
        StatusLine sl = response.getStatusLine();
        if (WebClient.isOK(sl.getStatusCode())) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        } else {
            result = sl.getReasonPhrase();
        }
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("code", Integer.toString(sl.getStatusCode()));
        resultMap.put("result", result);
        return resultMap;
    }

    public HashMap<String, String> delete(String endpoint) throws IOException {
        HashMap<String, String> resultMap = new HashMap<>();
        String result = "";
        String url = endpoint;
        String credentials = this.getUsername() + ":" + this.getPassword();
        //if (1==11) LogUtil.log("credentials: " + credentials);
        String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
        while ((credentials64.length() % 4) > 0) {
            credentials64 += "=";
        }
        HttpClient client = HttpClientBuilder.create().build();
        if (9 == 9) LogUtil.log("DELETE: " + url);
        HttpDelete delete = new HttpDelete();
        delete.setHeader("Authorization", "Basic " + credentials64);
        delete.setHeader(new BasicHeader("Content-Type", "application/json"));
        delete.setHeader(new BasicHeader("Accept", "application/json"));
        try {
            if (9 == 9) LogUtil.log("DELETE start: " + url);
            HttpResponse response = client.execute(delete);
            if (9 == 9) LogUtil.log("DELETE end: " + url);
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() >= 200 && sl.getStatusCode() < 300) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result += line;
                }
            } else {
                result = sl.getReasonPhrase();
            }
            resultMap.put("code", Integer.toString(sl.getStatusCode()));
            resultMap.put("result", result);
        } catch (java.lang.NullPointerException e) {
            if (9 == 9) LogUtil.log("DELETE ERROR: " + ExceptionUtil.getFullStackTrace(e));
            resultMap.put("code", "500");
            resultMap.put("result", ExceptionUtil.getFullStackTrace(e));
        }
        return resultMap;
    }

    public HashMap<String, String> delete(HashMap<String, String> headers) throws IOException {
        HashMap<String, String> resultMap = new HashMap<>();
        String result = "";
        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete delete = new HttpDelete(this.url);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            delete.setHeader(new BasicHeader(header.getKey(), header.getValue()));
        }
        HttpResponse response = client.execute(delete);
        StatusLine sl = response.getStatusLine();
        if (sl.getStatusCode() >= 200 && sl.getStatusCode() < 300) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
        } else {
            result = sl.getReasonPhrase();
        }
        resultMap.put("code", Integer.toString(sl.getStatusCode()));
        resultMap.put("result", result);
        return resultMap;
    }

    public InputStream GetImageStream(String imageSource, String imageType) throws IOException {
        URL url = new URL(imageSource);
        BufferedImage image = ImageIO.read(url);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, os);
        return new ByteArrayInputStream(os.toByteArray());
    }


    public BufferedImage GetImage(String imageSource) throws IOException {
        BufferedImage image;
        URL imageURL = new URL(imageSource);
        image = ImageIO.read(imageURL);
        return image;
    }


    public BufferedImage GetImage() throws IOException {
        BufferedImage image;
        URL imageURL = new URL(this.url);
        image = ImageIO.read(imageURL);
        return image;
    }

}