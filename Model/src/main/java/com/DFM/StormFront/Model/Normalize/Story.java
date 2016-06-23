package com.DFM.StormFront.Model.Normalize;

import com.DFM.StormFront.Util.XmlUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;


@XmlRootElement(name = "story")
public class Story implements Serializable {
    private static final long serialVersionUID = -3525006222516929810L;
    private String title;
    private String id;
    private String guid;
    private String md5;
    private String updateCheck;
    private String viewURI;
    private String siteName;
    private String fileName;

    @XmlElement(name = "storyLinks")
    public StoryLinks storyLinks;

    @XmlElement(name = "subjects")
    public StorySubjects storySubjects;

    @XmlElement(name = "images")
    public Images images;

    public static Story fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(Story.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<Story> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), Story.class);
        return je1.getValue();
    }

    public String toXml() throws Exception {
        Document doc = XmlUtil.deserialize(this);
        return XmlUtil.toString(doc);
    }

    public String getTitle() {
        return title;
    }

    @XmlElement(name = "title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    @XmlElement(name = "guid")
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getupdateCheck() {
        return updateCheck;
    }

    @XmlElement(name = "updateCheck")
    public void setupdateCheck(String updateCheck) {
        this.updateCheck = updateCheck;
    }

    public String getMd5() {
        return md5;
    }

    @XmlElement(name = "md5")
    public void setMd5(String md5) {
        this.md5 = md5;
    }

 /*
    public StoryLinks getStoryLinks() {
        return storyLinks;
    }

    @XmlElement(name = "storyLinks")
    public void setStoryLinks(StoryLinks storyLinks) {
        this.storyLinks = storyLinks;
    }

    public StorySubjects getStorySubjects() {
        return storySubjects;
    }

    @XmlElement(name = "subjects")
    public void setStorySubjects(StorySubjects storySubjects) {
        this.storySubjects = storySubjects;
    }

    public Images getImages() {
        return images;
    }

    @XmlElement(name = "images")
    public void setImages(Images images) {
        this.images = images;
    }
*/
    public String getViewURI() {
        return viewURI;
    }

    @XmlElement(name = "viewURI")
    public void setViewURI(String viewURI) {
        this.viewURI = viewURI;
    }

    public String getFileName() {
        return fileName;
    }

    @XmlElement(name = "fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSiteName() {
        return siteName;
    }

    @XmlElement(name = "siteName")
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
