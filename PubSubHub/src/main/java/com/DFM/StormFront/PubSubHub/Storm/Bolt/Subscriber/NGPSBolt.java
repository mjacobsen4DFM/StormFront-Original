package com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Exec.NGPSExec;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.PubSubHub.Util.StormUtil;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.SerializationUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 4/12/2016.
 */
public class NGPSBolt extends BaseRichBolt {

    private static Publisher _publisher = new Publisher();
    private static RedisClient _redisClient = new RedisClient();
    private static String _xsltRootPath = "";
    private static String _sStory = "";
    private static String _feedKey = "";
    private static String _contentKey = "";
    private static String _subscriberFeed = "";
    private static String _deliveredStoryKey = "";
    private static String _subjectsXML = "";
    private static Boolean _bNewChanged = false;
    private static Map<String, String> _subscriberMap = new HashMap<>();
    private static String[] _keyArgs = {};

    private static Tuple _tuple;
    private static Map<String, Object> _stormConf;
    private static OutputCollector _collector;

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _redisClient = new RedisClient(_stormConf);
        //Get feed and article objects
        _contentKey = _tuple.getStringByField("contentKey");
        _bNewChanged = _tuple.getBooleanByField("bNewChanged");
        _subscriberFeed = _tuple.getStringByField("subscriberFeed");
        _subscriberMap = (Map<String, String>) SerializationUtil.deserialize(_tuple.getBinaryByField("subscriberMap"));
        _sStory = _tuple.getStringByField("story");
        _subjectsXML = _tuple.getStringByField("subjectsXML");
        _feedKey = _tuple.getStringByField("feedKey");
        _publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _keyArgs = new String[]{"delivered", _subscriberMap.get("type"), _subscriberMap.get("name"), _publisher.getSource(), _contentKey};
        _deliveredStoryKey = RedisContentUtil.setKey(_keyArgs);
    }


    private static void configure(Map conf) throws Exception {
        //Get feed and article objects
        Map<String, String> mainConf = (Map) conf.get("mainConf");
        _redisClient = new RedisClient(mainConf);
        _xsltRootPath = mainConf.get("xsltRootPath");
        _contentKey = (String) conf.get("contentKey");
        _bNewChanged = (Boolean) conf.get("bNewChanged");
        _subscriberFeed = (String) conf.get("subscriberFeed");
        _subscriberMap = (Map<String, String>) SerializationUtil.deserialize((byte[]) conf.get("subscriberMap"));
        _sStory = (String) conf.get("story");
        _subjectsXML = (String) conf.get("subjectsXML");
        _feedKey = (String) conf.get("feedKey");
        _publisher = new Publisher((byte[]) conf.get("publisher"));
        _keyArgs = new String[]{"delivered", _subscriberMap.get("type"), _subscriberMap.get("name"), _publisher.getSource(), _contentKey};
        _deliveredStoryKey = RedisContentUtil.setKey(_keyArgs);
    }


    private static Map<String, String> run() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        if (2 == 12) {LogUtil.log("getFeedType: " + _publisher.getFeedType() + " deliveredStoryKey: " + _deliveredStoryKey);}

        NGPSExec ngps = new NGPSExec();

        try {
            if(_bNewChanged) {
                ngps = new NGPSExec(
                        _publisher,
                        _redisClient,
                        _subscriberMap,
                        _deliveredStoryKey,
                        _feedKey,
                        _contentKey,
                        _subjectsXML,
                        _xsltRootPath,
                        _sStory
                );
                resultMap = ngps.Exec();
            }
        } catch (Exception e) {
            String errMsg = "Operation: " + ngps.operation + ", " + ExceptionUtil.getFullStackTrace(e) + " into: NGPS";
            throw new Exception(errMsg);
        }
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
        if (_deliveredStoryKey != null && RedisContentUtil.getMD5(_deliveredStoryKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_deliveredStoryKey, _redisClient);
        }
        if (_contentKey != null && RedisContentUtil.getMD5(_contentKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_contentKey, _redisClient);
        }
        if (_feedKey != null && RedisContentUtil.getMD5(_feedKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_feedKey, _redisClient);
        }

        if(_contentKey != null) { _redisClient.hdel(_contentKey, _subscriberFeed); }

        fail();
    }

    public static Map<String, String> linear(Map conf) throws Exception {
        try {
            configure(conf);
            return run();
        } catch (Exception e) {
            fail(ExceptionUtil.getFullStackTrace(e));
            throw e;
        }
    }

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _stormConf = conf;
        _xsltRootPath = (String) conf.get("xsltRootPath");
        _collector = collector;
    }

    public void execute(Tuple tuple) {
        try {
            if (2 == 12) LogUtil.log("Entered NGPSBolt");
            configure(tuple);
            run();
            ack();
        } catch (Exception e) {
            fail(ExceptionUtil.getFullStackTrace(e));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
