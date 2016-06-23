package com.DFM.StormFront.Model.WordPress;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Images implements Serializable {
    private static final long serialVersionUID = -2824561042786328184L;
    private Image image;

    public Image getImage() {
        return image;
    }

    @XmlElement(name = "image")
    public void setImage(Image image) {
        this.image = image;
    }

}
