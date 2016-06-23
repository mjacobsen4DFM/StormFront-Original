package com.DFM.StormFront.Client.Util;

import com.DFM.StormFront.Client.RedisClient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RedisContentUtil {
    public static boolean isNew(String redisKey, RedisClient redisClient) {
        return getMD5(redisKey, redisClient) == null;
    }

    public static boolean isNew(String redisKey, String hashKey, RedisClient redisClient) {
        return redisClient.hget(redisKey, hashKey) == null;
    }

    public static boolean isUpdated(String redisKey, String content, RedisClient redisClient) {
        String md5Test = getMD5(redisKey, redisClient);
        String md5 = (md5Test != null) ? md5Test : "";
        return !md5.contentEquals(createMD5(content));
    }

    public static boolean isNewOrUpdated(String redisKey, String content, RedisClient redisClient) {
        return isNew(redisKey, redisClient) | isUpdated(redisKey, content, redisClient);
    }

    public static boolean keyExists(String redisKey, String hashKey, RedisClient redisClient) {
        try {
            return redisClient.hget(redisKey, hashKey) != null;
        } catch (Exception e) {
            String msg = "Seeking redisKey: " + redisKey + ", hashKey: " + hashKey;
            RedisLogUtil.logError(msg, e, redisClient);
            return false;
        }
    }

    public static Map<String, Object> createStoryStatusMap(String redisKey, String md5, RedisClient redisClient) throws Exception {
        return createStoryStatusMap(redisKey, "md5", md5, "content", redisClient);
    }

    public static Map<String, Object> createStoryStatusMap(String redisKey, String key, String newValue, RedisClient redisClient) throws Exception {
        return createStoryStatusMap(redisKey, key, newValue, "content", redisClient);
    }

    public static Map<String, Object> createStoryStatusMap(String redisKey, String key, String newValue, String compareType, RedisClient redisClient) throws Exception {
        Map<String, Object> statusMap = new HashMap<>();
        Map<String, String> keys;
        try {
            keys = redisClient.hgetAll(redisKey);
        } catch (Exception e) {
            String msg = "Seeking redisKey: " + redisKey;
            RedisLogUtil.logError(msg, e, redisClient);
            return null;
        }

        boolean bNew = (keys.get(key) == null);
        String preValue = (bNew) ? "" : keys.get(key);
        boolean bUpdated = false;
        if (!bNew) {
            if (compareType.equalsIgnoreCase("md5")) {
                bUpdated = !preValue.contentEquals(newValue);
            } else if (compareType.equalsIgnoreCase("content")) {
                bUpdated = !preValue.contentEquals(newValue);
            } else if (compareType.equalsIgnoreCase("int")) {
                bUpdated = Integer.parseInt(newValue) > Integer.parseInt(preValue);
            } else {
                throw new Exception(compareType + " Comparison Type Not Implemented");
            }
        }
        statusMap.put("isNew", bNew);
        statusMap.put("isUpdated", bUpdated);
        statusMap.put("old" + key.toUpperCase(), preValue);
        statusMap.put("new" + key.toUpperCase(), newValue);
        statusMap.put("keyCheck", key);
        statusMap.put("valueCheck", newValue);
        if (!bNew) {
            statusMap.put("id", keys.get("id"));
            statusMap.put("title", keys.get("title"));
            statusMap.put("location", keys.get("location"));
        }
        return statusMap;
    }

    public static void setMD5(String redisKey, String content, RedisClient redisClient) {
        redisClient.hset(redisKey, "md5", createMD5(content));
    }

    public static String getMD5(String redisKey, RedisClient redisClient) {
        try {
            Map<String, String> keys = redisClient.hgetAll(redisKey);
            if(keys.containsKey("md5")) {
                return keys.get("md5");
            }else{
                return null;
            }
        } catch (Exception e) {
            String msg = "Seeking redisKey: " + redisKey;
            RedisLogUtil.logError(msg, e, redisClient);
            return null;
        }
    }

    public static void updateMD5(String redisKey, String content, RedisClient redisClient) {
        setMD5(redisKey, content, redisClient);
    }

    public static void resetMD5(String redisKey, RedisClient redisClient) {
        String msg = "Reset" + " redisKey: " + redisKey;
        redisClient.hset(redisKey, "md5", "");
    }

    public static String cleanKey(String key) {
        key = key.replace("http://", "");
        key = key.substring(1);
        return key;
    }

    public static String setKey(String[] args) {
        String key = "";
        for (String arg : args) {
            key = key + ":" + arg;
        }
        return cleanKey(key);
    }

    public static void trackContent(String redisKey, String hashKey, String hashValue, RedisClient redisClient) {
        //setMD5(redisKey, content, redisClient);
        redisClient.hset(redisKey, hashKey, hashValue);
    }

    public static void trackContent(String redisKey, Map<String, String> hashMap, RedisClient redisClient) {
        //setMD5(redisKey, content, redisClient);
        redisClient.hmset(redisKey, hashMap);
    }

    public static String createMD5(String str) {
        byte[] bytesOfMessage = null;
        try {
            bytesOfMessage = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] thedigest = md.digest(bytesOfMessage);
        StringBuilder sb = new StringBuilder();
        for (byte aThedigest : thedigest) {
            sb.append(Integer.toHexString((aThedigest & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
