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

//@SuppressWarnings("restriction")
@XmlRootElement(name = "subjects")
public class StorySubjects implements Serializable {
    private static final long serialVersionUID = -428988001071921405L;
    private String[] subjectsXML;

    StorySubjects() {
    }

    public static StorySubjects fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(StorySubjects.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<StorySubjects> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), StorySubjects.class);
        return je1.getValue();
    }

    public String toXml() throws Exception {
        Document doc = XmlUtil.deserialize(this);
        return XmlUtil.toString(doc);
    }


    public String[] getSubjectsXML() {
        return subjectsXML;
    }

    @XmlElement(name = "subject")
    public void setSubjectsXML(String[] subject) {
        this.subjectsXML = subject;
    }
}