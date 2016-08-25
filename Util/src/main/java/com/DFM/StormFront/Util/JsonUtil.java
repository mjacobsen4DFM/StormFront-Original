package com.DFM.StormFront.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static boolean isWellFormed(String json) {
        try {
            Object obj = new JSONParser().parse(json);
            if (obj instanceof JSONObject) {
                return true;
            } else if (obj instanceof JSONArray) {
                return true;
            }
        } catch (Exception ex) {
                return false;
        }
        return false;
    }


    public static boolean isNotWellFormed(String json) {
        try {
            Object obj = new JSONParser().parse(json);
            if (obj instanceof JSONObject) {
                return false;
            } else if (obj instanceof JSONArray) {
                return false;
            }
        } catch (Exception ex) {
            return true;
        }
        return true;
    }

    public static String toJSON(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return removeUnicode(json);
    }


    public static String toJSON(Map<String, Object> map) throws Exception {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(null == entry.getValue()) {
                jsonObject.put(entry.getKey(), "");
            } else if (entry.getValue() instanceof HashMap) {
                jsonObject.put(entry.getKey(), fromJSON(toJSON((Map)entry.getValue())));
            } else if (JsonUtil.isWellFormed((String)entry.getValue())) {
                jsonObject.put(entry.getKey(), fromJSON((String)entry.getValue()));
            } else {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        return removeUnicode(jsonObject.toJSONString());
    }



    public static JSONObject fromJSON(String json) throws Exception {
        JSONObject jsonObj;
        JSONArray jsonArray;
        if(isNotWellFormed(json)) { return null; }
        try {

            Object obj = new JSONParser().parse(json);
            if (obj instanceof JSONObject) {
                jsonObj = (JSONObject) obj;
            } else {
                jsonArray = (JSONArray) obj;
                jsonObj = (JSONObject) jsonArray.get(0);
            }
            return jsonObj;
        } catch (ParseException e) {
            if (1 == 11) LogUtil.log("BAD JSON:" + json);
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
    }

    @SuppressWarnings("unchecked")
    //This isn't used...not sure if it works
    public static <T> Object fromJSON(String json, Object obj) {
        Gson gson = new Gson();
        return gson.fromJson(json, (Class<T>) obj);
    }

    //Couldn't get this to work....
    public static <T> Object fromJSON(String source, String sourceType, Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        switch (sourceType) {
            case "file":
                return mapper.readValue(new File(source), (Class<T>) obj);
            case "url":
                return mapper.readValue(new URL(source), (Class<T>) obj);
            case "string":
                return mapper.readValue(source, (Class<T>) obj);
            default:
                throw new Exception("Unknown sourceType converting from JSON: " + sourceType);
        }
    }

    public static String getValue(String json, String key) throws Exception {
        JSONObject jsonObj;
        JSONArray jsonArray;
        if(isNotWellFormed(json)) { return ""; }
        try {

            Object obj = new JSONParser().parse(json);
            if (obj instanceof JSONObject) {
                jsonObj = (JSONObject) obj;
            } else {
                jsonArray = (JSONArray) obj;
                jsonObj = (JSONObject) jsonArray.get(0);
            }

            return (jsonObj.get(key) == null) ? "" : jsonObj.get(key).toString();
        } catch (ParseException e) {
            if (1 == 11) LogUtil.log("BAD JSON:" + json);
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
    }

    public static JSONArray getArray(String json) throws Exception {
        JSONArray jsonArray = new JSONArray();
        if( isNotWellFormed(json)) { return jsonArray; }
        try {
            Object obj = new JSONParser().parse(json);
            if (obj instanceof JSONObject) {
                //It appears that the string is a JSON entity;
                //Wrapping in brackets and trying again.
                json = "[" + json +"]";
                if(isNotWellFormed(json)) { return jsonArray; }
                obj = new JSONParser().parse(json);
            }
            jsonArray = (JSONArray) obj;
        } catch (ParseException e) {
            throw new Exception(ExceptionUtil.getFullStackTrace(e));
        }
        return jsonArray;
    }

    public static List<JSONObject> getList(String json, String arrayName) throws Exception {
        if( isNotWellFormed(json)) { return null; }
        JSONObject jsonObject = (JSONObject) JSONValue.parse(json);
        return (List<JSONObject>) jsonObject.get(arrayName);
    }

    public static String removeUnicode(String string) {
        String cleanString = StringUtil.removeUnicode(string);
        cleanString = cleanString.replace("\u003c", "<");
        cleanString = cleanString.replace("\u003e", ">");
        cleanString = cleanString.replace("\\\\u003c", "<");
        cleanString = cleanString.replace("\\\\u003e", ">");
        return cleanString;
    }

    public static String cleanString(String string) {
        String cleanString = string;
        cleanString = cleanString.replace("\\\\", "\\"); //unescape escaped \ to avoid double escaping
        cleanString = cleanString.replace("\\", "\\\\"); //escape unescaped \
        cleanString = cleanString.replace("\\\"", "\""); //unescape escaped " to avoid double escaping
        cleanString = cleanString.replace("\"", "\\\""); //escape unescaped "
        return cleanString;
    }
}
