package com.DFM.StormFront.PubSubHub.Storm.Bolt.Workflow;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.ElasticSearchBolt;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.NGPSBolt;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.WordPressBolt;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.PubSubHub.Util.StormUtil;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.SerializationUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

public class SubscriberBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;

    private static RedisClient _redisClient = new RedisClient();
    private static String _storyGUID = "";
    private static String _feedKey = "";
    private static String _contentKey = "";
    private static String _publisherSource = "";
    private static String _subscriberSearch = "";
    private static ArrayList<String> _subscribers = new ArrayList<>();

    private static Fields _fields = new Fields("storyGUID", "subscriberMap", "contentKey", "story", "subjectsXML", "bNewChanged", "feedKey", "publisher");
    private static Values _values;
    private static Tuple _tuple;
    private static Map<String, Object> _stormConf;
    private static Map<String, Object> _linearConf;
    private static boolean _isLinear = false;
    private static OutputCollector _collector;

    private static void configure(Map conf) throws Exception {
        _linearConf = conf;
        _isLinear = true;
        Map<String, String> mainConf = (Map) _linearConf.get("mainConf");
        Publisher publisher = new Publisher((byte[]) _linearConf.get("publisher"));
        _redisClient = new RedisClient(mainConf);
        _storyGUID = (String) _linearConf.get("storyGUID");
        _feedKey = (String) _linearConf.get("feedKey");
        _contentKey = (String) conf.get("contentKey");
        _publisherSource = publisher.getPubKey().replace("publishers:", "");
        String[] keyArgs = new String[]{"subscribers", "*", _publisherSource};
        _subscriberSearch = RedisContentUtil.setKey(keyArgs);
        _subscribers = _redisClient.keys(_subscriberSearch);
    }

    private static Map<String, String> run() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> subscribeMap = new HashMap<>();
        Map<String, String> subscriberPropMap = new HashMap<>();
        boolean bError = false;
        String resultJson = "";
        for (String subscriberFeed : _subscribers) {
            try {
                String subscriberBase = subscriberFeed.replace(_publisherSource, "");
                subscriberBase = StringUtils.removeEnd(subscriberBase, ":");
                String subscriberProp = subscriberBase + ":Properties";
                Map<String, String> subscriberMap = _redisClient.hgetAll(subscriberFeed);
                subscriberMap.put("subscriberFeed", subscriberFeed);
                subscriberMap.put("subscriberBase", subscriberBase);
                subscriberMap.put("subscriberProp", subscriberProp);
                subscriberMap.put("subscriberSearch", _subscriberSearch);
                Boolean subscriptionActive = Boolean.parseBoolean(subscriberMap.get("active"));

                subscriberPropMap = _redisClient.hgetAll(subscriberProp);
                Boolean subscriberActive = Boolean.parseBoolean(subscriberPropMap.get("active"));
                subscriberMap.putAll(subscriberPropMap);

                String subscriberType = subscriberMap.get("type");
                if (2 == 2) {
                    LogUtil.log("Publisher->source: " + _publisherSource + ", SubscriberBolt->subscriberType: " + subscriberType + ", _storyGUID: " + _storyGUID + " Active: " + subscriberActive);
                }
                if (subscriberActive && subscriptionActive) {
                    byte[] binarySubscriberMap = SerializationUtil.serialize(subscriberMap);
                    subscribeMap = emit(binarySubscriberMap, subscriberType);
                    if (isNumber(subscribeMap.get("code")) && WebClient.isBad(Integer.parseInt(subscribeMap.get("code")))) {
                        bError = true;
                    }

                    String subscriberResultJson;
                    if (JsonUtil.isWellFormed(subscribeMap.get("result"))) {
                        subscriberResultJson = subscribeMap.get("result");
                    } else {
                        subscriberResultJson = String.format("\"%s\"", subscribeMap.get("result"));
                    }

                    resultJson = String.format("{\"response\":\"%s\", \"result\":%s}", subscribeMap.get("code"), subscriberResultJson);
                    resultMap.put(subscriberPropMap.get("type"), resultJson);
                } else {
                    resultJson = String.format("{\"response\":\"%s\", \"result\":\"%s\"}", "200", "inactive");
                    resultMap.put(subscriberPropMap.get("type"), resultJson);
                }
            } catch (Exception e) {
                bError = true;
                resultJson = String.format("{\"response\":\"%s\", \"error\":\"%s\"}", "500", JsonUtil.toJSON(ExceptionUtil.getFullStackTrace(e)));
                resultMap.put(subscriberPropMap.get("type"), resultJson);
            }
        }

        if (0 == _subscribers.size()) {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("message", "No subscribers; subkey: " + _subscriberSearch);
            String msg = jsonobject.toString();
            resultMap.put("result", msg);
        }

        if (bError) {
            resultMap.put("code", "500");
        } else {
            resultMap.put("code", "200");
        }
        return resultMap;
    }

    private static Map<String, String> emit(byte[] binarySubscriberMap, String subscriberType) throws Exception {
        if (_isLinear) {
            _values = new Values(_storyGUID, binarySubscriberMap, _contentKey, _linearConf.get("story"), _linearConf.get("subjectsXML"), _linearConf.get("bNewChanged"), _linearConf.get("feedKey"), _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            if (subscriberType.equalsIgnoreCase("WordPress")) {
                return WordPressBolt.linear(outConf);
            }
            if (subscriberType.equalsIgnoreCase("NGPS")) {
                return NGPSBolt.linear(outConf);
            }
            if (subscriberType.equalsIgnoreCase("ElasticSearch")) {
                return ElasticSearchBolt.linear(outConf);
            }
        } else if (_tuple != null) {
            _values = new Values(_storyGUID, binarySubscriberMap, _contentKey, _tuple.getStringByField("story"), _tuple.getStringByField("subjectsXML"), _tuple.getBooleanByField("bNewChanged"), _tuple.getStringByField("feedKey"), _tuple.getBinaryByField("publisher"));
            _collector.emit(subscriberType, _tuple, _values);

        } else {
            String msg = "???: both conf and tuple are null";
            StormUtil.logFail(msg, _redisClient);
        }

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "500");
        resultMap.put("result", "Unknown subscriberType: " + subscriberType);
        return resultMap;
    }

    private static void ack() {
        if (_tuple != null) {
            _collector.ack(_tuple);
        }
    }

    private static void fail() {
        if (_tuple != null) {
            _collector.fail(_tuple);
        }
    }

    private static void fail(String msg) {
        StormUtil.logFail(msg, _redisClient);
        if (_contentKey != null && RedisContentUtil.getMD5(_contentKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_contentKey, _redisClient);
        }
        if (_feedKey != null && RedisContentUtil.getMD5(_feedKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_feedKey, _redisClient);
        }
        fail();
    }

    public static Map<String, String> linear(Map inConf) throws Exception {
        try {
            configure(inConf);
            return run();
        } catch (Exception e) {
            fail(ExceptionUtil.getFullStackTrace(e));
            throw e;
        }
    }

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _isLinear = false;
        //Get feed and article objects
        Publisher publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _redisClient = new RedisClient(_stormConf);
        _storyGUID = _tuple.getStringByField("storyGUID");
        _feedKey = _tuple.getStringByField("feedKey");
        _contentKey = _tuple.getStringByField("contentKey");
        _publisherSource = publisher.getPubKey().replace("publishers:", "");
        String[] keyArgs = new String[]{"subscribers", "*", _publisherSource};
        _subscriberSearch = RedisContentUtil.setKey(keyArgs);
        _subscribers = _redisClient.keys(_subscriberSearch);
    }

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _stormConf = conf;
        _collector = collector;
    }

    public void execute(Tuple tuple) {
        try {
            configure(tuple);
            run();
            ack();
        } catch (Exception e) {
            fail(ExceptionUtil.getFullStackTrace(e));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("WordPress", _fields);
        declarer.declareStream("NGPS", _fields);
        declarer.declareStream("ElasticSearch", _fields);
        //declarer.declareStream("Other", _fields);
    }
}
