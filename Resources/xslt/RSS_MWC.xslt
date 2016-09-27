<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:media="http://search.yahoo.com/mrss/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:content="http://purl.org/rss/1.0/modules/content/"
                xmlns:dcterms="http://purl.org/dc/terms/"
>
  <xsl:include href="http://delivery.digitalfirstmedia.com/WebServices/ConversionPublisher/BaseFiles/MainFunctions.xslt"/>
  <!--<xsl:include href="http://trfeeds04.medianewsgroup.com/WebServices/ConversionPublisher/BaseFiles/MainFunctions.xslt"/>-->
  <!--<xsl:include href="C:\Users\Mick\Documents\Cloud\Google Drive\mjacobsen@denverpost.com\Dev\Projects\TRFEEDS\Webservices\ConversionPublisher\Basefiles\MainFunctions.xslt"/>-->

  <xsl:output method="text" indent="no"/>

  <xsl:template match="@* | node()">
    <xsl:variable name="source" select="normalize-space(//item/source)"/>
    <xsl:variable name="title_name" select="translate(normalize-space(//item/title-short), ' ', '-')"/>
    <xsl:variable name="yyyy">
      <xsl:call-template name="getYear">
        <xsl:with-param name="date" select="normalize-space(//item/pubDate)" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="content">
      <xsl:value-of select="normalize-space(//item/content:encoded)" disable-output-escaping="yes"/>
    </xsl:variable>
    <xsl:variable name="wordcount" select="string-length($content) - string-length(translate($content,' ','')) +1" />
    
    <xsl:text>GUID: </xsl:text><xsl:value-of select="//item/link"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>Slug: </xsl:text><xsl:value-of select="$title_name"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>Headline: </xsl:text><xsl:value-of select="//item/title"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>Keywords: </xsl:text><xsl:for-each select="//item/categories/category">
      <xsl:value-of select="concat(., '; ')"/>
    </xsl:for-each>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>Copyright: </xsl:text><xsl:value-of select="concat('c.', $yyyy, ' ', $source)"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>Byline: </xsl:text><xsl:value-of select="//item/dc:creator"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>WordCount: </xsl:text><xsl:value-of select="$wordcount"/>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:text>&#09;&#10;</xsl:text>
    <xsl:value-of select="$content" disable-output-escaping="yes"/>
  </xsl:template>
</xsl:stylesheet>

