package com.DFM.StormFront.Model.ElasticSearch.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class BodyAnchors
{
    private String[] anchors;

    public String[] getAnchors ()
    {
        return anchors;
    }

    public void setAnchors (String[] anchors)
    {
        this.anchors = anchors;
    }

    @Override
    public String toString()
    {
        return "BodyAnchors [anchors = "+anchors+"]";
    }
}
