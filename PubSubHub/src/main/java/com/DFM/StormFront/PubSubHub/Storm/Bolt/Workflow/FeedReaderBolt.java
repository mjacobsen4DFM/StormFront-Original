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
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Exec.FeedReaderExec;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;

import java.util.ArrayList;
import java.util.Map;


public class FeedReaderBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;

    private static Publisher _publisher = new Publisher();
    private static RedisClient _redisClient = new RedisClient();
    private static String _sStories = "";
    private static String _feedKey = "";
    private static String _phaseType = "";

    private static Fields _fields = new Fields("story", "feedKey", "publisher");
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
        _sStories = (String) _linearConf.get("stories");
        _publisher = new Publisher((byte[]) _linearConf.get("publisher"));
        _phaseType = _publisher.getPhaseType();
        _feedKey = (String) _linearConf.get("feedKey");
    }

    private static void run() throws Exception {
        FeedReaderExec feedReader = new FeedReaderExec(_publisher, _redisClient, _sStories, _feedKey);
        ArrayList<String> storyList = feedReader.Exec();
        for (String story : storyList) {
            if (2 == 12) LogUtil.log("FeedReaderBolt->phaseType: " + _phaseType);
            emit(story);
        }
    }

    private static void emit(String sStory) throws Exception {
        if (_isLinear) {
            _values = new Values(sStory, _linearConf.get("feedKey"), _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            if (_phaseType.equalsIgnoreCase("SinglePhase")) {
                SinglePhaseLoadBolt.linear(outConf);
            }
            if (_phaseType.equalsIgnoreCase("MultiPhase")) {
                MultiPhaseLoadBolt.linear(outConf);
            }
        } else if (_tuple != null) {
            _values = new Values(sStory, _tuple.getStringByField("feedKey"), _tuple.getBinaryByField("publisher"));
            _collector.emit(_phaseType, _tuple, _values);
        } else {
            String msg = "FeedReaderBolt: both conf and tuple are null";
            RedisLogUtil.logError(msg, _redisClient);
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
        RedisLogUtil.logError(msg, _redisClient);
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

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _isLinear = false;
        _redisClient = new RedisClient(_stormConf);
        //Get feed and article objects
        _sStories = tuple.getStringByField("stories");
        _publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _phaseType = _publisher.getPhaseType();
        _feedKey = _tuple.getStringByField("feedKey");
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
        declarer.declareStream("SinglePhase", _fields);
        declarer.declareStream("MultiPhase", _fields);
    }
}
