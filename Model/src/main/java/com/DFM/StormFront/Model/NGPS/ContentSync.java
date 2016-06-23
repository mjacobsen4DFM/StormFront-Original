package com.DFM.StormFront.Model.NGPS;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mick on 4/18/2016.
 */
@XmlRootElement(name = "articles")
public class ContentSync {
    @XmlElement(name = "article")
    public List<Article> articles;

    //Set by workflow
    private String publisher;

    public ContentSync() {
        articles = new ArrayList<>();
    }

    public ContentSync fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(ContentSync.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<ContentSync> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), ContentSync.class);
        return je1.getValue();
    }

  /*  public ContentSync fromXML(InputStream xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(ContentSync.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<ContentSync> je1 = (JAXBElement<ContentSync>)unmarshaller.unmarshal(xml);  // .unmarshal(xml, ContentSync.class);
        return je1.getValue();
    }*/

/*    public ContentSync fromJSON(String source, String sourceType) throws Exception {
        Article article = new Article();

        String art = JsonUtil.getValue(source, "article");
        article = article.fromJSON(art, sourceType);
        this.articles.add(article);
        return this;
    }*/

    public String toXml() throws Exception {
        Document doc = XmlUtil.deserialize(this);
        return XmlUtil.toString(doc);
    }

    @Override
    public String toString()
    {
        return "ContentSync [articles = "+articles+"]";
    }


}
