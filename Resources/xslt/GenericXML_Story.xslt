<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <story>
      <guid>
        <xsl:value-of select="cId"/>
      </guid>
      <title>
        <xsl:value-of select="headline"/>
      </title>
      <viewURI>
        <xsl:value-of select="canonicalUrl"/>
      </viewURI>
      <subjects>
        <subject>
          <xsl:value-of select="taxonomy/level1"/>
        </subject>
      </subjects>
    </story>
  </xsl:template>
</xsl:stylesheet>

