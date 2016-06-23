package com.DFM.StormFront.Model.ElasticSearch.NGPS;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "image")
public class Image implements Serializable {
    private static final long serialVersionUID = -5913957382972359157L;
    private String id;
    private String filesize;
    private String height;
    private String width;
    private String caption;
    private String credit;
    private String url;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFilesize ()
    {
        return filesize;
    }

    public void setFilesize (String filesize)
    {
        this.filesize = filesize;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getCaption ()
    {
        return caption;
    }

    public void setCaption (String caption)
    {
        this.caption = caption;
    }

    public String getCredit ()
    {
        return credit;
    }

    public void setCredit (String credit)
    {
        this.credit = credit;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "Image [id = "+id+", filesize = "+filesize+", height = "+height+", width = "+width+", caption = "+caption+", credit = "+credit+", url = "+url+"]";
    }
}
