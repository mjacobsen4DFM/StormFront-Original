package com.DFM.StormFront.PubSubHub.Storm.Spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Model.Storm.MsgTracker;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Workflow.FeedLoadBolt;
import com.DFM.StormFront.PubSubHub.Util.LinearControl;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.*;

import java.util.*;


public class PublisherSpout extends BaseRichSpout {
    private static final long serialVersionUID = 1L;
    private static String _mode = "unknown";
    private static String _operation = "init";

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
    private static Queue<String> _pubPending = new LinkedList<>();
    private static Queue<String> _pubFeeds = new LinkedList<>();
    private static Integer _msDelay = 60000;
    private static Integer tupleCount = 0;


    private static LogUtil _logUtil;

    private static Fields _fields = new Fields("pubKey", "publisher");
    private static Values _values;
    private static Map<String, Object> _mainConf;

    public PublisherSpout(String publisherKeySearch) {
        _logUtil = new LogUtil();
        _operation = "NEW_SPOUT";
        _logUtil.log(_operation, 4);
        _publisherKeySearch = publisherKeySearch;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector spoutOutputCollector) {
        _logUtil = new LogUtil();
        _operation = "OPEN";
        _logUtil.log(_operation, 4);
        // Open the spout
        _mode = "storm";
        configure(conf);
        _logUtil.level = 3; //Integer.parseInt(_redisClient.hget("config:storm", "loglevel"));
        //Collect the rain
        _spoutOutputCollector = spoutOutputCollector;
    }

    private void configure(Map conf) {
        _mainConf = conf;
        _redisClient = new RedisClient(conf);
        _logUtil.level = 3; //Integer.parseInt(_redisClient.hget("config:" + _mode, "loglevel"));
        _hostname = SystemUtil.getHostname();
        _publisherKeySearch = (String) conf.get("publisherKeySearch");
        _logUtil.log(String.format("publisherKeySearch: %s", _publisherKeySearch));

        //Get the pubs from Redis
        _pubs = _redisClient.keys(_publisherKeySearch);

        //Populate the pubs list
        _pubList.addAll(_pubs);
        _pubQueue.addAll(_pubs);

        _logUtil.log(String.format("_pubs: %s, _pubList: %s ,_pubQueue: %s", _pubs.size(), _pubList.size(), _pubQueue.size()), "spout.log");

    }

    private static void run() throws Exception {
        _operation = "RUN";
        _logUtil.log(_operation, 4);
        String pubQueued = _pubQueue.poll();

        try {
            if (StringUtil.isNotNullOrEmpty(pubQueued)) {
                MsgTracker msgTracker = new MsgTracker(pubQueued);
                if(msgTracker.Ready()) {
                    _pubKey = msgTracker.key;
                    _keys = _redisClient.hgetAll(_pubKey);
                    _keys.put("pubKey", _pubKey);
                    _publisher = new Publisher(_keys);
                    if (_publisher.getActive() && _pubList.contains(_pubKey) && StringUtil.isNotNullOrEmpty(_publisher.getUrl())) {
                        if(!_pubFeeds.contains(_pubKey)) {
                            _pubFeeds.add(_pubKey);
                        }
                        _pubPending.add(msgTracker.toString());
                        _logUtil.log(String.format("Track %s->PubFeeds: %s, PubQueue: %s, PubPending: %s", _operation, _pubFeeds.size(), _pubQueue.size(), _pubPending.size()), 3);
                        byte[] binaryPublisher = SerializationUtil.serialize(_publisher);
                        _logUtil.log(String.format("PublisherSpout-> START pubKey = %s", _pubKey), 2);
                        emit(_pubKey, binaryPublisher, msgTracker.toString());
                    } else {
                        _logUtil.log(String.format("PublisherSpout-> SKIP pubKey = %s", _pubKey), 2);
                    }
                } else {
                    _pubQueue.add(msgTracker.toString());
                }
            }
        } catch (Exception e) {
            String errMsg = "Spout(" + _pubKey + "), " + ExceptionUtil.getFullStackTrace(e);
            throw new Exception(errMsg);
        }
    }

