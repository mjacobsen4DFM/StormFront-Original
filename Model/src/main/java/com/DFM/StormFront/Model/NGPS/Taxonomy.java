package com.DFM.StormFront.Model.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class Taxonomy
{
    private String[] level;

    public String[] getLevel ()
    {
        return level;
    }

    public void setLevel (String[] level)
    {
        this.level = level;
    }

    @Override
    public String toString()
    {
        return "Taxonomy [level = "+level+"]";
    }
}
