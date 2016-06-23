package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Mick on 4/18/2016.
 */
@XmlRootElement(name = "HubSync")
public class HubSync {

    private String method;
    private String title;
    private String slug;
    private String category;
    private String content;
    private String excerpt;
    private String date;
    private String date_modified_gmt;
    private String status;
    private String post_id;
    private String source;
    private String source_id;
    private String source_type;
    private String source_link;
    private String source_author_id;
    private String source_author_name;

    //coming
    private String site_name;
    private String site_url;
    private String remote_file_name;

    private Term[] terms;
    private Image[] images;

    //Set by workflow
    private String publisher;

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getRemote_file_name() {
        return remote_file_name;
    }

    public void setRemote_file_name(String remote_file_name) {
        this.remote_file_name = remote_file_name;
    }

    public HubSync() {
    }

    public HubSync fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(HubSync.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<HubSync> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), HubSync.class);
        return je1.getValue();
    }

    public HubSync fromJSON(String source, String sourceType) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        switch (sourceType) {
            case "file":
                return mapper.readValue(new File(source), this.getClass());
            case "url":
                return mapper.readValue(new URL(source), this.getClass());
            case "string":
                return mapper.readValue(source, this.getClass());
            default:
                throw new Exception("Unknown sourceType converting from JSON: " + sourceType);
        }
    }

    public String toXml() throws Exception {
        Document doc = XmlUtil.deserialize(this);
        return XmlUtil.toString(doc);
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_modified_gmt() {
        return date_modified_gmt;
    }

    public void setDate_modified_gmt(String date_modified_gmt) {
        this.date_modified_gmt = date_modified_gmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getSource_link() {
        return source_link;
    }

    public void setSource_link(String source_link) {
        this.source_link = source_link;
    }

    public String getSource_author_id() {
        return source_author_id;
    }

    public void setSource_author_id(String source_author_id) {
        this.source_author_id = source_author_id;
    }

    public String getSource_author_name() {
        return source_author_name;
    }

    public void setSource_author_name(String source_author_name) {
        this.source_author_name = source_author_name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }
}
