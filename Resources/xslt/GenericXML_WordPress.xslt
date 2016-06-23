<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="@* | node()">
      <wp-api>
        <guid>
          <xsl:value-of select="cId"/>
        </guid>
        <date>
          <xsl:value-of select="firstPubDateISO8601"/>
        </date>
        <title>
          <xsl:value-of select="headline"/></title>
        <content>
          <xsl:value-of select="body"/></content>
        <excerpt>
          <xsl:value-of select="abstract"/></excerpt>
      </wp-api>
    </xsl:template>
</xsl:stylesheet>

