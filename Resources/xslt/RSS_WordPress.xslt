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

  <xsl:output method="xml" indent="yes" cdata-section-elements="title byline source post_site_name"/>

  <xsl:template match="@* | node()">
    <wp-api>
      <guid>
        <xsl:variable name="guid" select="//item/guid"/>
        <xsl:choose>
          <xsl:when test="contains($guid, 'http://')">
            <xsl:value-of select="translate(substring-after($guid, 'http://'), '/', ':')"/>
          </xsl:when>
          <xsl:when test="contains($guid, 'https://')">
            <xsl:value-of select="translate(substring-after($guid, 'https://'), '/', ':')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$guid"/>
          </xsl:otherwise>
        </xsl:choose>
      </guid>
      <date>
        <xsl:call-template name="formatDateLong">
          <xsl:with-param name="date" select="normalize-space(//item/pubDate)" />
        </xsl:call-template>
      </date>
      <title>
        <xsl:value-of select="normalize-space(//item/title)"/>
      </title>
      <byline>
        <xsl:value-of select="normalize-space(//item/author)"/>
      </byline>
      <source>
        <xsl:value-of select="normalize-space(//item/source)"/>
      </source>
      <post_site_name>
        <xsl:value-of select="normalize-space(//item/source)"/>
      </post_site_name>
      
      <xsl:variable name="content">
        <xsl:value-of select="normalize-space(//item/description)" disable-output-escaping="yes"/>
      </xsl:variable>
      <content>
        <xsl:value-of select="$content" disable-output-escaping="yes"/>
      </content>
      <excerpt>
        <xsl:variable name="excerpt">
          <xsl:choose>
            <xsl:when test="starts-with($content, '&lt;p&gt;')">
              <xsl:value-of select="substring-before(substring-after($content, '&lt;p&gt;'), '&lt;/p&gt;')"/>
            </xsl:when>
            <xsl:when test="starts-with($content, '&amp;lt;p&amp;gt;')">
              <xsl:value-of select="substring-before(substring-after($content, '&amp;lt;p&amp;gt;'), '&amp;lt;/p&amp;gt;')"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat(substring-before($content, '.'), '.')"/>              
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$excerpt" disable-output-escaping="yes"/>
      </excerpt>

      <images>
        <xsl:variable name="title" select="normalize-space(//item/title)"/>
        <xsl:variable name="title_name" select="translate(normalize-space(//item/title), ' ', '_')"/>
        <xsl:for-each select="//item/enclosure">
          <xsl:variable name="i" select="position()" />
          <image>
            <guid>
              <xsl:value-of select="concat($title_name, '(', $i, ')')"/>
            </guid>
            <mime-type>
              <xsl:value-of select="normalize-space(@type)"/>
            </mime-type>
            <source>
              <xsl:value-of select="normalize-space(@url)"/>
            </source>
            <caption/>
            <name>
              <xsl:value-of select="concat($title_name, '(', $i, ')')"/>
            </name>
            <title>
              <xsl:value-of select="concat($title, '(', $i, ')')"/>
            </title>
            <credit>
              <xsl:value-of select="normalize-space(//item/source)"/>
            </credit>
          </image>
        </xsl:for-each>
      </images>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>