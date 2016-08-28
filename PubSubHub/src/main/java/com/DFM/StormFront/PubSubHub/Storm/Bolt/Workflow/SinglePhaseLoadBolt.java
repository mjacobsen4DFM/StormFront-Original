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
import com.DFM.StormFront.Model.Normalize.Story;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.PubSubHub.Util.StormUtil;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;

import java.util.Map;

public class SinglePhaseLoadBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;
    private static RedisClient _redisClient = new RedisClient();
    private static String _feedKey = "";
    private static String _sStory = "";
    private static Fields _fields = new Fields("storyGUID", "story", "feedKey", "publisher");
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
        _redisClient = new RedisClient(mainConf);
        //Get feed and article objects
        _feedKey = (String) _linearConf.get("feedKey");
        _sStory = (String) _linearConf.get("story");
    }

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _isLinear = false;
        _redisClient = new RedisClient(_stormConf);
        //Get feed and article objects
        _feedKey = _tuple.getStringByField("feedKey");
        _sStory = _tuple.getStringByField("story");
    }

    private static void run() throws Exception {
        if (2 == 12) { LogUtil.log("SinglePhaseLoadBolt->phaseTypeBOLT: SinglePhase");}
        //Simple pass-thru for when story content is within the feed
        Story story = Story.fromXML(_sStory);
        String storyGuid = story.getGuid();
        emit(storyGuid);
    }

    private static void emit(String storyGuid) throws Exception {
        if (_isLinear) {
            _values = new Values(storyGuid, _sStory, _feedKey, _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            StoryReaderBolt.linear(outConf);
        } else if (_tuple != null) {
            _values = new Values(storyGuid, _sStory, _feedKey, _tuple.getBinaryByField("publisher"));
            _collector.emit(_tuple, _values);
        } else {
            String msg = "SinglePhaseLoadBolt: both conf and tuple are null";
            StormUtil.logFail(msg, _redisClient);
        }
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
        if (_feedKey != null && RedisContentUtil.getMD5(_feedKey, _redisClient) != null) {
            RedisContentUtil.resetMD5(_feedKey, _redisClient);
        }
        fail();
    }

    public static void linear(Map inConf) throws Exception {
        try {
            configure(inConf);
            run();
            ack();
        } catch (Exception e) {
            fail(ExceptionUtil.getFullStackTrace(e));
            throw e;
        }
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
        declarer.declare(_fields);
    }
}
