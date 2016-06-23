package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 4/21/2016.
 */
public class FeedReaderExec {
    public Publisher publisher;
    public RedisClient redisClient;
    private String _stories;
    private String _feedKey;

    public FeedReaderExec() {
    }

    public FeedReaderExec(
            Publisher publisher,
            RedisClient redisClient,
            String stories,
            String feedKey) {
        this.publisher = publisher;
        this.redisClient = redisClient;
        _stories = stories;
        _feedKey = feedKey;
    }


    public ArrayList<String> Exec() throws Exception {

        String md5 = RedisContentUtil.createMD5(_stories);
        Map<String, Object> statusMap;
        statusMap = RedisContentUtil.createStoryStatusMap(_feedKey, md5, redisClient);
        boolean bNew = (Boolean) statusMap.get("isNew");
        boolean bUpdated = (Boolean) statusMap.get("isUpdated");

        ArrayList<String> storyList = new ArrayList<>();

        if (bNew | bUpdated) {
            Document doc = XmlUtil.deserialize(_stories);
            NodeList stories = doc.getElementsByTagName(publisher.getItemElement());
            for (int i = 0; i < stories.getLength(); i++) {
                Element story = (Element) stories.item(i);
                String sStory = XmlUtil.toString(story);
                storyList.add(sStory);
            }
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("md5", md5);
            RedisContentUtil.trackContent(_feedKey, hashMap, redisClient);
        }
        return storyList;
    }

    public void set_stories(String _stories) {
        this._stories = _stories;
    }

    public void set_feedKey(String _feedKey) {
        this._feedKey = _feedKey;
    }
}
