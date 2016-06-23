package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Model.Normalize.Story;
import com.DFM.StormFront.Model.Normalize.StorySubjects;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.SerializationUtil;
import com.DFM.StormFront.Util.StringUtil;
import com.DFM.StormFront.Util.XmlUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 4/21/2016.
 */
public class StoryReaderExec {
    private String xsltRootPath;
    private Publisher publisher;
    private String sStory;
    private RedisClient redisClient;

    public StoryReaderExec(){}

    public Map<String, String> Exec() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        //Transform story to get attributes

        String xsltPath = this.xsltRootPath + this.publisher.getFeedType() + "_Story.xslt";
        String storyXML = XmlUtil.transform(this.sStory, xsltPath);
        Story story = Story.fromXML(storyXML);
        StorySubjects storySubjects = story.storySubjects;
        String subjectsXML = storySubjects.toXml();

        String[] keyArgs = {"content", this.publisher.getSourceType(), this.publisher.getSource(), this.publisher.getFeedType(), story.getGuid()};
        String contentKey = RedisContentUtil.setKey(keyArgs);

        //Check uniqueness
        String suStory = StringUtil.removeUnicode(this.sStory);
        byte[] b5 = SerializationUtil.serialize(this.sStory);
        String s5 = new String(b5, StandardCharsets.UTF_8);
        String m5 = RedisContentUtil.createMD5(s5);
        Map<String, Object> statusMap;
        statusMap = RedisContentUtil.createStoryStatusMap(contentKey, m5, this.redisClient);

        resultMap.put("storyGUID", story.getGuid());
        resultMap.put("contentKey", contentKey);
        resultMap.put("story", suStory);
        resultMap.put("subjectsXML", subjectsXML);


        if(StringUtil.isNullOrEmpty(story.getGuid())) {
            resultMap.put("new/changed", "false");
        }
        else if ((Boolean) statusMap.get("isNew") | (Boolean) statusMap.get("isUpdated")) {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("md5", m5);
            hashMap.put("title", story.getTitle());
            hashMap.put("viewuri", story.getViewURI());
            if(StringUtil.isNotNullOrEmpty(this.publisher.getCat()) && StringUtil.isNotNullOrEmpty(this.publisher.getUrl()))
            {
                hashMap.put(StringUtil.hyphenateString(this.publisher.getCat()), this.publisher.getUrl());
            }
            RedisContentUtil.trackContent(contentKey, hashMap, this.redisClient);
            if (2 == 12) {
                LogUtil.log("StoryReader->Normalize new/changed: " + story.getTitle() + "; oldSourceMD5: " + statusMap.get("oldMD5") + "; newSourceMD5: " + statusMap.get("newMD5"));
            }
            resultMap.put("new/changed", "true");
        }
        else {
            resultMap.put("new/changed", "false");
        }

        return resultMap;
    }

    public String getXsltRootPath() {
        return xsltRootPath;
    }

    public void setXsltRootPath(String xsltRootPath) {
        this.xsltRootPath = xsltRootPath;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public String getsStory() {
        return sStory;
    }

    public void setsStory(String sStory) {
        this.sStory = sStory;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
}
