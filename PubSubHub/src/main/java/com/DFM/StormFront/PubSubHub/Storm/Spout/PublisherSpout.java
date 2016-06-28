package com.DFM.StormFront.PubSubHub.Storm.Spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Workflow.FeedLoadBolt;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.*;

import java.util.*;


public class PublisherSpout extends BaseRichSpout {
    private static final long serialVersionUID = 1L;
    private static String _mode = "unknown";
    
    private static SpoutOutputCollector _spoutOutputCollector;
    private static String _publisherKeySearch = "";
    private static String _hostname = "Unknown";
    private static RedisClient _redisClient = new RedisClient();
    private static Publisher _publisher = new Publisher();
    private static String _pubKey = "";
    private static Map<String, String> _keys = new HashMap<>();
    private static ArrayList<String> _pubs;
    private static Queue<String> _pubQueue = new LinkedList<>();

    private static Fields _fields = new Fields("pubKey", "publisher");
    private static Values _values;
    private static Map<String, Object> _mainConf;

    public PublisherSpout(String publisherKeySearch){
        //LogUtil.log("newspout");
        _publisherKeySearch = publisherKeySearch;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector spoutOutputCollector) {
        //LogUtil.log("open");
        // Open the spout
        _mode = "storm";
        configure(conf);

        //Collect the rain
        _spoutOutputCollector = spoutOutputCollector;
    }

    private void configure(Map conf) {
        //LogUtil.log("configure");
        _mainConf = conf;
        _redisClient = new RedisClient(conf);
        _hostname = SystemUtil.getHostname();
        _publisherKeySearch = (String) conf.get("publisherKeySearch");
        //LogUtil.log("publisherKeySearch: " +  _publisherKeySearch);

        //Get the pubs from Redis
        _pubs = _redisClient.keys(_publisherKeySearch);

        //Populate the pubs list
        _pubQueue.addAll(_pubs);
    }

    private static void run() throws Exception {
        //LogUtil.log("run");
        _pubKey = _pubQueue.poll();
        try {
            if(StringUtil.isNotNullOrEmpty(_pubKey)) {
                if (2 == 2) LogUtil.log("PublisherSpout-> START msgId = " + _pubKey);
                _keys = _redisClient.hgetAll(_pubKey);
                _keys.put("pubKey", _pubKey);
                _publisher = new Publisher(_keys);
                byte[] binaryPublisher = SerializationUtil.serialize(_publisher);
                emit(_pubKey, binaryPublisher, _pubKey);
            }
        } catch (Exception e) {
            String errMsg = "Spout(" + _pubKey + "), " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
    }

    private static void emit(String pubKey, byte[] binaryPublisher, String pubId) throws Exception {
        //LogUtil.log("emit");
        _values = new Values(pubKey, binaryPublisher);
        if (_mode.equalsIgnoreCase("linear")) {
            Map<String, Object> outConf = LinearControl.buildConf(_fields, _values);
            outConf.put("mainConf", _mainConf);
            outConf.put("msgId", pubId);
            FeedLoadBolt.linear(outConf);
        } else {
            _spoutOutputCollector.emit(_values, pubId);
        }
    }

    public void linear(Map conf) {
        //LogUtil.log("linear");
        _mode = "linear";
        configure(conf);
        _pubQueue.clear();
        _pubQueue.add("publishers:AP:AP-Online-National-News");
        nextTuple();
    }

    @Override
    public void nextTuple() {
        //LogUtil.log("tuple");
        try {
            run();
            //	check();
        } catch (Exception e) {
            fail(_pubKey);
        }
    }

    private static void check() throws Exception {
        _publisherKeySearch = "delivered:WordPress:Wire:AP:content:external:urn:publicid:ap.org:*";
        String msgId = "not-set" + ":" + _hostname + ":" + System.nanoTime();
        try {
            String pubKey;
            ArrayList<String> delivered = _redisClient.keys(_publisherKeySearch);
            Iterator<String> DeliveryIterator = delivered.iterator();

            int i = 0;
            Map<String, String> delMap;
            Map<String, String> fullMap = new HashMap<>();

            while (DeliveryIterator.hasNext()) {
                i += 1;
                pubKey = DeliveryIterator.next();
                msgId = pubKey + ":" + _hostname + ":" + System.nanoTime();
                if (2 == 9) LogUtil.log("PublisherSpout->Spout START for msgId" + msgId);
                delMap = _redisClient.hgetAll(pubKey);
                LogUtil.log(i + " - id: " + delMap.get("id") + " pubkey: " + pubKey + " title: " + delMap.get("title"));
                fullMap.put(Integer.toString(i), delMap.get("id") + ", pubkey: " + pubKey + " title:" + delMap.get("title"));
            }
            Map<String, String> sortMap = sortHashMapByValuesD((HashMap) fullMap);
            printMap(sortMap);
        } catch (Exception e) {
            String msg = "Spout(" + msgId + ");";
            RedisLogUtil.logError(msg, e, _redisClient);
        }
    }

    public static void printMap(Map<String, String> map) {
        Set s = map.entrySet();
        Iterator it = s.iterator();
        String prev = "";
        String flag;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            //LogUtil.log(key + " => " + value);
            if (value.substring(0, 5).equalsIgnoreCase(prev)) {
                flag = "****\r\n";
            } else {
                flag = "";
            }
            LogUtil.log(flag + value);
            prev = value.substring(0, 5);

        }//while
        LogUtil.log("========================");
    }//printMap

    public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        for (Object val : mapValues) {
            for (Object key : mapKeys) {
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put(key, val);
                    break;
                }

            }

        }
        return sortedMap;
    }

    @Override
    public void ack(Object pubKey) {
        if (1 == 1) LogUtil.log("PublisherSpout-> ACK msgId = " + pubKey.toString());
        _pubQueue.add((String) pubKey);
    }

    @Override
    public void fail(Object pubKey) {
        RedisLogUtil.logFail("PublisherSpout-> FAIL msgId = " + pubKey.toString(), _redisClient);
        _pubQueue.add((String) pubKey);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //LogUtil.log("declare");
        // create the tuple with field names for Site
        declarer.declare(_fields);
    }
}