package com.DFM.StormFront.PubSubHub.Util;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 1/5/2016.
 */
public class LinearControl {
    public static Map<String, Object> buildConf(Fields fields, Values values) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            map.put(fields.get(i), values.get(i));
        }
        return map;
    }
}
