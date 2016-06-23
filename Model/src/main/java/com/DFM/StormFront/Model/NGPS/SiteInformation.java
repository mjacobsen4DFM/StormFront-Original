package com.DFM.StormFront.Model.NGPS;

/**
 * Created by Mick on 5/3/2016.
 */
public class SiteInformation
{
    private String siteUrl;

    private String logoURL;

    private String siteId;

    private String siteProductionUrl;

    private String siteProductionRssUrl;

    private String siteName;

    public String getSiteUrl ()
    {
        return siteUrl;
    }

    public void setSiteUrl (String siteUrl)
    {
        this.siteUrl = siteUrl;
    }

    public String getLogoURL ()
    {
        return logoURL;
    }

    public void setLogoURL (String logoURL)
    {
        this.logoURL = logoURL;
    }

    public String getSiteId ()
    {
        return siteId;
    }

    public void setSiteId (String siteId)
    {
        this.siteId = siteId;
    }

    public String getSiteProductionUrl ()
    {
        return siteProductionUrl;
    }

    public void setSiteProductionUrl (String siteProductionUrl)
    {
        this.siteProductionUrl = siteProductionUrl;
    }

    public String getSiteProductionRssUrl ()
    {
        return siteProductionRssUrl;
    }

    public void setSiteProductionRssUrl (String siteProductionRssUrl)
    {
        this.siteProductionRssUrl = siteProductionRssUrl;
    }

    public String getSiteName ()
    {
        return siteName;
    }

    public void setSiteName (String siteName)
    {
        this.siteName = siteName;
    }

    @Override
    public String toString()
    {
        return "SiteInformation [siteUrl = "+siteUrl+", logoURL = "+logoURL+", siteId = "+siteId+", siteProductionUrl = "+siteProductionUrl+", siteProductionRssUrl = "+siteProductionRssUrl+", siteName = "+siteName+"]";
    }
}
