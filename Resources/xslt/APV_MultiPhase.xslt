<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 xmlns:atom="http://www.w3.org/2005/Atom" xmlns:apcm="http://ap.org/schemas/03/2005/apcm" xmlns:apnm="http://ap.org/schemas/03/2005/apnm" xmlns:georss="http://www.georss.org/georss" xmlns:o="http://w3.org/ns/odrl/2/"
                exclude-result-prefixes="atom apcm apnm georss o"
>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <wp-api>
      <guid>
        <xsl:value-of select="//atom:entry/atom:id"/>
      </guid>
      <title>
        <xsl:value-of select="//atom:entry/atom:content"/>
      </title>
      <updateCheck>
        <xsl:value-of select="//atom:entry/atom:updated"/>
      </updateCheck>
      <subjects>
        <xsl:for-each select="//atom:entry/apcm:ContentMetadata/apcm:SubjectClassification[@Authority='AP Subject']">
          <subject>
            <xsl:value-of select="@Value"/>
          </subject>
        </xsl:for-each>
      </subjects>
      <storyLinks>
        <storyLink>
          <xsl:value-of select="//atom:entry/atom:link[@rel='enclosure']/@href"/>
        </storyLink>
      </storyLinks>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>
