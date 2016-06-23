package com.DFM.StormFront.Model.NGPS;

import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Mick on 5/3/2016.
 */
@XmlRootElement(name = "article")
public class Article
{
    private String headline;

    private String headlineEncoded;

    private String body;

    private String startDate;

    private String firstPubDateISO8601;

    private ArticleAssociation[] articleAssociations;

    private String keepIndefinitely;

    private String bodyIframes;

    private String endDate;

    private String bodyScripts;

    private String revision;

    private String title;

    private String bodyEmbeds;

    private String byline;

    private String bylineEncoded;

    private String canonicalUrl;

    private String isShareable;

    private String originatingSite;

    private String createDateISO8601;

    private String bodyObjects;

    private RelatedArticle[] relatedArticles;

    private String previousRevision;

    private String createDate;

    private String launchDate;

    private String isUpdate;

    private String overline;

    private String keyword;

    private String dateLine;

    private String endDateISO8601;

    private String blurb;

    private String updateDate;

    private String isExportable;

    private String sectionAnchor;

    private String seoDescriptiveText;

    private String contentItemVersion;

    private String updateDateISO8601;

    private List<Image> images;

    private String slug;

    private String dateId;

    private String cId;

    private String launchDateISO8601;

    private String daysToLive;

    private boolean delete;



    public SiteInformation siteInformation;

    public BodyAnchors bodyAnchors;

    public Taxonomy taxonomy;

    public ContentGroups contentGroups;




