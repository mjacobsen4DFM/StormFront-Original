<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:atom="http://www.w3.org/2005/Atom"
>
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="@* | node()">
      <wp-api>
        <guid>
          <xsl:value-of select="guid"/>
        </guid>
        <!-- <date>
          <xsl:value-of select="pubDate"/>
        </date> -->
        <title>
          <xsl:value-of select="title"/></title>
        <content>
          <xsl:value-of select="description"/></content>
        <excerpt>
          <xsl:value-of select="substring-before(description, 25)"/></excerpt>
      </wp-api>
    </xsl:template>
</xsl:stylesheet>

