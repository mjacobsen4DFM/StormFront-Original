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
import com.DFM.StormFront.Model.Normalize.Story;
import com.DFM.StormFront.Model.Normalize.StoryLinks;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.PubSubHub.Util.StormUtil;
import com.DFM.StormFront.Util.ExceptionUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.XmlUtil;

import java.util.HashMap;
import java.util.Map;

public class MultiPhaseLoadBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1L;

    private static RedisClient _redisClient = new RedisClient();
    private static Publisher _publisher = new Publisher();
    private static String _xsltRootPath = "";
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
        _xsltRootPath = mainConf.get("xsltRootPath");
        //Get feed and article objects
        _publisher = new Publisher((byte[]) _linearConf.get("publisher"));
        _sStory = (String) _linearConf.get("story");
        _feedKey = (String) _linearConf.get("feedKey");
    }

    private static void run() throws Exception {
        if (2 == 12) LogUtil.log("MultiPhaseLoadBolt->phaseTypeBOLT: MultiPhase");
        //Get feed and article objects
        Map<String, Object> statusMap;
        String xsltPath = _xsltRootPath + _publisher.getFeedType() + "_MultiPhase.xslt";
        String storyXML = XmlUtil.transform(_sStory, xsltPath);
        Story story = Story.fromXML(storyXML);
        StoryLinks storyLinks = story.storyLinks;
        String[] links = storyLinks.getLinks();
        String storyGuid = story.getGuid();
        String[] keyArgs = {"content", _publisher.getSourceType(), storyGuid};
        String contentKey = RedisContentUtil.setKey(keyArgs);
        Map<String, String> contentMap = new HashMap<>();
        WebClient client = new WebClient(_publisher);
        String phaseStory = "";
        String updateCheck;
        String sourceURL;

        updateCheck = story.getupdateCheck();
        if (updateCheck != null && !updateCheck.equals("")) {
            statusMap = RedisContentUtil.createStoryStatusMap(contentKey, "updateCheck", updateCheck, _redisClient);
            if ((Boolean) statusMap.get("isNew") | (Boolean) statusMap.get("isUpdated")) {
                for (String link : links) {
                    sourceURL = link.trim();
                    contentMap.put("sourceURL", sourceURL);
                    client.setUrl(sourceURL);
                    Map<String, String> resultMap = client.get();
                    if (WebClient.isBad(Integer.valueOf(resultMap.get("code")))) {
                        throw new Exception("WebClient error: " + sourceURL + " Code: " + resultMap.get("code") + " Reason: " + resultMap.get("body"));
                    }
                    phaseStory += resultMap.get("body");
                }

                contentMap.put("updateCheck", story.getupdateCheck());
                contentMap.put("title", story.getTitle());
                RedisContentUtil.trackContent(contentKey, contentMap, _redisClient);

                emit(storyGuid, phaseStory);
            }
        }
    }

    private static void emit(String storyGuid, String phaseStory) throws Exception {
        if (_isLinear) {
            _values = new Values(storyGuid, phaseStory, _linearConf.get("feedKey"), _linearConf.get("publisher"));
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _linearConf.get("mainConf"));
            StoryReaderBolt.linear(outConf);
        } else if (_tuple != null) {
            _values = new Values(storyGuid, phaseStory, _tuple.getStringByField("feedKey"), _tuple.getBinaryByField("publisher"));
            _collector.emit(_tuple, _values);
        } else {
            String msg = "MultiPhaseLoadBolt: both conf and tuple are null";
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
        _xsltRootPath = (String) conf.get("xsltRootPath");
        _collector = collector;
    }

    private void configure(Tuple tuple) throws Exception {
        _tuple = tuple;
        _isLinear = false;
        _redisClient = new RedisClient(_stormConf);
        //Get feed and article objects
        _publisher = new Publisher(_tuple.getBinaryByField("publisher"));
        _sStory = _tuple.getStringByField("story");
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
        declarer.declare(_fields);
    }
}
