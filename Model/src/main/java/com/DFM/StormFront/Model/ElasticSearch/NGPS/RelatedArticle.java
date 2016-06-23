package com.DFM.StormFront.Model.ElasticSearch.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class RelatedArticle
{
    private String id;

    private String title;

    private String url;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
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
        return "RelatedArticle [id = "+id+", title = "+title+", url = "+url+"]";
    }
}
