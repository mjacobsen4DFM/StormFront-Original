package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Adapter.ElasticSearchAdapter;
import com.DFM.StormFront.Client.ElasticSearchClient;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Model.NGPS.Article;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.*;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 5/18/2016.
 */
public class ElasticSearchExec {
    public String operation;
    public String postLocation;
    public RedisClient redisClient;
    public ElasticSearchClient elasticSearchClient = new ElasticSearchClient();
    private String _story;
    private Map<String, String> _subscriberMap;
    private String _deliveredStoryKey;
    private String _contentKey;
    private String _xsltRootPath;


    public Publisher publisher;

    public ElasticSearchExec() {
    }


    public ElasticSearchExec(Publisher publisher,
                             RedisClient redisClient,
                             Map<String, String> subscriberMap,
                             String contentKey,
                             String deliveredStoryKey,
                             String xsltRootPath,
                             String story) throws Exception {
        this.publisher =  publisher;
        this.redisClient = redisClient;
        _subscriberMap = subscriberMap;
        this.postLocation = _subscriberMap.get("url") + _subscriberMap.get("endpoint");
        _contentKey = contentKey;
        _deliveredStoryKey = deliveredStoryKey;
        _xsltRootPath = xsltRootPath;
        _story = story;
        this.elasticSearchClient = ElasticSearchClient.NewClient(subscriberMap);
    }


    public Map<String, String> Exec() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        operation = "start";

        operation = "Transform XML";
        String xsltPath = _xsltRootPath + this.publisher.getFeedType() + "_" + _subscriberMap.get("type") + ".xslt";
        String esXML = XmlUtil.transform(_story, xsltPath);

        Article article = Article.fromXML(esXML);
        String sArticle = JsonUtil.toJSON(article);

/*
        FileUtil.printFile(FileUtil.getLogPath(), "ContentSync", article.getCId(), "story", "txt", _story);
        FileUtil.printFile(FileUtil.getLogPath(), "ContentSync", article.getCId(), "esXML", "txt", esXML);
*/
        FileUtil.printFile(FileUtil.getLogPath(), "ContentSync", article.getCId(), "sArticle", "txt", sArticle);

        String index = _subscriberMap.get("index");
        String index_type = _subscriberMap.get("index_type");
        String cId = article.getCId();
        String endpoint = String.format("/%s/%s/%s", index, index_type, cId);

        if (null == index || null == index_type || null == cId){
            resultMap.put("code", "406");
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("message", String.format("NULL value: index=%s, index_type=%s, cId=%s", index, index_type, cId));
            String msg = jsonobject.toString();
            resultMap.put("result", msg);
            return resultMap;
        }

        if (article.isDelete()) {
            resultMap = ElasticSearchAdapter.delete(endpoint, this.elasticSearchClient);
            clearPut();
        } else {
            resultMap = ElasticSearchAdapter.putJson(sArticle, endpoint, this.elasticSearchClient);
            recordPut(resultMap);
        }
        return resultMap;
    }

    public Map<String, String> Get() throws Exception {
        Map<String, String> resultMap;
        operation = "start";
        Article article = Article.fromXML(_story);
        String endpoint = String.format("/%s/%s/%s", _subscriberMap.get("index"), _subscriberMap.get("index_type"), article.getCId());
        resultMap = ElasticSearchAdapter.getJson(endpoint, this.elasticSearchClient);
        return resultMap;
    }

    private void recordPut(Map<String, String> resultMap) {
        Map<String, String> postMap = new HashMap<>();
        String result = resultMap.get("result");
        try {
            byte[] b5 = SerializationUtil.serialize(_story);
            String s5 = new String(b5, StandardCharsets.UTF_8);
            String m5 = RedisContentUtil.createMD5(s5);
            postMap.put("md5", m5);
            postMap.put("id", JsonUtil.getValue(result, "_id"));
            postMap.put("index", JsonUtil.getValue(result, "_index"));
            postMap.put("type", JsonUtil.getValue(result, "_type"));
            postMap.put("version", JsonUtil.getValue(result, "_version"));
            LogUtil.log("_deliveredStoryKey: " + _deliveredStoryKey + ", postMap: " + postMap.toString());
            RedisContentUtil.trackContent(_deliveredStoryKey, postMap, this.redisClient);
        } catch (Exception e) {
            postMap.put("error", ExceptionUtil.getFullStackTrace(e));
        }
    }

    private void clearPut() {
        if (RedisContentUtil.getMD5(_deliveredStoryKey, this.redisClient) != null) {
            RedisContentUtil.resetMD5(_deliveredStoryKey, this.redisClient);
        }
        if (RedisContentUtil.getMD5(_contentKey, this.redisClient) != null) {
            RedisContentUtil.resetMD5(_contentKey, this.redisClient);
        }
    }
}
