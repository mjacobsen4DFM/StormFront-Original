<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl"
>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <doc>
      <pub/>
      <pubdate>
        <xsl:value-of select="date"/>
      </pubdate>
      <slug>
        <xsl:value-of select="slug"/>
      </slug>
      <edit>Needed??</edit>
      <source>
        <xsl:value-of select="source"/>
      </source>
      <sec>???</sec>
      <page>???</page>
      <keyword>NEED THIS</keyword>
      <SEODescription/>
      <SEOKeywords/>
      <note/>
      <version/>
      <overline/>
      <headline>
        <xsl:value-of select="title"/>
      </headline>
      <subhead/>
      <summary/>
      <byline>
        <xsl:value-of select="author"/>
      </byline>
      <body>
      </body>
      <footer/>
      <category>need</category>
    </doc>
  </xsl:template>
</xsl:stylesheet>
