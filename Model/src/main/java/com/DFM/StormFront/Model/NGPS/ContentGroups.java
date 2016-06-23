package com.DFM.StormFront.Model.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class ContentGroups
{
    private String[] contentGroup;

    public String[] getContentGroup ()
    {
        return contentGroup;
    }

    public void setContentGroup (String[] contentGroup)
    {
        this.contentGroup = contentGroup;
    }

    @Override
    public String toString()
    {
        return "ContentGroups [contentGroup = "+contentGroup+"]";
    }
}