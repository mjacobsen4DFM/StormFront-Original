package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Adapter.NGPSAdapter;
import com.DFM.StormFront.Client.RedisClient;
import com.DFM.StormFront.Client.Util.RedisContentUtil;
import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Model.Normalize.Image;
import com.DFM.StormFront.Model.Normalize.Story;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mick on 5/18/2016.
 */
public class NGPSExec {
    public Publisher publisher;
    public RedisClient redisClient;
    public String operation;
    public Map<String, Object> storyDataMap;
    private Map<String, String> _subscriberMap;
    private String _deliveredStoryKey;
    private String _feedKey;
    private String _contentKey;
    private String _subjectsXML;
    private String _xsltRootPath;
    private String _story;

    public NGPSExec() {
        this.operation = "new";
    }

    public NGPSExec(Publisher publisher,
                    RedisClient redisClient,
                    Map<String, String> subscriberMap,
                    String deliveredStoryKey,
                    String feedKey,
                    String contentKey,
                    String subjectsXML,
                    String xsltRootPath,
                    String story) throws Exception {
        this.publisher = publisher;
        this.redisClient = redisClient;
        _subscriberMap = subscriberMap;
        _deliveredStoryKey = deliveredStoryKey;
        _feedKey = feedKey;
        _contentKey = contentKey;
        _subjectsXML = subjectsXML;
        _xsltRootPath = xsltRootPath;
        _story = story;
    }


    public Map<String, String> Exec() throws Exception {
        operation = "start";
        Map<String, String> resultMap;

        operation = "Get FTP data";
        //Use Redis to get credentials and workingDirectory
        String host = _subscriberMap.get("host");
        String username = _subscriberMap.get("UT");
        String password = _subscriberMap.get("US");
        String workingDirectory = _subscriberMap.get("WD");

        operation = "Get Normalized data";
        storyDataMap = this.getStoryData(_story);

        operation = "Transform XML to Normalize";
        String xsltPath = _xsltRootPath + this.publisher.getFeedType() + "_Story.xslt";
        String storyXML = XmlUtil.transform(_story, xsltPath);

        operation = "Get fileinfo";
        Story story = Story.fromXML(storyXML);
        String id = story.getId();
        String file_name = story.getFileName();
        String remote_file_name = id + "_" + file_name;
        if(!remote_file_name.contains(".xml")) {
            remote_file_name = remote_file_name + ".xml";
        }
        String base_file_name = remote_file_name.replace(".xml", "");

        operation = "Transform XML to NGPS";
        xsltPath = FileUtil.getXsltDir() + this.publisher.getFeedType() + "_" + _subscriberMap.get("type") + ".xslt";
        String ngpsXML = XmlUtil.transform(_story, xsltPath);

        FileUtil.printFile(FileUtil.getLogDir(),"Hubsync", id + "_xml", file_name.replace(".xml", ""), "xml", ngpsXML);
        InputStream uploadStream = StringUtil.toInputStream(ngpsXML);

        operation = "FTP";
        String json = String.format("{\"host\":\"%s\"}", host);
        JSONObject jsonObject = JsonUtil.fromJSON(json);
        resultMap = NGPSAdapter.PutNGPS(host, username, password, workingDirectory, uploadStream, remote_file_name);
        String code = resultMap.get("code");
        jsonObject.put("file", JsonUtil.fromJSON(resultMap.get("result")));
/*
        JSONArray jsonArrayFile = new JSONArray();
        jsonArrayFile.add(resultMap.get("result"));
*/
        JSONArray jsonArrayImages = new JSONArray();

        if (WebClient.isOK(Integer.parseInt(resultMap.get("code").trim()))) {
            recordPut(story, storyDataMap);

            LogUtil.log("FTP success: " + _subscriberMap.get("name") + " for: \"" + story.getTitle() + "\" (" + story.getGuid()  + ")");
            operation = "FTP Images";
            Map<String, JSONArray> imageMap = putImages(story, base_file_name, host, username, password, workingDirectory);
            jsonArrayImages = imageMap.get("result");
        } else {
            LogUtil.log("FTP fail: " + _subscriberMap.get("name") + " for: \"" + story.getTitle() + "\"");
        }


        jsonObject.put("image", jsonArrayImages);
        resultMap.put("code", code);
        resultMap.put("result", jsonObject.toJSONString());
        operation = "Finish";
        return resultMap;
    }

