package com.DFM.StormFront.Model.Normalize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@SuppressWarnings("restriction")
@XmlRootElement(name = "links")
public class StoryLinks {
    private String[] links;

    public String[] getLinks() {
        return links;
    }

    @XmlElement(name = "storyLink")
    public void setLinks(String[] storyLink) {
        this.links = storyLink;
    }
}