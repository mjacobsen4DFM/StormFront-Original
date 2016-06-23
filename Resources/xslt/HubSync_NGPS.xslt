<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
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
      <edit/>
      <source>
        <xsl:value-of select="source"/>
      </source>
      <sec/>
      <page/>
      
      <keyword>
        <xsl:variable name="terms">
          <xsl:for-each select="//terms">
            <xsl:value-of select="name"/>
            <xsl:text>, </xsl:text>
          </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="trimTerms" select="substring($terms, 0, string-length($terms) - 1)"/>

        <xsl:variable name="keywords">
          <xsl:choose>
            <xsl:when test="$trimTerms=''">
              <xsl:text>News</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$trimTerms"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        
        <xsl:value-of select="$keywords"/>
      </keyword>
      <SEODescription/>
      <SEOKeywords/>
      <note/>
      <version/>
      <overline/>
      <headline>
        <xsl:value-of select="title"/>
      </headline>
      <subhead/>
      <summary>
        <xsl:value-of select="excerpt"/>
      </summary>
      <byline>
        <xsl:value-of select="source_author_name"/>
      </byline>
      <body>
        <xsl:value-of select="content"/>
      </body>
      <footer/>
      <category>
        <xsl:value-of select="category"/>
      </category>
      <thirdPartyURL>
        <xsl:value-of select="source_link"/>
      </thirdPartyURL>
    </doc>
  </xsl:template>
</xsl:stylesheet>