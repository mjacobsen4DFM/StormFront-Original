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
      <sequence type="int">
        <xsl:value-of select="normalize-space(substring(//item/updateDate,5))"/>
      </sequence>
      <date>
        <xsl:value-of select="normalize-space(//item/dcterms:created)"/>
      </date>
      <title>
        <xsl:value-of select="title"/>
      </title>
      <authors>
        <xsl:for-each select="//item/dc:creator">
          <author>
            <xsl:value-of select="."/>
          </author>
        </xsl:for-each>
      </authors>
      <source>
        <xsl:value-of select="//item/source"/>
        <!--<xsl:variable name="site" select="substring-before(substring-after(//item/source/@url, '//'), '/')" />
        <xsl:variable name="feed" select="concat('http://', $site, '/section?template=RSS')" />
        <xsl:value-of select="document($feed)/rss/channel/dc:publisher"/>-->
      </source>
      <content>
        <xsl:value-of select="content:encoded"/>
      </content>
      <excerpt>
        <xsl:value-of select="//item/description"/>
      </excerpt>

      <images>
        <xsl:for-each select="//item/media:content">
          <xsl:variable name="i" select="position()" />
          <image>
            <mime-type>
              <xsl:value-of select="normalize-space(@type)"/>
            </mime-type>
            <source>
              <xsl:value-of select="normalize-space(//item/source)"/>
            </source>
            <guid>
              <xsl:value-of select="normalize-space(@url)"/>
            </guid>
            <caption>
              <xsl:value-of select="normalize-space(media:description)"/>
            </caption>
            <name>
              <xsl:variable name="name" select="normalize-space(translate(//item/title, translate(//item/title, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_', ''), '_'))"/>
              <xsl:value-of select="concat($name, '_', $i)"/>
            </name>
            <title>
              <xsl:value-of select="normalize-space(translate(//item/title, translate(//item/title, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_ ', ''), '_'))"/>
            </title>
            <credit>
              <xsl:value-of select="normalize-space(//item/source)"/>
            </credit>
          </image>
        </xsl:for-each>
      </images>
    </wp-api>
  </xsl:template>
</xsl:stylesheet>

