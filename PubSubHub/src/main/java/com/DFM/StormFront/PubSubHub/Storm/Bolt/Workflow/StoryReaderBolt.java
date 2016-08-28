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
import com.DFM.StormFront.Exec.StoryReaderExec;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.PubSubHub.Util.StormUtil;
import com.DFM.StormFront.Util.ExceptionUtil;

import java.util.HashMap;
import java.util.Map;

public class StoryReaderBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;

    private static Publisher _publisher = new Publisher();
    private static RedisClient _redisClient = new RedisClient();
    private static String _xsltRootPath = "";
    private static String _sStory = "";
    private static String _feedKey = "";
    private static String _contentKey = "";

    private static Fields _fields = new Fields("storyGUID", "contentKey", "story", "subjectsXML", "bNewChanged", "feedKey", "publisher");
    private static Values _values;
    private static Tuple _tuple;
    private static Map<String, Object> _stormConf;
    private static Map<String, Object> _linearConf;
    private static boolean _isLinear = false;
    private static OutputCollector _collector;

    private static void configure(Map conf) throws Exception {
        _linearConf = conf;
        _isLinear = true;
        //Get feed and article objects
        Map<String, String> mainConf = (Map) _linearConf.get("mainConf");
        _redisClient = new RedisClient(mainConf);
        _xsltRootPath = mainConf.get("xsltRootPath");
        _sStory = (String) _linearConf.get("story");
        _feedKey = (String) _linearConf.get("feedKey");
        _publisher = new Publisher((byte[]) _linearConf.get("publisher"));
        _sStory = _sStory.replace("<" + _publisher.getItemElement() + ">", _publisher.getItemOutRoot());
    }

    private static Map<String, String> run() throws Exception {

        Map<String, String> resultMap;
        StoryReaderExec storyReader = new StoryReaderExec();
        storyReader.setPublisher(_publisher);
        storyReader.setRedisClient(_redisClient);
        storyReader.setsStory(_sStory);
        storyReader.setXsltRootPath(_xsltRootPath);

        resultMap = storyReader.Exec();

        //if (Boolean.valueOf(resultMap.get("new/changed"))) {
            return emit(resultMap.get("storyGUID"), resultMap.get("contentKey"), resultMap.get("story"), resultMap.get("subjectsXML"), Boolean.valueOf(resultMap.get("new/changed")));
        //}
    }

    private static Map<String, String>  emit(String storyGUID, String contentKey, String sStory, String subjectsXML, Boolean bNewChanged) throws Exception {
        if (_isLinear) {
            _values = new Values(storyGUID, contentKey, sStory, subjectsXML, bNewChanged, _linearConf.get("feedKey"), _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            return SubscriberBolt.linear(outConf);
        } else if (_tuple != null) {
            _values = new Values(storyGUID, contentKey, sStory, subjectsXML, bNewChanged, _tuple.getStringByField("feedKey"), _tuple.getBinaryByField("publisher"));
            _collector.emit(_tuple, _values);

        } else {
            String msg = "StoryReaderBolt: both conf and tuple are null";
            StormUtil.logFail(msg, _redisClient);
        }

        String msg = "StoryReaderBolt: both conf and tuple are null";
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "500");
        resultMap.put("result", msg);
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

    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _stormConf = conf;
        _xsltRootPath = (String) conf.get("xsltRootPath");
        _collector = collector;
    }

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _isLinear = false;
        _redisClient = new RedisClient(_stormConf);
        //Get feed and article objects
        _sStory = _tuple.getStringByField("story");
        _feedKey = _tuple.getStringByField("feedKey");
        _publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _sStory = _sStory.replace("<" + _publisher.getItemElement() + ">", _publisher.getItemOutRoot());
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
