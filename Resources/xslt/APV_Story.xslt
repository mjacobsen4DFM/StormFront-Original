<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:msxsl="urn:schemas-microsoft-com:xslt"
                xmlns:nitf="http://iptc.org/std/NITF/2006-10-18/"
                exclude-result-prefixes="msxsl nitf">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <story>
      <guid>
        <xsl:value-of select="/nitf/head/docdata/date.issue/@norm"/>
        <xsl:variable name="hed" select="translate(/nitf/body/body.head/hedline/hl1, ' -!@#$%^&amp;*(){}:&lt;>[]\;,./=', '')"/>
        <xsl:variable name="hedc" select="translate($hed, $quot, '')"/>
        <xsl:value-of select="translate($hedc, $apos, '')"/>
      </guid>
      <title>
        <xsl:value-of select="/nitf/body/body.head/hedline/hl1"/>
      </title>
    </story>
  </xsl:template>
</xsl:stylesheet>
