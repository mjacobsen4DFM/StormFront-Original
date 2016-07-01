<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom" 
                xmlns:media="http://search.yahoo.com/mrss/" 
                xmlns:dc="http://purl.org/dc/elements/1.1/" 
                xmlns:content="http://purl.org/rss/1.0/modules/content/" 
                xmlns:dcterms="http://purl.org/dc/terms/"
>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <wp-api>
      <guid>
        <xsl:value-of select="//item/guid"/>
      </guid>
      <sequence type="long">
        <xsl:value-of select="normalize-space(//item/updateDate)"/>
      </sequence>
      <date>
        <xsl:value-of select="normalize-space(//item/dcterms:created)"/>
      </date>
      <title>
        <xsl:value-of select="title"/>
      </title>
      <byline>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/byline/text())"/>
      </byline>
      <source>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/distributor/text())"/>
      </source>
      <content>
        <xsl:value-of select="content:encoded"/>
      </content>
      <excerpt>
        <xsl:value-of select="//item/description"/>
      </excerpt>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>

