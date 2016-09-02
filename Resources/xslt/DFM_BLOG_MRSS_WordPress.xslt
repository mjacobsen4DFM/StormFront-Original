<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:content="http://purl.org/rss/1.0/modules/content/"
        xmlns:wfw="http://wellformedweb.org/CommentAPI/"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:atom="http://www.w3.org/2005/Atom"
        xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
        xmlns:slash="http://purl.org/rss/1.0/modules/slash/"
        xmlns:media="http://search.yahoo.com/mrss/"
        xmlns:georss="http://www.georss.org/georss"
        xmlns:dcterms="http://purl.org/dc/terms/"
>
  <!-- <xsl:include href="http://delivery.digitalfirstmedia.com/WebServices/ConversionPublisher/BaseFiles/MainFunctions.xslt"/> -->
  <xsl:include href="C:\Users\Mick\Documents\Cloud\Google Drive\mjacobsen@denverpost.com\Dev\Projects\TRFEEDS\Webservices\ConversionPublisher\Basefiles\MainFunctions.xslt"/>
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <wp-api>
      <guid>
        <xsl:call-template name="buildGUID">
          <xsl:with-param name="path" select="//item/link"/>
        </xsl:call-template>
      </guid>
      <date>
        <xsl:call-template name="formatDateLong">
          <xsl:with-param name="date" select="normalize-space(//item/pubDate)" />
        </xsl:call-template>
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
        <xsl:value-of select="//item/dc:creator"/>
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
              <xsl:value-of select="normalize-space(@url)"/>
            </source>
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
      <metadata>
        <field>
          <key>
            <xsl:text>source_id</xsl:text>
          </key>
          <value>
            <xsl:call-template name="extractSEO">
              <xsl:with-param name="path" select="//item/guid"/>
            </xsl:call-template>
          </value>
        </field>

        <field>
          <key>
            <xsl:text>source_name</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(dc:creator)"/>
          </value>
        </field>

        <field>
          <key>
            <xsl:text>source_link</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(//item/link)"/>
          </value>
        </field>

        <field>
          <key>
            <xsl:text>dfm_SlugLine</xsl:text>
          </key>
          <value>
            <xsl:call-template name="extractSEO">
              <xsl:with-param name="path" select="//item/link"/>
            </xsl:call-template>
          </value>
        </field>
        
        <xsl:for-each select="//item/dc:creator">
          <field>
            <key>
              <xsl:text>source_author_name</xsl:text>
            </key>
            <value>
              <xsl:value-of select="normalize-space(.)"/>
            </value>
          </field>
        </xsl:for-each>
      </metadata>
    </wp-api>
  </xsl:template>

  <xsl:template name="extractSEO">
    <xsl:param name="path"/>
    <xsl:variable name="after-http">
      <xsl:value-of select="substring-after($path, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-slash">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-domain">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-yyyy">
      <xsl:value-of select="substring-after($after-domain, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-mm">
      <xsl:value-of select="substring-after($after-yyyy, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-dd">
      <xsl:value-of select="substring-after($after-mm, '/')"/>
    </xsl:variable>    
    <xsl:variable name="seo-id">
      <xsl:value-of select="substring-after($after-dd, '/')"/>
    </xsl:variable>
    <xsl:variable name="seo">
      <xsl:value-of select="substring-before($seo-id, '/')"/>
    </xsl:variable>
    <xsl:value-of select="$seo"/>
  </xsl:template>




  <xsl:template name="buildGUID">
    <xsl:param name="path"/>
    <xsl:variable name="after-http">
      <xsl:value-of select="substring-after($path, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-slash">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-domain">
      <xsl:value-of select="substring-after($after-http, '/')"/>
    </xsl:variable>
    <xsl:variable name="domain">
      <xsl:value-of select="substring-before($after-domain, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-yyyy">
      <xsl:value-of select="substring-after($after-domain, '/')"/>
    </xsl:variable>
    <xsl:variable name="yyyy">
      <xsl:value-of select="substring-before($after-yyyy, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-mm">
      <xsl:value-of select="substring-after($after-yyyy, '/')"/>
    </xsl:variable>
    <xsl:variable name="mm">
      <xsl:value-of select="substring-before($after-mm, '/')"/>
    </xsl:variable>
    <xsl:variable name="after-dd">
      <xsl:value-of select="substring-after($after-mm, '/')"/>
    </xsl:variable>
    <xsl:variable name="dd">
      <xsl:value-of select="substring-before($after-dd, '/')"/>
    </xsl:variable>
    <xsl:variable name="seo-id">
      <xsl:value-of select="substring-after($after-dd, '/')"/>
    </xsl:variable>
    <xsl:variable name="seo">
      <xsl:value-of select="substring-before($seo-id, '/')"/>
    </xsl:variable>
    <xsl:variable name="id">
      <xsl:value-of select="substring-before(substring-after($seo-id, '/'), '/')"/>
    </xsl:variable>
    <xsl:value-of select="concat($domain, ':', $yyyy, ':', $dd, ':', $mm, ':', $seo, ':', $id) "/>
  </xsl:template>
</xsl:stylesheet>

