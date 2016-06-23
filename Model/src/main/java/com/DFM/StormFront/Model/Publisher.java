package com.DFM.StormFront.Model;

import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.SerializationUtil;

import java.io.Serializable;
import java.util.Map;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class Publisher implements Serializable {
    private static final long serialVersionUID = -4787117016512778268L;
    private String originator;
    private Integer pubid;
    private String pubKey;
    private String url;
    private String cat;
    private String feedType;
    private String source;
    private String sourceType;
    private String phaseType;
    private String updateCheckType;
    private String itemElement;
    private String itemOutRoot;
    private String itemNamespace;
    private String sortOrder;
    private String username;
    private String password;
    private String oauthkey;


    public Publisher() {
    }

    public Publisher(Map<String, String> keys) {
        this.originator = keys.get("originator");
        String pid = keys.get("pubid");
        if(isNumber(pid)) {
            this.pubid = Integer.parseInt(pid);
        } else {
            this.pubid = 0;
        }
        this.pubKey = keys.get("pubKey");
        this.url = keys.get("url");
        this.cat = keys.get("cat");
        this.itemElement = keys.get("itemElement");
        this.itemOutRoot = keys.get("itemOutRoot");
        this.itemNamespace = keys.get("itemNamespace");
        this.sortOrder = keys.get("reSortOrder");
        this.username = keys.get("stamp");
        this.password = keys.get("validity");
        this.oauthkey = keys.get("guid");
        this.feedType = keys.get("feedType");
        this.source = keys.get("source");
        this.sourceType = keys.get("sourceType");
        this.phaseType = keys.get("phaseType");
        this.updateCheckType = keys.get("updateCheckType");
    }

    public Publisher(byte[] serializedFeed) throws Exception {
        Publisher feed;
        try {
            feed = (Publisher) SerializationUtil.deserialize(serializedFeed);
        } catch (Exception e) {
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
        this.originator = feed.getOriginator();
        this.pubid = feed.getPubid();
        this.pubKey = feed.getPubKey();
        this.url = feed.getUrl();
        this.cat = feed.getCat();
        this.itemElement = feed.getItemElement();
        this.itemOutRoot = feed.getItemOutRoot();
        this.itemNamespace = feed.getItemNamespace();
        this.sortOrder = feed.getSortOrder();
        this.username = feed.getUsername();
        this.password = feed.getPassword();
        this.oauthkey = feed.getOauthkey();
        this.feedType = feed.getFeedType();
        this.source = feed.getSource();
        this.sourceType = feed.getSourceType();
        this.phaseType = feed.getPhaseType();
        this.updateCheckType = feed.getUpdateCheckType();
    }

    public Integer getPubid() {
        return pubid;
    }

    public void setPubid(Integer id) {
        this.pubid = id;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String feedKey) {
        this.pubKey = feedKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauthkey() {
        return oauthkey;
    }

    public void setOauthkey(String oauthkey) {
        this.oauthkey = oauthkey;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getItemElement() {
        return itemElement;
    }

    public void setItemElement(String itemElement) {
        this.itemElement = itemElement;
    }

    public String getItemOutRoot() {
        return itemOutRoot;
    }

    public void setItemOutRoot(String itemOutRoot) {
        this.itemOutRoot = itemOutRoot;
    }

    public String getPhaseType() {
        return phaseType;
    }

    public void setPhaseType(String phaseType) {
        this.phaseType = phaseType;
    }

    public String getItemNamespace() {
        return itemNamespace;
    }

    public void setItemNamespace(String itemNamespace) {
        this.itemNamespace = itemNamespace;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUpdateCheckType() {
        return updateCheckType;
    }

    public void setUpdateCheckType(String updateCheckType) {
        this.updateCheckType = updateCheckType;
    }
}
