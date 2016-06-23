package com.DFM.StormFront.Client;

/**
 * Created by Mick on 2/3/2016.
 */

import com.DFM.StormFront.Util.SerializationUtil;
import org.apache.commons.collections.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

//import static java.lang.Thread.sleep;

public class RedisClient implements Serializable {
    private static final long serialVersionUID = -9102851779759656124L;
    private String host;
    private Integer port;
    private Integer timeout;
    private String password;
    private int database = 0;

    public RedisClient() {
    }

    public RedisClient(String host, Integer port, Integer timeout, String password) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        if (!password.equalsIgnoreCase("")) {
            this.password = password;
        }
    }


    public RedisClient(String host, Integer port, Integer timeout, String password, int database) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        if (!password.equalsIgnoreCase("")) {
            this.password = password;
        }
        this.database = database;
    }

    public RedisClient(String propertyInstance) {
        ResourceBundle resource = ResourceBundle.getBundle("redis");
        this.host = resource.getString("redisHost_" + propertyInstance);
        this.port = Integer.parseInt(resource.getString("redisPort_" + propertyInstance));
        this.timeout = Integer.parseInt(resource.getString("redisTimeout_" + propertyInstance));
        if (!resource.getString("redisPassword_" + propertyInstance).equalsIgnoreCase("")) {
            this.password = resource.getString("redisPassword_" + propertyInstance);
        }
        if (!resource.getString("redisDatabase_" + propertyInstance).equalsIgnoreCase("")) {
            this.database = Integer.parseInt(resource.getString("redisDatabase_" + propertyInstance));
        }
    }


    public RedisClient(String host, Integer port, Integer timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public RedisClient(Map conf) {
        this.host = (String) conf.get("redisHost");
        this.port = Integer.parseInt((String) conf.get("redisPort"));
        this.timeout = Integer.parseInt((String) conf.get("redisTimeout"));
        if (!conf.get("redisPassword").toString().equalsIgnoreCase("")) {
            this.password = (String) conf.get("redisPassword");
        }
        if (!conf.get("redisDatabase").toString().equalsIgnoreCase("")) {
            this.database = Integer.parseInt((String) conf.get("redisDatabase"));
        }
    }

    public RedisClient(byte[] serializedRedisClient) {
        RedisClient redisClient = new RedisClient();
        try {
            redisClient = (RedisClient) SerializationUtil.deserialize(serializedRedisClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.host = redisClient.getHost();
        this.port = redisClient.getPort();
        this.timeout = redisClient.getTimeout();
        this.password = redisClient.getPassword();
    }

    public static ArrayList<String> keys(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        return keys(redisKey, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static ArrayList<String> keys(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            Set<byte[]> rKeys = jedis.keys(SafeEncoder.encode(redisKey));
            if (CollectionUtils.isEmpty(rKeys)) {
                return new ArrayList<>();
            } else {
                return rKeys.stream().map(SafeEncoder::encode).collect(Collectors.toCollection(ArrayList::new));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static Map<String, String> hgetAll(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        return hgetAll(redisKey, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static Map<String, String> hgetAll(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        Map<String, String> keys;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            keys = jedis.hgetAll(redisKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
        return keys;
    }

    public static String hget(String redisKey, String hashKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        return hget(redisKey, hashKey, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static String hget(String redisKey, String hashKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        String value;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            value = jedis.hget(redisKey, hashKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
        return value;
    }



    public static void hmset(String redisKey, Map<String, String> hash, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        hmset(redisKey, hash, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static void hmset(String redisKey, Map<String, String> hash, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            jedis.hmset(redisKey, hash);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static void hset(String redisKey, String hashKey, String hashValue, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        hset(redisKey, hashKey, hashValue, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static void hset(String redisKey, String hashKey, String hashValue, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            jedis.hset(redisKey, hashKey, hashValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static void hdel(String redisKey, String hashKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        hdel(redisKey, hashKey, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static void hdel(String redisKey, String hashKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            jedis.hdel(redisKey, hashKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static void del(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        del(redisKey, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static void del(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            jedis.del(redisKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static void set(String key, String value, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        set(key, value, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static void set(String key, String value, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
    }

    public static String get(String key, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass) {
        return get(key, redisHost, redisPort, redisTimeout, redisPass, 0);
    }

    public static String get(String key, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass, int database) {
        String value;
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout, redisPass, database);
        Jedis jedis = getJedis(pool);
        try {
            value = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (pool != null) {
                pool.close();
                pool.destroy();
            }
        }
        return value;
    }

    private static Jedis getJedis(JedisPool pool) {
        Jedis jedis = new Jedis();
        try {
            jedis = pool.getResource();
        } catch (JedisConnectionException e) {
            try {
                Thread.sleep(randomSleep());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            jedis = getJedis(pool);
        }
        return jedis;
    }

    private static int randomSleep() {
        int min = 25;
        int max = 100;
        int rand = randInt(min, max);
        return rand * 100;
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public ArrayList<String> keys(String redisKey) {
        return keys(redisKey, this.host, this.port, this.timeout, this.password, this.database);
    }

    public Map<String, String> hgetAll(String redisKey) {
        return hgetAll(redisKey, this.host, this.port, this.timeout, this.password, this.database);
    }

    public String hget(String redisKey, String hashKey) {
        return hget(redisKey, hashKey, this.host, this.port, this.timeout, this.password, this.database);
    }

    public void hmset(String redisKey, Map<String, String> hash) {
        hmset(redisKey, hash, this.host, this.port, this.timeout, this.password, this.database);
    }

    public void hset(String redisKey, String hashKey, String hashValue) {
        hset(redisKey, hashKey, hashValue, this.host, this.port, this.timeout, this.password, this.database);
    }

    public void hdel(String redisKey, String hashKey) {
        hdel(redisKey, hashKey, this.host, this.port, this.timeout, this.password, this.database);
    }

    public void del(String redisKey) {
        del(redisKey, this.host, this.port, this.timeout, this.password, this.database);
    }

    public void set(String key, String value) {
        set(key, value, this.host, this.port, this.timeout, this.password, this.database);
    }

    public String get(String key) {
        return get(key, this.host, this.port, this.timeout, this.password, this.database);
    }
}