    public static Article fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException {
        JAXBContext jc = JAXBContext.newInstance(Article.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<Article> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), Article.class);
        return je1.getValue();
    }

    public String toXml() throws Exception {
        Document doc = XmlUtil.deserialize(this);
        return XmlUtil.toString(doc);
    }


    public Article fromJSON(String source, String sourceType) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        switch (sourceType) {
            case "file":
                return mapper.readValue(new File(source), this.getClass());
            case "url":
                return mapper.readValue(new URL(source), this.getClass());
            case "string":
                return mapper.readValue(source, this.getClass());
            default:
                throw new Exception("Unknown sourceType converting from JSON: " + sourceType);
        }
    }



    public String getHeadline ()
    {
        return headline;
    }

    public void setHeadline (String headline)
    {
        this.headline = headline;
    }

    public String getHeadlineEncoded ()
    {
        return headlineEncoded;
    }

    public void setHeadlineEncoded (String headlineEncoded)
    {
        this.headlineEncoded = headlineEncoded;
    }

    public String getBody ()
    {
        return body;
    }

    public void setBody (String body)
    {
        this.body = body;
    }

    public String getStartDate ()
    {
        return startDate;
    }

    public void setStartDate (String startDate)
    {
        this.startDate = startDate;
    }

    public String getFirstPubDateISO8601 ()
    {
        return firstPubDateISO8601;
    }

    public void setFirstPubDateISO8601 (String firstPubDateISO8601)
    {
        this.firstPubDateISO8601 = firstPubDateISO8601;
    }

    public ArticleAssociation[] getArticleAssociations ()
    {
        return articleAssociations;
    }

    public void setArticleAssociations (ArticleAssociation[] articleAssociations)
    {
        this.articleAssociations = articleAssociations;
    }

    public String getKeepIndefinitely ()
    {
        return keepIndefinitely;
    }

    public void setKeepIndefinitely (String keepIndefinitely)
    {
        this.keepIndefinitely = keepIndefinitely;
    }

    public String getBodyIframes ()
    {
        return bodyIframes;
    }

    public void setBodyIframes (String bodyIframes)
    {
        this.bodyIframes = bodyIframes;
    }

    public String getEndDate ()
    {
        return endDate;
    }

    public void setEndDate (String endDate)
    {
        this.endDate = endDate;
    }

    public String getBodyScripts ()
    {
        return bodyScripts;
    }

    public void setBodyScripts (String bodyScripts)
    {
        this.bodyScripts = bodyScripts;
    }

    public String getRevision ()
    {
        return revision;
    }

    public void setRevision (String revision)
    {
        this.revision = revision;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getBodyEmbeds ()
    {
        return bodyEmbeds;
    }

    public void setBodyEmbeds (String bodyEmbeds)
    {
        this.bodyEmbeds = bodyEmbeds;
    }

    public String getBylineEncoded ()
    {
        return bylineEncoded;
    }

    public void setBylineEncoded (String bylineEncoded)
    {
        this.bylineEncoded = bylineEncoded;
    }

    public String getCanonicalUrl ()
    {
        return canonicalUrl;
    }

    public void setCanonicalUrl (String canonicalUrl)
    {
        this.canonicalUrl = canonicalUrl;
    }

    public String getIsShareable ()
    {
        return isShareable;
    }

    public void setIsShareable (String isShareable)
    {
        this.isShareable = isShareable;
    }

    public String getOriginatingSite ()
    {
        return originatingSite;
    }

    public void setOriginatingSite (String originatingSite)
    {
        this.originatingSite = originatingSite;
    }

    public String getCreateDateISO8601 ()
    {
        return createDateISO8601;
    }

    public void setCreateDateISO8601 (String createDateISO8601)
    {
        this.createDateISO8601 = createDateISO8601;
    }

    public String getBodyObjects ()
    {
        return bodyObjects;
    }

    public void setBodyObjects (String bodyObjects)
    {
        this.bodyObjects = bodyObjects;
    }

    public RelatedArticle[] getRelatedArticles ()
    {
        return relatedArticles;
    }

    public void setRelatedArticles (RelatedArticle[] relatedArticles)
    {
        this.relatedArticles = relatedArticles;
    }

    public String getPreviousRevision ()
    {
        return previousRevision;
    }

    public void setPreviousRevision (String previousRevision)
    {
        this.previousRevision = previousRevision;
    }

    public String getCreateDate ()
    {
        return createDate;
    }

    public void setCreateDate (String createDate)
    {
        this.createDate = createDate;
    }

    public String getLaunchDate ()
    {
        return launchDate;
    }

    public void setLaunchDate (String launchDate)
    {
        this.launchDate = launchDate;
    }

    public String getIsUpdate ()
    {
        return isUpdate;
    }

    public void setIsUpdate (String isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public String getOverline ()
    {
        return overline;
    }

    public void setOverline (String overline)
    {
        this.overline = overline;
    }

    public String getKeyword ()
    {
        return keyword;
    }

    public void setKeyword (String keyword)
    {
        this.keyword = keyword;
    }

    public String getDateLine ()
    {
        return dateLine;
    }

    public void setDateLine (String dateLine)
    {
        this.dateLine = dateLine;
    }

    public String getEndDateISO8601 ()
    {
        return endDateISO8601;
    }

    public void setEndDateISO8601 (String endDateISO8601)
    {
        this.endDateISO8601 = endDateISO8601;
    }

    public String getBlurb ()
    {
        return blurb;
    }

    public void setBlurb (String blurb)
    {
        this.blurb = blurb;
    }

    public String getUpdateDate ()
    {
        return updateDate;
    }

    public void setUpdateDate (String updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getIsExportable ()
    {
        return isExportable;
    }

    public void setIsExportable (String isExportable)
    {
        this.isExportable = isExportable;
    }

    public String getSectionAnchor ()
    {
        return sectionAnchor;
    }

    public void setSectionAnchor (String sectionAnchor)
    {
        this.sectionAnchor = sectionAnchor;
    }

    public String getSeoDescriptiveText ()
    {
        return seoDescriptiveText;
    }

    public void setSeoDescriptiveText (String seoDescriptiveText)
    {
        this.seoDescriptiveText = seoDescriptiveText;
    }

    public String getContentItemVersion ()
    {
        return contentItemVersion;
    }

    public void setContentItemVersion (String contentItemVersion)
    {
        this.contentItemVersion = contentItemVersion;
    }

    public String getUpdateDateISO8601 ()
    {
        return updateDateISO8601;
    }

    public void setUpdateDateISO8601 (String updateDateISO8601)
    {
        this.updateDateISO8601 = updateDateISO8601;
    }

    public List<Image> getImages ()
    {
        return images;
    }

    @XmlElementWrapper(name="images")
    @XmlElement(name = "image")
    public void setImages (List<Image> images)
    {
        this.images = images;
    }

    public String getSlug ()
    {
        return slug;
    }

    public void setSlug (String slug)
    {
        this.slug = slug;
    }

    public String getDateId ()
    {
        return dateId;
    }

    public void setDateId (String dateId)
    {
        this.dateId = dateId;
    }

    public String getCId ()
    {
        return cId;
    }

    @XmlElement(name = "cId")
    public void setCId (String cId)
    {
        this.cId = cId;
    }

    public String getLaunchDateISO8601 ()
    {
        return launchDateISO8601;
    }

    public void setLaunchDateISO8601 (String launchDateISO8601)
    {
        this.launchDateISO8601 = launchDateISO8601;
    }

    public String getDaysToLive ()
    {
        return daysToLive;
    }

    public void setDaysToLive (String daysToLive)
    {
        this.daysToLive = daysToLive;
    }

    public String getStoryGuid() {
        return cId;
    }
    @Override
    public String toString()
    {
        return "Article [headline = "+headline+", headlineEncoded = "+headlineEncoded+", body = "+body+", startDate = "+startDate+", firstPubDateISO8601 = "+firstPubDateISO8601+", articleAssociations = "+articleAssociations+", keepIndefinitely = "+keepIndefinitely+", bodyIframes = "+bodyIframes+", endDate = "+endDate+", bodyScripts = "+bodyScripts+", revision = "+revision+", title = "+title+", bodyEmbeds = "+bodyEmbeds+", bylineEncoded = "+bylineEncoded+", canonicalUrl = "+canonicalUrl+", isShareable = "+isShareable+", siteInformation = "+siteInformation+", originatingSite = "+originatingSite+", contentGroups = "+contentGroups+", createDateISO8601 = "+createDateISO8601+", bodyObjects = "+bodyObjects+", relatedArticles = "+relatedArticles+", previousRevision = "+previousRevision+", taxonomy = "+taxonomy+", createDate = "+createDate+", launchDate = "+launchDate+", isUpdate = "+isUpdate+", overline = "+overline+", keyword = "+keyword+", dateLine = "+dateLine+", endDateISO8601 = "+endDateISO8601+", blurb = "+blurb+", updateDate = "+updateDate+", isExportable = "+isExportable+", sectionAnchor = "+sectionAnchor+", seoDescriptiveText = "+seoDescriptiveText+", contentItemVersion = "+contentItemVersion+", bodyAnchors = "+bodyAnchors+", updateDateISO8601 = "+updateDateISO8601+", images = "+images+", slug = "+slug+", dateId = "+dateId+", cId = "+cId+", launchDateISO8601 = "+launchDateISO8601+", daysToLive = "+daysToLive+"]";
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }
}