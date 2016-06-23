<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:nitf="http://iptc.org/stdnitf/2006-10-18/"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:apcm="http://ap.org/schemas/03/2005/apcm"
                xmlns:apnm="http://ap.org/schemas/03/2005/apnm"
                xmlns:georss="http://www.georss.org/georss"
                xmlns:o="http://w3.org/ns/odrl/2/"
                exclude-result-prefixes="nitf atom apcm apnm georss o" >
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <story>
      <guid>
        <xsl:value-of select="normalize-space(/article/article_uid)"/>
      </guid>
      <title>
        <xsl:value-of select="/article/heading"/>
      </title>
      <updateCheck>
        <xsl:value-of select="normalize-space(/atom:entry/apnm:NewsManagement/apnm:ManagementSequenceNumber)"/>
      </updateCheck>
      <viewURI>
        <xsl:value-of select="/atom:entry/atom:link[@title='AP Article']/@href"/>
      </viewURI>
      <subjects>
        <xsl:for-each select="//atom:entry/apcm:ContentMetadata/apcm:SubjectClassification[@Authority='AP Subject']">
          <subject>
            <xsl:value-of select="@Value"/>
          </subject>
        </xsl:for-each>
      </subjects>
  </story>
  </xsl:template>
</xsl:stylesheet>
