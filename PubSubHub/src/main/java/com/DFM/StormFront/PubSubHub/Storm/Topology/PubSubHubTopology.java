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

import java.util.HashMap;
import java.util.Map;

public class PubSubHubTopology {
    private static StormTopology buildTopology(String[] args) throws AlreadyAliveException, InvalidTopologyException {
        //Instantiate builder
        TopologyBuilder builder = new TopologyBuilder();

        // Spout to get feed names from Redis
        builder.setSpout("PublisherSpout", new PublisherSpout("publishers:*"), 1).setNumTasks(1);

        // Bolt to determine feed type and send to appropriate bolt based on feed class
        builder.setBolt("FeedLoadBolt", new FeedLoadBolt(), 2).setNumTasks(4).shuffleGrouping("PublisherSpout");

        // Bolt to process Feed class
        builder.setBolt("FeedReaderBolt", new FeedReaderBolt(), 3).setNumTasks(6).shuffleGrouping("FeedLoadBolt");

        // Bolts to extract Feed Item data
        builder.setBolt("SinglePhaseLoadBolt", new SinglePhaseLoadBolt(), 3).setNumTasks(6).shuffleGrouping("FeedReaderBolt", "SinglePhase");
        builder.setBolt("MultiPhaseLoadBolt", new MultiPhaseLoadBolt(), 3).setNumTasks(6).shuffleGrouping("FeedReaderBolt", "MultiPhase");

        // Bolt to read story
        builder.setBolt("StoryReaderBolt", new StoryReaderBolt(), 1).setNumTasks(1).fieldsGrouping("SinglePhaseLoadBolt", new Fields("storyGUID")).fieldsGrouping("MultiPhaseLoadBolt", new Fields("storyGUID"));

        // Bolt to queue up subscribers
        builder.setBolt("SubscriberBolt", new SubscriberBolt(), 2).setNumTasks(4).fieldsGrouping("StoryReaderBolt", new Fields("storyGUID"));

        // Bolts for subscribers
        builder.setBolt("WordPressBolt", new WordPressBolt(), 4).setNumTasks(8).fieldsGrouping("SubscriberBolt", "WordPress", new Fields("storyGUID"));
        builder.setBolt("NGPSBolt", new NGPSBolt(), 4).setNumTasks(8).fieldsGrouping("SubscriberBolt", "NGPS", new Fields("storyGUID"));
        builder.setBolt("ElasticSearchBolt", new ElasticSearchBolt(), 4).setNumTasks(8).fieldsGrouping("SubscriberBolt", "ElasticSearch", new Fields("storyGUID"));

        //Put it all together
        return builder.createTopology();
    }

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
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

        if (options.get("mode").equalsIgnoreCase("storm")) {
            storm(args, options.get("loc"));
        }

        if (options.get("mode").equalsIgnoreCase("linear")) {
            linear(args, options.get("loc"));
        }
    }

    private static void storm(String[] args, String dbLoc) throws AlreadyAliveException, InvalidTopologyException {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setNumWorkers(4);
        conf.setMaxSpoutPending(1000);
        conf.setMessageTimeoutSecs(300);
        //conf.setMaxTaskParallelism(4);
        RedisClient redisClient = new RedisClient(dbLoc);
        conf.put("redisHost", redisClient.getHost());
        conf.put("redisPort", Integer.toString(redisClient.getPort()));
        conf.put("redisTimeout", Integer.toString(redisClient.getTimeout()));
        conf.put("redisPassword", redisClient.getPassword());
        conf.put("redisDatabase", Integer.toString(redisClient.getDatabase()));
        conf.put("xsltRootPath", FileUtil.getXsltPath());
        conf.put("publisherKeySearch", "publishers:*");

        // PubSubTopology is the name of submitted topology
        //Submit to prod cluster uncomment next command, comment previous 2 commands
        try {
            StormSubmitter.submitTopology("PubSubHubTopology", conf, buildTopology(args));
        } catch (Exception ignored) {
        }
    }

    private static void linear(String[] args, String dbLoc) {
        Map<String, String> conf = new HashMap<>();
        RedisClient redisClient = new RedisClient(dbLoc);
        conf.put("redisHost", redisClient.getHost());
        conf.put("redisPort", Integer.toString(redisClient.getPort()));
        conf.put("redisTimeout", Integer.toString(redisClient.getTimeout()));
        conf.put("redisPassword", redisClient.getPassword());
        conf.put("redisDatabase", Integer.toString(redisClient.getDatabase()));
        conf.put("xsltRootPath", FileUtil.getXsltPath());
        conf.put("publisherKeySearch", "publishers:*");

        PublisherSpout publisherSpout = new PublisherSpout("publishers:*");
        publisherSpout.linear(conf);
    }

}