    private static void emit(String pubKey, byte[] binaryPublisher, String pubId) throws Exception {
        _operation = "EMIT";
        _logUtil.log(_operation, 4);
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
        _logUtil = new LogUtil();
        _operation = "LINEAR";
        _logUtil.log(_operation, 4);
        _mode = "linear";
        configure(conf);
        _logUtil.level = Integer.parseInt(_redisClient.hget("config:storm", "loglevel"));
        nextTuple();
    }

    public void loadNewPubs() {
        //Get the pubs from Redis
        Queue<String> tstPubList = new LinkedList<>();
        ArrayList<String> pubs = _redisClient.keys(_publisherKeySearch);
        tstPubList.addAll(pubs);
        String pubkey;

        Queue<String> newPubList = new LinkedList<>(tstPubList);
        Queue<String> oldPubList = new LinkedList<>(_pubList);

        //Add pubs
        while ((pubkey = newPubList.poll()) != null) {
            if (!oldPubList.contains(pubkey)) {
                //Add any new pubs to the list & queue
                _logUtil.log(String.format("PubList changed! Adding: %s", pubkey), 1);
                _pubList.add(pubkey);
                _pubQueue.add(pubkey);
            }
        }

         newPubList = new LinkedList<>(tstPubList);
         oldPubList = new LinkedList<>(_pubList);

        //Remove pubs
        while ((pubkey = oldPubList.poll()) != null) {
            if (!newPubList.contains(pubkey)) {
                //Remove any pubs no longer in the _publisherKeySearch results
                _logUtil.log(String.format("PubList changed! Removing: %s", pubkey), 1);
                _pubList.remove(pubkey);
            }
        }
    }

    @Override
    public void nextTuple() {
        _operation = "TUPLE";
        _logUtil.log(_operation, 4);
        try {
            tupleCount += 1;
            if (1000 == tupleCount) {
                //_logUtil.log(String.format("PubFeeds: %s, PubQueue: %s, PubPending: %s", _pubFeeds.size(), _pubQueue.size(), _pubPending.size()), 3);
                loadNewPubs();
                tupleCount = 0;
            }
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
                _logUtil.log(String.format("PublisherSpout->Spout START for msgId: %s", msgId), 1);
                delMap = _redisClient.hgetAll(pubKey);

                _logUtil.log(String.format("%s - id: %s, pubkey: %s, title: %s", i, delMap.get("id"), pubKey, delMap.get("title")), 1);
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
            _logUtil.log(String.format("%s => %s", key, value), 4);
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
    public void ack(Object msgId) {
        doAckFail(msgId, "ACK");
    }

    @Override
    public void fail(Object msgId) {
        doAckFail(msgId, "FAIL");
    }

    private void doAckFail(Object msgId, String msg){
        MsgTracker msgTracker = new MsgTracker((String)msgId);
        _pubPending.remove(msgTracker.toString());

        String pubKey = msgTracker.key;

        _logUtil.log(String.format("PublisherSpout-> %s pubKey = %s, took %s seconds", msg, pubKey, msgTracker.getDuration()/1000.000), 1);

        if(msgTracker.TooFast(_msDelay)){
            _logUtil.log(String.format("PublisherSpout-> DELAY pubKey = %s for %s seconds", pubKey, msgTracker.msDelay/1000.000), 1);
        }

        _pubQueue.add(msgTracker.toString());
        _logUtil.log(String.format("Track %s->PubFeeds: %s, PubQueue: %s, PubPending: %s", msg, _pubFeeds.size(), _pubQueue.size(), _pubPending.size()), 3);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        _logUtil.log("declare", 4);
        // create the tuple with field names for Site
        declarer.declare(_fields);
    }
}