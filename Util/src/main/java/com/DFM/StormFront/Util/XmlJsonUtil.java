package com.DFM.StormFront.Util;


import org.json.JSONObject;
import org.json.XML;

import java.util.Map;

/**
 * Created by Mick on 6/3/2016.
 */
public class XmlJsonUtil {
    public static String XmlToJson(String xml) {
        JSONObject jsonObj = XML.toJSONObject(xml);
        return jsonObj.toString();
    }

    public static String JsonToXml(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return XML.toString(jsonObject);
    }


    public static JSONObject MapToJson(Map map) {
        return new JSONObject(map);
    }


    public static String toJSON(Map<String, Object> map) throws Exception {
        JSONObject jo = MapToJson(map);
        return jo.toString();
    }

}