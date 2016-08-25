package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Adapter.WordPressAdapter;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Client.Util.RedisLogUtil;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Client.WordPressClient;
import com.DFM.StormFront.Model.Normalize.StorySubjects;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Model.WordPress.Image;
import com.DFM.StormFront.Model.WordPress.WordPressPost;
import com.DFM.StormFront.Util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

/**
 * Created by Mick on 4/21/2016.
 */
public class WordPressExec {
    public WordPressClient wordPressClient;
    public Publisher publisher;
    public RedisClient redisClient;
    public String operation;
    public String postLocation;
    public Map<String, Object> storyDataMap;
    private Map<String, String> _subscriberMap;
    private String _catKeyRoot;
    private String _tagKeyRoot;
    private String _deliveredStoryKey;
    private String _feedKey;
    private String _contentKey;
    private String _subjectsXML;
    private String _xsltRootPath;
    private String _story;
    private WordPressPost _wpp = new WordPressPost();

    public WordPressExec() {
        this.operation = "new";
    }

    public WordPressExec(Publisher publisher,
                         RedisClient redisClient,
                         Map<String, String> subscriberMap,
                         String deliveredStoryKey,
                         String catKeyRoot,
                         String tagKeyRoot,
                         String feedKey,
                         String contentKey,
                         String subjectsXML,
                         String xsltRootPath,
                         String story) throws Exception {
        this.publisher = publisher;
        this.redisClient = redisClient;
        _subscriberMap = subscriberMap;
        _deliveredStoryKey = deliveredStoryKey;
        _catKeyRoot = catKeyRoot;
        _tagKeyRoot = tagKeyRoot;
        _feedKey = feedKey;
        _contentKey = contentKey;
        _subjectsXML = subjectsXML;
        _xsltRootPath = xsltRootPath;
        _story = story;
        this.wordPressClient = WordPressClient.NewClient(_subscriberMap);
    }

    public Map<String, String> Exec() throws Exception {
        return Post();
    }

