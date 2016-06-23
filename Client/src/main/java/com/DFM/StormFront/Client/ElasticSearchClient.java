package com.DFM.StormFront.Client;

import com.DFM.StormFront.Util.LogUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ElasticSearchClient implements Serializable {
    private WebClient webClient = new WebClient();

    public ElasticSearchClient() {
    }

    public ElasticSearchClient(String url) {
        this.webClient.setUrl(url);
    }

    public ElasticSearchClient(String url, Integer port) {
        this.webClient.setUrl(url);
        this.webClient.setPort(port);
    }

    public ElasticSearchClient(String url, Integer port, String username, String password) {
        this.webClient.setUrl(url);
        this.webClient.setPort(port);
        this.webClient.setUsername(username);
        this.webClient.setPassword(password);
    }

    public static ElasticSearchClient NewClient(Map<String, String> subscriberMap) throws Exception {
        if (subscriberMap.containsKey("AM") && subscriberMap.get("AM").equalsIgnoreCase("U")) {
            if (2 == 12) {
                LogUtil.log("DISCOVERED OAUTH: '" + subscriberMap.get("AM") + "': NOT SUPPORTED!");
            }
            return new ElasticSearchClient(subscriberMap.get("url"),Integer.parseInt(subscriberMap.get("port")), subscriberMap.get("stamp"), subscriberMap.get("validity"));
        } else if (subscriberMap.containsKey("AM") && subscriberMap.get("AM").equalsIgnoreCase("B")) {
            if (2 == 12)
                LogUtil.log("DISCOVERED BASIC: '" + subscriberMap.get("AM").toUpperCase() + "'");
            //return new ElasticSearchClient(subscriberMap.get("url"),Integer.parseInt(subscriberMap.get("port")), subscriberMap.get("stamp"), subscriberMap.get("validity"));
        } else {
            if (3 == 13) {
                LogUtil.log("DISCOVERED NOTHING: '" + subscriberMap.get("AM").toUpperCase() + "'");
            }
            throw new Exception("WordPressClient type is unknown. Access type requested: " + subscriberMap.get("AM"));
        }
        return null;
    }

    public HashMap<String, String> get(String endpoint) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        String url = String.format("%s:%s%s", this.webClient.getUrl(), this.webClient.getPort(), endpoint);
        String credUrl = url.replace("://", String.format("://%s:%s@", this.webClient.getUsername(), this.webClient.getPassword()));

        if (1==1) LogUtil.log("GET: " + credUrl);


        WebClient localWebClient = new WebClient(
                this.webClient.getUrl(),
                this.webClient.getPort(),
                this.webClient.getTimeout(),
                this.webClient.getUsername(),
                this.webClient.getPassword()
        );
        localWebClient.setUrl(credUrl);
        headers.put("Accept", "application/json");
        return localWebClient.get(headers);
    }

    public HashMap<String, String> put(String endpoint, String json) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        String url = String.format("%s:%s%s", this.webClient.getUrl(), this.webClient.getPort(), endpoint);
        String credUrl = url.replace("://", String.format("://%s:%s@", this.webClient.getUsername(), this.webClient.getPassword()));

        if (1==1) LogUtil.log("PUT: " + credUrl);
        if (1==1) LogUtil.log("JSON: " + json);

        WebClient localWebClient = new WebClient(
                this.webClient.getUrl(),
                this.webClient.getPort(),
                this.webClient.getTimeout(),
                this.webClient.getUsername(),
                this.webClient.getPassword()
        );
        localWebClient.setUrl(credUrl);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return localWebClient.put(headers, json);
    }



    public HashMap<String, String> delete(String endpoint) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        String url = String.format("%s:%s%s", this.webClient.getUrl(), this.webClient.getPort(), endpoint);
        String credUrl = url.replace("://", String.format("://%s:%s@", this.webClient.getUsername(), this.webClient.getPassword()));

        if (1==1) LogUtil.log("DELETE: " + credUrl);


        WebClient localWebClient = new WebClient(
                this.webClient.getUrl(),
                this.webClient.getPort(),
                this.webClient.getTimeout(),
                this.webClient.getUsername(),
                this.webClient.getPassword()
        );
        localWebClient.setUrl(credUrl);
        headers.put("Accept", "application/json");
        return localWebClient.delete(headers);
    }
}