<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:nitf="http://iptc.org/stdnitf/2006-10-18/"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:apcm="http://ap.org/schemas/03/2005/apcm"
                xmlns:apnm="http://ap.org/schemas/03/2005/apnm"
                xmlns:georss="http://www.georss.org/georss"
                xmlns:o="http://w3.org/ns/odrl/2/"
                exclude-result-prefixes="nitf atom apcm apnm georss o">
  <xsl:output method="xml" indent="yes" cdata-section-elements="content excerpt caption"/>

  <xsl:template match="@* | node()">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <wp-api>
      <guid>
        <xsl:value-of select="normalize-space(/article/article_uid)"/>
      </guid>
      <sequence type="int">
        <xsl:value-of select="0"/>
      </sequence>
      <date>
        <xsl:value-of select="normalize-space(/article/startdate)"/>
      </date>
      <title>
        <xsl:value-of select="normalize-space(/article/heading)"/>
      </title>
      <byline>
        <xsl:value-of select="normalize-space(/article/byline)"/>
      </byline>
      <source>
        <xsl:value-of select="normalize-space(/article/siteid)"/>
      </source>
      <content>
        <xsl:value-of select="normalize-space(/article/body)"/>
      </content>
      <excerpt>
        <xsl:value-of select="normalize-space(/article/summary)"/>
      </excerpt>
      <images>
        <xsl:for-each select="/article/images/image">
          <image>
            <source>
              <xsl:value-of select="normalize-space(imagepath)"/>
            </source>
            <guid>
              <xsl:value-of select="normalize-space(asset_uid)"/>
            </guid>
            <caption>
              <xsl:value-of select="normalize-space(caption)"/>
            </caption>
            <name>
              <xsl:value-of select="normalize-space(asset_uid)"/>
            </name>
            <title>
              <xsl:value-of select="normalize-space(asset_uid)"/>
            </title>
          </image>
        </xsl:for-each>
      </images>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>