    private Map<String, JSONArray> putImages(Story story, String base_file_name, String host, String username, String password, String workingDirectory) {
        Map<String, JSONArray> resultMap = new HashMap<>();
        Map<String, String> imageMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Image images[] = story.images.getImages();
        String imageKey;

        Integer imageCount;
        if(images != null) {
            for (int i = 0; i < images.length; i++) {
                try {
                    imageCount = i + 1;
                    Image image = images[i];
                    String imageSource = image.getSource();
                    String imageCredit = image.getCredit();
                    String imageCaption = image.getCaption();
                    String imageType = image.getMimetype();

                    if (imageType == null || imageType.equals("")) {
                        imageType = "jpg";
                    }

                    WebClient wc = new WebClient();
                    InputStream imageStream = wc.GetImageStream(imageSource, imageType);

                    String imageName = base_file_name + "~" + imageCount + "." + imageType;
                    String cutlineName = base_file_name + "~" + imageCount + "cl.txt";
                    String cutlineText = String.format("<caption>%s</caption>\n<credit><i>%s / %s</i></credit>", imageCaption, imageCredit, story.getSiteName());
                    InputStream cutlineStream = StringUtil.toInputStream(cutlineText);

                    String imageSlug = StringUtil.hyphenateString(imageName);
                    String[] imageKeyArgs = {_deliveredStoryKey, "image", imageSlug};
                    imageKey = RedisContentUtil.setKey(imageKeyArgs);

                    imageMap = NGPSAdapter.PutNGPS(host, username, password, workingDirectory, imageStream, imageName);

                    jsonObject = JsonUtil.fromJSON(imageMap.get("result"));
                    jsonArray.add(jsonObject);

                    if (WebClient.isOK(Integer.parseInt(imageMap.get("code").trim()))) {
                        //Record this image so we can find it later
                        recordImage(image, imageKey);
                        LogUtil.log("ftpImages info: " + _subscriberMap.get("name") + " for: \"" + imageName + "\"");
                    } else {
                        LogUtil.log("ftpImages fail: " + _subscriberMap.get("name") + " for: \"" + imageName + "\"");
                    }


                    imageMap = NGPSAdapter.PutNGPS(host, username, password, workingDirectory, cutlineStream, cutlineName);

                    if (WebClient.isOK(Integer.parseInt(imageMap.get("code").trim()))) {
                        jsonObject.put("cutline", cutlineName);
                        LogUtil.log("ftpCutline info: " + _subscriberMap.get("name") + " for: \"" + cutlineName + "\"");
                    } else {
                        LogUtil.log("ftpCutline fail: " + _subscriberMap.get("name") + " for: \"" + cutlineName + "\"");
                    }

                    jsonArray.add(jsonObject);
                } catch (Exception e) {
                    //ignore image error
                }
            }
        }
        resultMap.put("result", jsonArray);
        return resultMap;
    }


    public Map<String, Object> getStoryData(String xml) throws Exception {
        String keyCheck = "key";
        String valCheck = "value";
        String typeCheck = "type";
        if (publisher.getUpdateCheckType().equalsIgnoreCase("md5")) {
            byte[] b5 = SerializationUtil.serialize(xml);
            String s5 = new String(b5, StandardCharsets.UTF_8);
            String m5 = RedisContentUtil.createMD5(s5);
            keyCheck = this.publisher.getUpdateCheckType();
            valCheck = m5;
            typeCheck = "md5";
        } else if (publisher.getUpdateCheckType().equalsIgnoreCase("sequence")) {
            keyCheck = this.publisher.getUpdateCheckType();
            valCheck = XmlUtil.getNodeValue(xml, "sequence");
            typeCheck = XmlUtil.getAttributeValue(xml, "sequence", "type");
        }
        //NOT catching exceptions, because if we don't know this metadata, the rest of the objects don't matter.
        return RedisContentUtil.createStoryStatusMap(_deliveredStoryKey, keyCheck, valCheck, typeCheck, this.redisClient);
    }

    private void recordPut(Story story, Map<String, Object> storyDataMap) {
        Map<String, String> postMap = new HashMap<>();
        try {
            LogUtil.log("_deliveredStoryKey: " + _deliveredStoryKey + ", postMap: " + postMap.toString());
            postMap.put("title", story.getTitle());
            postMap.put("viewuri", story.getViewURI());
            postMap.put((String) storyDataMap.get("keyCheck"), (String) storyDataMap.get("valueCheck"));
            RedisContentUtil.trackContent(_deliveredStoryKey, postMap, this.redisClient);
        } catch (Exception e) {
            postMap.put("error", ExceptionUtil.getFullStackTrace(e));
        }
    }


    private void recordImage(Image image, String imageKey) {
        Map<String, String> imageHashMap = new HashMap<>();
        try {
            imageHashMap.put("credit", image.getCredit());
            imageHashMap.put("caption", image.getCaption());
            imageHashMap.put("source", image.getSource());
            RedisContentUtil.trackContent(imageKey, imageHashMap, this.redisClient);
        } catch (Exception e) {
            imageHashMap.put("error", ExceptionUtil.getFullStackTrace(e));
        }
    }
}
