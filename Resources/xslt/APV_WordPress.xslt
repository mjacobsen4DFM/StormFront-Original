<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:nitf="http://iptc.org/std/NITF/2006-10-18/"
                exclude-result-prefixes="nitf">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <wp-api>
      <guid>
        <xsl:value-of select="/nitf/head/docdata/date.issue/@norm"/>
        <xsl:variable name="hed" select="translate(/nitf/body/body.head/hedline/hl1, ' -!@#$%^&amp;*(){}:&lt;>[]\;,./=', '')"/>
        <xsl:variable name="hedc" select="translate($hed, $quot, '')"/>
        <xsl:value-of select="translate($hedc, $apos, '')"/>
      </guid>
      <date>
        <xsl:variable name="date" select="/nitf/head/docdata/date.issue/@norm"/>
        <xsl:value-of select="concat(substring($date, 1,4), '-',substring($date, 5,2), '-',substring($date, 7,2), 'T', substring($date, 10,2), ':',substring($date, 12,2), ':',substring($date, 14,2), '+00:00')"/>
      </date>
      <title>
        <xsl:value-of select="/nitf/body/body.head/hedline/hl1"/>
      </title>
      <byline>
        <xsl:value-of select="/nitf/body/body.head/byline/text()"/>
      </byline>
      <source>
        <xsl:value-of select="/nitf/body/body.head/distributor/text()"/>
      </source>
      <content>
        <xsl:if test="/nitf/body/body.content/block[@id='Main']/p">
          <xsl:for-each select="/nitf/body/body.content/block[@id='Main']/p">
            <xsl:text>&lt;p&gt;</xsl:text>
            <xsl:value-of select="."/>
            <xsl:text>&lt;/p&gt;</xsl:text>
          </xsl:for-each>
        </xsl:if>
        <xsl:if test="/nitf/body/body.content/block[@id='Main']/hl2">
          <xsl:for-each select="/nitf/body/body.content/block[@id='Main']">
            <xsl:text>&lt;p&gt;</xsl:text>
            <xsl:value-of select="hl2"/>
            <xsl:text>&lt;/p&gt;</xsl:text>
          </xsl:for-each>
        </xsl:if>
      </content>
      <excerpt>
        <xsl:if test="/nitf/body/body.content/block[@id='Main']/p">
          <xsl:value-of select="/nitf/body/body.content/block[@id='Main']/p[1]"/>
        </xsl:if>
        <xsl:if test="/nitf/body/body.content/block[@id='Main']/hl2">
          <xsl:text>HEADLINES ONLY</xsl:text>
        </xsl:if>
      </excerpt>
      <images>
        <xsl:for-each select="/nitf/body/body.content/media[@media-type='Photo']">
          <image>
            <xsl:for-each select="media-reference[@name='AP Photo']">
                <mime-type>
                  <xsl:value-of select="@mime-type"/>
                </mime-type>
                <source>
                  <xsl:value-of select="@source"/>
                </source>
            </xsl:for-each>
            <caption>
              <xsl:for-each select="media-caption/p">                
                <xsl:value-of select="concat('&lt;p&gt;', normalize-space(.), '&lt;/p&gt;')"/>
              </xsl:for-each>
            </caption>
            <name>
              <xsl:value-of select="media-metadata[@name='OriginalFileName']/@value"/>
            </name>
            <title>
              <xsl:value-of select="substring-before(media-metadata[@name='OriginalFileName']/@value, '.')"/>
            </title>
            <credit>
              <xsl:value-of select="media-producer"/>              
            </credit>
          </image>
        </xsl:for-each>
      </images>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>
