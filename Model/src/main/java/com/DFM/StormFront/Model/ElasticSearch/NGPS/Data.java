package com.DFM.StormFront.Model.ElasticSearch.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class Data
{
    private String associationCaption;

    private String associationCredit;

    private String associationURL;

    public String getAssociationCaption ()
    {
        return associationCaption;
    }

    public void setAssociationCaption (String associationCaption)
    {
        this.associationCaption = associationCaption;
    }

    public String getAssociationCredit ()
    {
        return associationCredit;
    }

    public void setAssociationCredit (String associationCredit)
    {
        this.associationCredit = associationCredit;
    }

    public String getAssociationURL ()
    {
        return associationURL;
    }

    public void setAssociationURL (String associationURL)
    {
        this.associationURL = associationURL;
    }

    @Override
    public String toString()
    {
        return "Data [associationCaption = "+associationCaption+", associationCredit = "+associationCredit+", associationURL = "+associationURL+"]";
    }
}

