package com.DFM.StormFront.Model.NGPS;

import java.io.Serializable;
import java.util.List;

public class Images implements Serializable {
    private static final long serialVersionUID = -2824561042786328184L;
    private String mediaCount;

    public List<Image> image;

    public String getMediaCount ()
    {
        return mediaCount;
    }

    public void setMediaCount (String mediaCount)
    {
        this.mediaCount = mediaCount;
    }

    @Override
    public String toString()
    {
        return "Images [mediaCount = "+mediaCount+", image = "+image+"]";
    }

}
