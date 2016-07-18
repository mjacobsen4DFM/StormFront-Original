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
    private static Queue<String> _pubList = new LinkedList<>();
    private static Queue<String> _pubQueue = new LinkedList<>();
    private static Integer tupleCount = 0;


    private static LogUtil _logUtil = new LogUtil();

    private static Fields _fields = new Fields("pubKey", "publisher");
    private static Values _values;
    private static Map<String, Object> _mainConf;

    public PublisherSpout(String publisherKeySearch){
        _logUtil.log("newspout", 4);
        _publisherKeySearch = publisherKeySearch;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector spoutOutputCollector) {
        _logUtil.log("open", 4);
        // Open the spout
        _mode = "storm";
        configure(conf);
        _logUtil.level = Integer.parseInt(_redisClient.hget("config:storm", "loglevel"));
        //Collect the rain
        _spoutOutputCollector = spoutOutputCollector;
    }

    private void configure(Map conf) {
        _mainConf = conf;
        _redisClient = new RedisClient(conf);
        _logUtil.level = Integer.parseInt(_redisClient.hget("config:" + _mode, "loglevel"));
        _hostname = SystemUtil.getHostname();
        _publisherKeySearch = (String) conf.get("publisherKeySearch");
        _logUtil.log("publisherKeySearch: " +  _publisherKeySearch);

        //Get the pubs from Redis
        _pubs = _redisClient.keys(_publisherKeySearch);

        //Populate the pubs list
        _pubList.addAll(_pubs);
        _pubQueue.addAll(_pubs);

        _logUtil.log(String.format("_pubs: %s, _pubList: %s ,_pubQueue: %s", _pubs.size(), _pubList.size(), _pubQueue.size()), "/home/storm/apache-storm/logs/spout.log");

    }

    private static void run() throws Exception {
        _logUtil.log("run", 4);
        _pubKey = _pubQueue.poll();
        try {
            if(StringUtil.isNotNullOrEmpty(_pubKey)) {
                _logUtil.log("PublisherSpout-> START msgId = " + _pubKey, 2);
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
        _logUtil.log("emit", 4);
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
        _logUtil.log("linear", 4);
        _mode = "linear";
        configure(conf);
        _logUtil.level = Integer.parseInt(_redisClient.hget("config:storm", "loglevel"));
        nextTuple();
    }

    public void reloadPubs(){
        Queue<String> pubList = new LinkedList<>();
        //Get the pubs from Redis
        ArrayList<String> pubs = _redisClient.keys(_publisherKeySearch);
        pubList.addAll(pubs);
        String pubkey;

        while ((pubkey = pubList.poll()) != null) {
            if(! _pubList.contains(pubkey)){
                //Add any new pubs to the list & queue
                _logUtil.log("Publist changed! Adding: " + pubkey, 1);
                _pubList.add(pubkey);
                _pubQueue.add(pubkey);
            }
        }
    }

    @Override
    public void nextTuple() {
        _logUtil.log("tuple", 4);
        try {
            run();
            //	check();
            tupleCount += 1;
            if(1000 == tupleCount){
                _logUtil.log("Tuples: " + tupleCount, 3);
                reloadPubs();
                tupleCount = 0;
            }
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
                _logUtil.log("PublisherSpout->Spout START for msgId" + msgId, 1);
                delMap = _redisClient.hgetAll(pubKey);
                _logUtil.log(i + " - id: " + delMap.get("id") + " pubkey: " + pubKey + " title: " + delMap.get("title"), 1);
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
            _logUtil.log(key + " => " + value, 4);
            if (value.substring(0, 5).equalsIgnoreCase(prev)) {
                flag = "****\r\n";
            } else {
                flag = "";
            }
            _logUtil.log(flag + value, 1);
            prev = value.substring(0, 5);

        }//while
        _logUtil.log("========================", 1);
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
        _logUtil.log("PublisherSpout-> ACK msgId = " + pubKey.toString(), 1);
        _pubQueue.add((String) pubKey);
    }

    @Override
    public void fail(Object pubKey) {
        RedisLogUtil.logFail("PublisherSpout-> FAIL msgId = " + pubKey.toString(), _redisClient);
        _pubQueue.add((String) pubKey);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        _logUtil.log("declare", 4);
        // create the tuple with field names for Site
        declarer.declare(_fields);
    }
}