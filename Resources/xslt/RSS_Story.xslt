<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:atom="http://www.w3.org/2005/Atom"
>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <wp-api>
      <guid>
        <xsl:variable name="guid" select="//item/guid"/>
        <xsl:choose>
          <xsl:when test="contains(guid, 'http://')">
            <xsl:value-of select="translate(substring-after($guid, 'http://'), '/', ':')"/>
          </xsl:when>
          <xsl:when test="contains(guid, 'https://')">
            <xsl:value-of select="translate(substring-after($guid, 'https://'), '/', ':')"/>
          </xsl:when>
        </xsl:choose>
      </guid>
      <title>
        <xsl:value-of select="title"/>
      </title>
      <updateCheck>
        <xsl:value-of select="normalize-space(//item/updateDate)"/>
      </updateCheck>
      <viewURI>
        <xsl:value-of select="//item/source/@url"/>
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
</xsl:stylesheet>

