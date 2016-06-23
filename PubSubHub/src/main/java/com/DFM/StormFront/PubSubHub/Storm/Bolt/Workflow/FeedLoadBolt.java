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
import com.DFM.StormFront.Exec.FeedLoadExec;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.StringUtil;

import java.util.Map;

//import com.sun.jndi.toolkit.url.Uri;


public class FeedLoadBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;

    private static RedisClient _redisClient = new RedisClient();
    private static Publisher _publisher = new Publisher();
    private static String[] _keyArgs = {};

    private static Fields _fields = new Fields("stories", "feedKey", "publisher");
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
        _publisher = new Publisher((byte[]) _linearConf.get("publisher"));
        _keyArgs = new String[]{"feeds", _publisher.getPubKey().replace("publishers:", "")};
    }

    private static void run() throws Exception {
        if(null == _publisher.getUrl()) { return; }

        String feedKey = RedisContentUtil.setKey(_keyArgs);
        if (2 == 12) LogUtil.log("FeedLoadBolt->feedKey: " + feedKey);

        FeedLoadExec feedLoad = new FeedLoadExec(_publisher);
        String sStories = feedLoad.Exec();

        if (StringUtil.isNullOrEmpty(sStories)) {
            sStories = String.format("<%s/>", _publisher.getItemElement());
        }
        emit(sStories, feedKey);
    }

    private static void emit(String sStories, String feedKey) throws Exception {
        if (_isLinear) {
            _values = new Values(sStories, feedKey, _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            FeedReaderBolt.linear(outConf);
        } else if (_tuple != null) {
            _values = new Values(sStories, feedKey, _tuple.getBinaryByField("publisher"));
            _collector.emit(_tuple, _values);

        } else {
            String msg = "FeedLoadBolt: both conf and tuple are null";
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
        _publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _keyArgs = new String[]{"feeds", _publisher.getPubKey().replace("publishers:", "")};
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
