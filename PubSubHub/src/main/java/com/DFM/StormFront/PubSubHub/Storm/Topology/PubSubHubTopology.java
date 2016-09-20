package com.DFM.StormFront.PubSubHub.Storm.Topology;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.ElasticSearchBolt;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.NGPSBolt;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Subscriber.WordPressBolt;
import com.DFM.StormFront.PubSubHub.Storm.Bolt.Workflow.*;
import com.DFM.StormFront.PubSubHub.Storm.Spout.PublisherSpout;
import com.DFM.StormFront.Util.FileUtil;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class PubSubHubTopology {
    private static StormTopology buildTopology(Map<String, String> options) throws AlreadyAliveException, InvalidTopologyException {
        //Instantiate builder
        TopologyBuilder builder = new TopologyBuilder();

        // Spout to get feed names from Redis
        builder.setSpout("PublisherSpout", new PublisherSpout(options.get("publisherKeySearch")), 1).setNumTasks(1);

        // Bolt to determine feed type and send to appropriate bolt based on feed class
        builder.setBolt("FeedLoadBolt", new FeedLoadBolt(), 5).setNumTasks(10).shuffleGrouping("PublisherSpout");

        // Bolt to process Feed class
        builder.setBolt("FeedReaderBolt", new FeedReaderBolt(), 3).setNumTasks(6).shuffleGrouping("FeedLoadBolt");

        // Bolts to extract Feed Item data
        builder.setBolt("SinglePhaseLoadBolt", new SinglePhaseLoadBolt(), 3).setNumTasks(6).shuffleGrouping("FeedReaderBolt", "SinglePhase");
        builder.setBolt("MultiPhaseLoadBolt", new MultiPhaseLoadBolt(), 3).setNumTasks(6).shuffleGrouping("FeedReaderBolt", "MultiPhase");

        // Bolt to read story
        builder.setBolt("StoryReaderBolt", new StoryReaderBolt(), 2).setNumTasks(4).fieldsGrouping("SinglePhaseLoadBolt", new Fields("storyGUID")).fieldsGrouping("MultiPhaseLoadBolt", new Fields("storyGUID"));

        // Bolt to queue up subscribers
        builder.setBolt("SubscriberBolt", new SubscriberBolt(), 3).setNumTasks(6).fieldsGrouping("StoryReaderBolt", new Fields("storyGUID"));

        // Bolts for subscribers
        builder.setBolt("WordPressBolt", new WordPressBolt(), 5).setNumTasks(10).fieldsGrouping("SubscriberBolt", "WordPress", new Fields("storyGUID"));
        builder.setBolt("NGPSBolt", new NGPSBolt(), 4).setNumTasks(8).fieldsGrouping("SubscriberBolt", "NGPS", new Fields("storyGUID"));
        builder.setBolt("ElasticSearchBolt", new ElasticSearchBolt(), 4).setNumTasks(8).fieldsGrouping("SubscriberBolt", "ElasticSearch", new Fields("storyGUID"));

        //Put it all together
        return builder.createTopology();
    }

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

        Map<String, String> options = getOptions(args);
        if (options.get("mode").equalsIgnoreCase("storm")) {
            storm(options);
        }

        if (options.get("mode").equalsIgnoreCase("linear")) {
            linear(options);
        }
    }

    private static void storm(Map<String, String> options ) throws AlreadyAliveException, InvalidTopologyException {
        LogUtil logUtil = new LogUtil();
        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(12);
        conf.setMaxSpoutPending(1000);
        conf.setMaxTaskParallelism(8);
        RedisClient redisClient = new RedisClient(options.get("loc"));
        conf.put("loc", options.get("loc"));
        conf.put("redisHost", redisClient.getHost());
        conf.put("redisPort", Integer.toString(redisClient.getPort()));
        conf.put("redisTimeout", Integer.toString(redisClient.getTimeout()));
        conf.put("redisPassword", redisClient.getPassword());
        conf.put("redisDatabase", Integer.toString(redisClient.getDatabase()));
        conf.put("xsltRootPath", FileUtil.getXsltDir());

        if(StringUtil.isNotNullOrEmpty(options.get("pubsearch"))) {
            logUtil.log(String.format("Setting Search: %s", options.get("pubsearch")), "topology.log");
            conf.put("publisherKeySearch", options.get("pubsearch"));
        } else {
            logUtil.log("Setting Search: publishers:*", "topology.log");
            conf.put("publisherKeySearch", "publishers:*");
        }

        options.put("publisherKeySearch", conf.get("publisherKeySearch").toString());

        // PubSubTopology is the name of submitted topology
        //Submit to prod cluster uncomment next command, comment previous 2 commands
        try {
            StormSubmitter.submitTopology("PubSubHubTopology", conf, buildTopology(options));
        } catch (Exception ignored) {
        }
    }

    private static void linear(Map<String, String> options) {
        Map<String, String> conf = new HashMap<>();
        LogUtil logUtil = new LogUtil();
        RedisClient redisClient = new RedisClient(options.get("loc"));
        conf.put("loc", options.get("loc"));
        conf.put("redisHost", redisClient.getHost());
        conf.put("redisPort", Integer.toString(redisClient.getPort()));
        conf.put("redisTimeout", Integer.toString(redisClient.getTimeout()));
        conf.put("redisPassword", redisClient.getPassword());
        conf.put("redisDatabase", Integer.toString(redisClient.getDatabase()));
        conf.put("xsltRootPath", FileUtil.getXsltDir());

        if(StringUtil.isNotNullOrEmpty(options.get("pubsearch"))) {
            logUtil.log(String.format("Setting Search: %s", options.get("pubsearch")), "topology.log");
            conf.put("publisherKeySearch", options.get("pubsearch"));
        } else {
            logUtil.log("Setting Search: publishers:*", "topology.log");
            conf.put("publisherKeySearch", "publishers:AP:*");
            //conf.put("publisherKeySearch", "publishers:blogs:www.heyreverb.com");
            //conf.put("publisherKeySearch", "publishers:AP:AP-Online-National-News");
        }

        options.put("publisherKeySearch", conf.get("publisherKeySearch"));

        PublisherSpout publisherSpout = new PublisherSpout(options.get("publisherKeySearch"));
        publisherSpout.linear(conf);
    }

    private static Map<String, String> getOptions(String[] args) {
        int i = 0;
        String key = "";
        String val = "";
        Map<String, String> options = new HashMap<>();
        while (i < args.length) {
            if (args[i].startsWith("-")) {
                key = args[i].replace("-", "");
            } else {
                val = args[i];
            }
            options.put(key, val);
            i++;
        }
        return options;
    }

}