    public Map<String, String> Post() throws Exception {
        operation = "Post() start";
        Map<String, String> resultMap = new HashMap<>();
        String wpPostId = "";
        String storyTitle;

        if (2 == 12) {
            LogUtil.log("getFeedType: " + this.publisher.getFeedType() + " deliveredStoryKey: " + _deliveredStoryKey);
        }

        String authorId = _subscriberMap.get("userid");
        String baseURI = _subscriberMap.get("url");
        String postBaseEndpoint = baseURI + "wp/v2/posts/";
        String mediaBaseEndpoint = baseURI + "wp/v2/media/";
        postLocation = postBaseEndpoint;

        operation = "Post() Transform XML";
        String xsltPath = _xsltRootPath + this.publisher.getFeedType() + "_" + _subscriberMap.get("type") + ".xslt";
        String wppXML = XmlUtil.transform(_story, xsltPath);
        _wpp = WordPressPost.fromXML(wppXML);

/*
        Map<String, Map<String, String>> storyMetaTest = this.getStoryMeta(wppXML);
        if (1==1) { return null; }
*/

        storyTitle = _wpp.getTitle();

        operation = "Post() Get Normalized data";
        storyDataMap = this.getStoryData(_wpp, wppXML);
        ArrayList<Integer> categories = new ArrayList<>();
        ArrayList<Integer> tags = new ArrayList<>();

        operation = "Post() Checking isUpdated";
        if ((Boolean) storyDataMap.get("isUpdated")) {
            operation = "Post() Existing Post Category/Tag Retrieval";
            wpPostId = (String) storyDataMap.get("id");
            _wpp.setID(wpPostId);

            postLocation = postBaseEndpoint + wpPostId;
            resultMap = this.getPost(postLocation);

            String json = resultMap.get("body");
            try {
                String preCategories = JsonUtil.getValue(json, "categories");
                String preTags = JsonUtil.getValue(json, "tags");

                preCategories = preCategories.replace("[", "").replace("]", "");
                preTags = preTags.replace("[", "").replace("]", "");

                if (StringUtil.isNotNullOrEmpty(preCategories)) {
                    categories.addAll(StringUtil.CSVtoArrayListInt(preCategories));
                }
                if (StringUtil.isNotNullOrEmpty(preTags)) {
                    tags.addAll(StringUtil.CSVtoArrayListInt(preTags));
                }
            } catch (Exception e) {
                String msg = String.format("%s Error: from %s", operation, postLocation);
                RedisLogUtil.logError(msg, e, this.redisClient);
            }
        }

        operation = "Post() Checking isNew/isUpdated";
        if ((Boolean) storyDataMap.get("isNew") || (Boolean) storyDataMap.get("isUpdated")) {
            operation = "Post() Set Category";
            categories.addAll(this.setCats());
            _wpp.setCategories(categories);

            operation = "Post() Set Tags";
            tags.addAll(this.setTags());
            _wpp.setTags(tags);

            operation = "Post() Set Author";
            _wpp.setAuthor(authorId);

            operation = "Post() Set Status";
            _wpp.setStatus(_subscriberMap.get("status"));

            operation = "Post() Post Normalize";
            resultMap = this.postStory(_wpp, storyDataMap, postLocation);

            if ((Boolean) storyDataMap.get("isNew")) {
                operation = "Post() Gather Post Data";
                wpPostId = resultMap.get("wpPostId");
                _wpp.setID(wpPostId);
                postLocation = resultMap.get("postLocation");
            }


            if ((postLocation == null || postLocation.equals("")) && (wpPostId == null || wpPostId.equals(""))) {
                //WTF?!
                throw new Exception("Both postLocation and wpPostId are NULL.");
            } else if (postLocation == null || postLocation.equals("")) {
                postLocation = postBaseEndpoint + wpPostId;
            }

            LogUtil.log("postStory info: " + _subscriberMap.get("name") + " for: \"" + storyTitle + "\" at: " + postLocation);

            operation = "Post() Post Images";
            this.postImages(wpPostId, postLocation, authorId, _wpp, mediaBaseEndpoint);

            if ((Boolean) storyDataMap.get("isNew")) {
                operation = "Post() Post TrackingMeta";
                this.postTrackingMeta(postLocation);

                operation = "Post() Post StoryMeta";
                Map<String, Map<String, String>> storyMeta = this.getStoryMeta(wppXML);
                this.postStoryMeta(postLocation, storyMeta);
            }

            operation = "Post() All clear";
            LogUtil.log("Completed post into: " + _subscriberMap.get("name") + " for: \"" + storyTitle + "\" at: " + postLocation);
        }
        return resultMap;
    }

    public Map<String, String> Get() throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        Map<String, String> apiMap;
        String baseURI = _subscriberMap.get("url");
        String postBaseEndpoint = baseURI + "wp/v2/posts/";

        operation = "Get() Get Existing Post ID";
        Map<String, String> deliveredKeys = redisClient.hgetAll(_deliveredStoryKey);
        String wpPostId = deliveredKeys.get("id");

        operation = "Get() Set postLocation";
        postLocation = postBaseEndpoint + wpPostId;
        apiMap = this.getPost(postLocation);

        operation = "Get() Return Data";
        resultMap.put("code", apiMap.get("code"));
        resultMap.put("result", apiMap.get("body"));

