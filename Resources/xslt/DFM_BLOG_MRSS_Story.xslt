<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:content="http://purl.org/rss/1.0/modules/content/"
        xmlns:wfw="http://wellformedweb.org/CommentAPI/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:atom="http://www.w3.org/2005/Atom"
        xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
        xmlns:slash="http://purl.org/rss/1.0/modules/slash/"
        xmlns:media="http://search.yahoo.com/mrss/"
        xmlns:georss="http://www.georss.org/georss"
        xmlns:dcterms="http://purl.org/dc/terms/"
>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <wp-api>
      <guid>
        <xsl:call-template name="buildGUID">
          <xsl:with-param name="path" select="//item/link"/>
        </xsl:call-template>
      </guid>
      <title>
        <xsl:value-of select="title"/>
      </title>
      <viewURI>
        <xsl:value-of select="//item/link"/>
      </viewURI>
      <subjects>
        <xsl:for-each select="//item/category">
          <subject>
            <xsl:value-of select="."/>
          </subject>
        </xsl:for-each>
      </subjects>
    </wp-api>
  </xsl:template>


  <xsl:template name="buildGUID">
    <xsl:param name="path"/>
    <xsl:variable name="after-http">
      <xsl:value-of select="substring-after($path, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-slash">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-domain">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="domain">
      <xsl:value-of select="substring-before($after-domain, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-yyyy">
      <xsl:value-of select="substring-after($after-domain, '/')"/>
    </xsl:variable>
    <xsl:variable name="yyyy">
      <xsl:value-of select="substring-before($after-yyyy, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-mm">
      <xsl:value-of select="substring-after($after-yyyy, '/')"/>
    </xsl:variable>
    <xsl:variable name="mm">
      <xsl:value-of select="substring-before($after-mm, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-dd">
      <xsl:value-of select="substring-after($after-mm, '/')"/>
    </xsl:variable>
    <xsl:variable name="dd">
      <xsl:value-of select="substring-before($after-dd, '/')"/>
    </xsl:variable>
    <xsl:variable name="seo-id">
      <xsl:value-of select="substring-after($after-dd, '/')"/>
    </xsl:variable>
    <xsl:variable name="seo">
      <xsl:value-of select="substring-before($seo-id, '/')"/>
    </xsl:variable>
    <xsl:variable name="id">
      <xsl:value-of select="substring-before(substring-after($seo-id, '/'), '/')"/>
    </xsl:variable>
    <xsl:value-of select="concat($domain, ':', $yyyy, ':', $mm, ':', $dd, ':', $seo, ':', $id) "/>
  </xsl:template>
</xsl:stylesheet>
