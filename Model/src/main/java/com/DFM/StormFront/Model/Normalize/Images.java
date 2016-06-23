package com.DFM.StormFront.Model.Normalize;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Images implements Serializable {
    private static final long serialVersionUID = -2824561042786328184L;
    private Image[] images;

    public com.DFM.StormFront.Model.Normalize.Image[] getImages() {
        return images;
    }

    @XmlElement(name = "image")
    public void setImages(Image[] images) {
        this.images = images;
    }

}