        return resultMap;
    }

    public Map<String, String> getPost(String postBaseEndpoint) throws Exception {
        return WordPressAdapter.getJson(postBaseEndpoint, this.wordPressClient);
    }

    public Map<String, Object> getStoryData(WordPressPost wpp, String wppXML) throws Exception {
        String keyCheck = "key";
        String valCheck = "value";
        String typeCheck = "type";
        try {
            if (publisher.getUpdateCheckType().equalsIgnoreCase("md5")) {
                byte[] b5 = SerializationUtil.serialize(wpp.getContent());
                String s5 = new String(b5, StandardCharsets.UTF_8);
                String m5 = RedisContentUtil.createMD5(s5);
                keyCheck = this.publisher.getUpdateCheckType();
                valCheck = m5;
                typeCheck = "md5";
            } else if (publisher.getUpdateCheckType().equalsIgnoreCase("sequence")) {
                keyCheck = this.publisher.getUpdateCheckType();
                valCheck = XmlUtil.getNodeValue(wppXML, "sequence");
                typeCheck = XmlUtil.getAttributeValue(wppXML, "sequence", "type");
            }
        } catch (Exception e) {
            keyCheck = "sequence";
            valCheck = "0";
            typeCheck = "int";
        }
        //NOT catching exceptions, because if we don't know this metadata, the rest of the objects don't matter.
        return RedisContentUtil.createStoryStatusMap(_deliveredStoryKey, keyCheck, valCheck, typeCheck, this.redisClient);
    }

    public Map<String, Map<String, String>> getStoryMeta(String wppXML) {
        Map<String, Map<String, String>> storyMeta = new HashMap<>();
        String key;
        String value;
        try {
            Document wppDocument = XmlUtil.fromString(wppXML.trim());
            NodeList nodes = wppDocument.getElementsByTagName("field");
            for (int i = 0; i < nodes.getLength(); i++) {
                key = "";
                value = "";
                try {
                    Element element = (Element) nodes.item(i);

                    key = XmlUtil.getFirstValue(element, "key");
                    value = XmlUtil.getFirstValue(element, "value");
                    Map<String, String> metaField = new HashMap<>();
                    metaField.put(key, value);

                } catch (Exception e) {
                    String msg = String.format("getStoryMeta() Error: Publisher %s, url %s, title %s, Key %s, Value %s.", publisher.getPubKey(), publisher.getUrl(), _wpp.getTitle(), key, value);
                    RedisLogUtil.logError(msg, e, this.redisClient);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return storyMeta;
    }

    public Map<String, String> postStory(WordPressPost wpp, Map<String, Object> storyDataMap, String postLocation) throws Exception {
        Map<String, String> resultMap;

        String json = JsonUtil.toJSON(wpp);

        resultMap = WordPressAdapter.postJson(json, postLocation, this.wordPressClient);
        if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
            //Record this story so we can find it later
            recordPost(resultMap, storyDataMap);
        }
        //NOT catching exceptions, because if the story doesn't post, the rest of the objects don't matter.
        return resultMap;
    }

    public Map<String, String> postTrackingMeta(String postLocation) {
        Map<String, String> resultMap = new HashMap<>();
        long timestamp = System.nanoTime();

        try {

            Map<String, String> metaHashMap = new HashMap<>();
            metaHashMap.put(timestamp + "_deliveredStoryKey", _deliveredStoryKey);
            metaHashMap.put(timestamp + "_feedKey", _feedKey);
            metaHashMap.put(timestamp + "_contentKey", _contentKey);

            resultMap = postMeta(postLocation, metaHashMap);
        } catch (Exception e) {
            RedisLogUtil.logError(e, this.redisClient);
        }
        return resultMap;
    }

    public Map<String, String> postStoryMeta(String postLocation, Map<String, Map<String, String>> storyMeta) {
        Map<String, String> resultMap = new HashMap<>();


        for (Map.Entry<String, Map<String, String>> metaEntry : storyMeta.entrySet()) {
            try {
                Map<String, String> fieldEntry = metaEntry.getValue();
                resultMap = postMeta(postLocation, fieldEntry);
            } catch (Exception e) {
                RedisLogUtil.logError(e, redisClient);
            }
        }
        return resultMap;
    }


    public Map<String, String> postMeta(String postLocation, Map<String, String> meta) {
        Map<String, String> resultMap = new HashMap<>();
        String postEndpoint = postLocation + "/meta";
        String json;
        String key = "";
        Object value = "";

        for (Map.Entry<String, String> entry : meta.entrySet()) {
            try {
                key = entry.getKey();
                value = StringEscapeUtils.unescapeHtml(entry.getValue());
                json = String.format("{ \"key\":\"%s\",\"value\":\"%s\" }", key, value);
                resultMap = WordPressAdapter.postJson(json, postEndpoint, this.wordPressClient);
            } catch (Exception e) {
                String errMsg = "Meta post error for: " + key + "(" + value + ")" + " into Subscriber: " + _subscriberMap.get("name") + " at: " + postLocation + " for contentKey: " + _contentKey + " from feedKey: " + _feedKey + " " + ExceptionUtil.getFullStackTrace(e);
                RedisLogUtil.logError(errMsg, this.redisClient);
            }
        }
        return resultMap;
    }

    public ArrayList<Integer> setCats() {
        ArrayList<Integer> catList = new ArrayList<>();
        String catKey;
        String catId = "";

        try {
            if (isNumber(_subscriberMap.get("catid"))) {
                catList.add(Integer.parseInt(_subscriberMap.get("catid")));
            } else {
                StorySubjects storySubjects = StorySubjects.fromXML(_subjectsXML);
                String[] cats = storySubjects.getSubjectsXML();
                if (cats != null && cats.length > 0) {
                    for (String cat : cats) {
                        catKey = _catKeyRoot + ":" + cat;
                        if (2 == 9) LogUtil.log("findCatKey: " + catKey);
                        catId = this.redisClient.hget(catKey, "id");
                        if (catId != null) {
                            if (2 == 9) LogUtil.log("addCatKey: " + catKey);
                            catList.add(Integer.parseInt(catId));
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errMsg = "Cat set error into: " + _subscriberMap.get("name") + " for catId: " + catId + " " + ExceptionUtil.getFullStackTrace(e);
            RedisLogUtil.logError(errMsg, this.redisClient);
        }
        return catList;
    }

    public ArrayList<Integer> setTags() {
        ArrayList<Integer> tagList = new ArrayList<>();
        String tagKey;
        String tagId = "";

        try {
            StorySubjects storySubjects = StorySubjects.fromXML(_subjectsXML);
            String[] tags = storySubjects.getSubjectsXML();
            if (tags != null && tags.length > 0) {
                for (String tag : tags) {
                    tagKey = _tagKeyRoot + ":" + tag;
                    if (2 == 9) LogUtil.log("findTagKey: " + tagKey);
                    tagId = this.redisClient.hget(tagKey, "id");
                    if (tagId != null) {
                        if (2 == 9) LogUtil.log("addTagKey: " + tagKey);
                        tagList.add(Integer.parseInt(tagId));
                    }
                }
            }
        } catch (Exception e) {
            String errMsg = "Tag set error into: " + _subscriberMap.get("name") + " for tagId: " + tagId + " " + ExceptionUtil.getFullStackTrace(e);
            RedisLogUtil.logError(errMsg, this.redisClient);
        }
        return tagList;
    }

    public Map<String, String> postImages(String wpPostid, String postLocation, String authorId, WordPressPost wpp, String mediaBaseEndpoint) {
        Map<String, String> resultMap = new HashMap<>();
        Image image = new Image();
        String imageName = "";
        String imageKey = "";
        String json;
        Boolean bFeatured;
        Boolean bNewFeature = true;

        try {
            Image[] images = wpp.getImages();
            if (images != null && images.length != 0) {

                //Look in Redis to see if any of these are already the feature
                for (Image imageCheck : images) {
                    // image = imageCheck.getImage();
                    if (imageCheck != null) {
                        String[] imageKeyArgs = {_deliveredStoryKey, "image", imageCheck.getGuid()};
                        imageKey = RedisContentUtil.setKey(imageKeyArgs);
                        if (RedisContentUtil.keyExists(imageKey, "featured", this.redisClient)) {
                            //This image is already the featured image; prevent any others from trying
                            bNewFeature = false;
                        }
                    }
                }

                //Post images
                for (int i = 0; i < images.length; i++) {
                    //if this is the first image, and no others are featured, make this the featured image
                    bFeatured = (i == 0 && bNewFeature);
                    image = images[i];
                    if (image != null) {
                        String[] imageKeyArgs = {_deliveredStoryKey, "image", image.getGuid()};
                        imageKey = RedisContentUtil.setKey(imageKeyArgs);
                        //See if this image exists; if so, skip it
                        if (RedisContentUtil.isNew(imageKey, "id", this.redisClient)) {
                            imageName = StringUtil.hyphenateString(image.getName());
                            json = "{ \"post_id\":" + wpPostid + ", \"postlocation\":\"" + postLocation + "\", \"name\":\"" + imageName.replace("\"", "\\\"") + "\", \"source\":\"" + image.getSource() + "\", \"mimetype\":\"" + image.getMimetype() + "\", \"caption\":\"" + image.getCaption().replace("\"", "\\\"") + "\", \"featured\":\"" + bFeatured + "\", \"author\":\"" + authorId + "\", \"date\":\"" + wpp.getDate() + "\" }";
                            resultMap = WordPressAdapter.postMedia(json, mediaBaseEndpoint, this.wordPressClient);
                            if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
                                //Record this image so we can find it later
                                recordImage(wpPostid, resultMap, image, imageKey, bFeatured.toString());
                                LogUtil.log("postImages info: " + _subscriberMap.get("name") + " for: \"" + image.getSource() + "\" at: " + resultMap.get("mediaLocation"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errMsg = "Image post error for: " + imageName + " into Subscriber: " + _subscriberMap.get("name") + " for: " + postLocation + ", Getting/Posting imageKey: " + imageKey + ", source: " + image.getSource() + ", " + ExceptionUtil.getFullStackTrace(e);
            RedisLogUtil.logError(errMsg, this.redisClient);
        }
        return resultMap;
    }

    private void recordPost(Map<String, String> storyPostMap, Map<String, Object> storyDataMap) {
        Map<String, String> postMap = new HashMap<>();
        try {
            postMap.put("id", storyPostMap.get("wpPostId"));
            String postLocation = storyPostMap.get("postLocation");
            if (postLocation != null) {
                postMap.put("location", storyPostMap.get("postLocation"));
            }
            postMap.put((String) storyDataMap.get("keyCheck"), (String) storyDataMap.get("valueCheck"));
            LogUtil.log("_deliveredStoryKey: " + _deliveredStoryKey + ", postMap: " + postMap.toString());
            RedisContentUtil.trackContent(_deliveredStoryKey, postMap, this.redisClient);
        } catch (Exception e) {
            postMap.put("error", ExceptionUtil.getFullStackTrace(e));
        }
    }

    private void recordImage(String wpPostid, Map<String, String> imagePostMap, Image image, String imageKey, String featured) {
        Map<String, String> imageHashMap = new HashMap<>();
        try {
            String wpImageId = JsonUtil.getValue(imagePostMap.get("result"), "id");
            String mediaLocation = imagePostMap.get("mediaLocation");
            imageHashMap.put("guid", image.getGuid());
            imageHashMap.put("location", mediaLocation);
            imageHashMap.put("id", wpImageId);
            imageHashMap.put("postid", wpPostid);
            imageHashMap.put("name", image.getName());
            imageHashMap.put("featured", featured);
            RedisContentUtil.trackContent(imageKey, imageHashMap, this.redisClient);
        } catch (Exception e) {
            imageHashMap.put("error", ExceptionUtil.getFullStackTrace(e));
        }
    }

    public Publisher getPublisher() {
        return this.publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public WordPressClient getWordPressClient() {
        return this.wordPressClient;
    }

    public void setWordPressClient(WordPressClient wordPressClient) {
        this.wordPressClient = wordPressClient;
    }

    public String getDeliveredStoryKey() {
        return _deliveredStoryKey;
    }

    public void setDeliveredStoryKey(String deliveredStoryKey) {
        this._deliveredStoryKey = deliveredStoryKey;
    }

    public String getFeedKey() {
        return _feedKey;
    }

    public void setFeedKey(String feedKey) {
        this._feedKey = feedKey;
    }

    public String getContentKey() {
        return _contentKey;
    }

    public void setContentKey(String contentKey) {
        this._contentKey = contentKey;
    }

    public String getSubjectsXML() {
        return _subjectsXML;
    }

    public void setSubjectsXML(String subjectsXML) {
        this._subjectsXML = subjectsXML;
    }

    public Map<String, String> getSubscriberMap() {
        return _subscriberMap;
    }

    public void setSubscriberMap(Map<String, String> subscriberMap) {
        this._subscriberMap = subscriberMap;
    }
}
