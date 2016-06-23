package com.DFM.StormFront.Model.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class ArticleAssociation
{
    private String id;

    private String priority;

    private String type;


    public Data data;



    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPriority ()
    {
        return priority;
    }

    public void setPriority (String priority)
    {
        this.priority = priority;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ArticleAssociation [id = "+id+", priority = "+priority+", data = "+data+", type = "+type+"]";
    }
}