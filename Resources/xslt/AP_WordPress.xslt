<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:exsl="http://exslt.org/common"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:nitf="http://iptc.org/stdnitf/2006-10-18/"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:apcm="http://ap.org/schemas/03/2005/apcm"
                xmlns:apnm="http://ap.org/schemas/03/2005/apnm"
                xmlns:georss="http://www.georss.org/georss"
                xmlns:o="http://w3.org/ns/odrl/2/"
                exclude-result-prefixes="nitf atom apcm apnm georss o">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <wp-api>
      <guid>
        <xsl:value-of select="normalize-space(/atom:entry/apnm:NewsManagement/apnm:ManagementId)"/>
      </guid>
      <sequence type="int">
        <xsl:value-of select="normalize-space(/atom:entry/apnm:NewsManagement/apnm:ManagementSequenceNumber)"/>
      </sequence>
      <date>
        <xsl:variable name="date" select="/atom:entry/atom:content/nitf/head/docdata/date.issue/@norm"/>
        <xsl:value-of select="normalize-space(concat(substring($date, 1,4), '-',substring($date, 5,2), '-',substring($date, 7,2), 'T', substring($date, 10,2), ':',substring($date, 12,2), ':',substring($date, 14,2), '+00:00'))"/>
      </date>
      <title>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/hedline/hl1)"/>
      </title>
      <byline>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/byline/text())"/>
      </byline>
      <post_site_name>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/distributor/text())"/>
      </post_site_name>
      <source>
        <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.head/distributor/text())"/>
      </source>
      <content>
        <xsl:if test="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/p">
          <xsl:for-each select="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/p">
            <xsl:text>&lt;p&gt;</xsl:text>
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:text>&lt;/p&gt;</xsl:text>
          </xsl:for-each>
        </xsl:if>
        <xsl:if test="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/hl2">
          <xsl:for-each select="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']">
            <xsl:text>&lt;p&gt;</xsl:text>
            <xsl:value-of select="normalize-space(hl2)"/>
            <xsl:text>&lt;/p&gt;</xsl:text>
          </xsl:for-each>
        </xsl:if>
      </content>
      <excerpt>
        <xsl:if test="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/p">
          <xsl:value-of select="normalize-space(/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/p[1])"/>
        </xsl:if>
        <xsl:if test="/atom:entry/atom:content/nitf/body/body.content/block[@id='Main']/hl2">
          <xsl:text>HEADLINES ONLY</xsl:text>
        </xsl:if>
      </excerpt>
      <images>
        <xsl:for-each select="/atom:entry/atom:content/nitf/body/body.content/media[@media-type='Photo']">
          <image>
            <xsl:for-each select="media-reference[@name='AP Photo']">
              <mime-type>
                <xsl:value-of select="normalize-space(@mime-type)"/>
              </mime-type>
              <source>
                <xsl:value-of select="normalize-space(@source)"/>
              </source>
            </xsl:for-each>
            <guid>
              <xsl:value-of select="normalize-space(media-metadata[@name='managementId']/@value)"/>
            </guid>
            <caption>
              <xsl:for-each select="media-caption/p">
                <xsl:value-of select="normalize-space(concat('&lt;p&gt;', normalize-space(.), '&lt;/p&gt;'))"/>
              </xsl:for-each>
            </caption>
            <name>
              <xsl:value-of select="normalize-space(media-metadata[@name='OriginalFileName']/@value)"/>
            </name>
            <title>
              <xsl:value-of select="normalize-space(substring-before(media-metadata[@name='OriginalFileName']/@value, '.'))"/>
            </title>
            <credit>
              <xsl:value-of select="normalize-space(media-producer)"/>
            </credit>
          </image>
        </xsl:for-each>
      </images>
      <metadata>
        <field>
          <key>
            <xsl:text>dfm_SlugLine</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(apcm:ContentMetadata/apcm:SlugLine)"/>
          </value>
        </field>
        <xsl:variable name="byline">
          <xsl:value-of select="substring-after(apcm:ContentMetadata/apcm:ByLine, 'By ')"/>
        </xsl:variable>

        <xsl:variable name="authors">
          <tokens>
            <xsl:call-template name="tokenizeAuthors">
              <xsl:with-param name="tokens" select="$byline" />
            </xsl:call-template>
          </tokens>
        </xsl:variable>

        <xsl:for-each select="exsl:node-set($authors)/tokens/token">
          <field>
            <key>
              <xsl:text>dfm_AP_Author</xsl:text>
            </key>
            <value>
              <xsl:value-of select="normalize-space(.)"/>
            </value>
          </field>
        </xsl:for-each>
        
        <xsl:for-each select="apcm:ContentMetadata/apcm:EntityClassification[@Authority='AP Party']">
          <field>
            <key>
              <xsl:text>dfm_AP_Party</xsl:text>
            </key>
            <value>
              <xsl:value-of select="normalize-space(@Value)"/>
            </value>
          </field>
        </xsl:for-each>
        <xsl:for-each select="apcm:ContentMetadata/apcm:EntityClassification[@Authority='AP Geography']">
          <field>
            <key>
              <xsl:text>dfm_AP_Geography</xsl:text>
            </key>
            <value>
              <xsl:value-of select="normalize-space(@Value)"/>
            </value>
          </field>
        </xsl:for-each>
      </metadata>
    </wp-api>
  </xsl:template>

  <xsl:template name="tokenizeAuthors">
    <xsl:param name="tokens" />
    <xsl:param name="delim" select="string(',')" />
    <xsl:choose>
      <xsl:when test="contains($tokens, string(', and '))">
        <token>
          <xsl:value-of select="substring-after($tokens, string(', and '))" />
        </token>
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-before($tokens, string(', and '))" />
          <xsl:with-param name="delim" select="string(', and ')" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string(','))">
        <token>
          <xsl:value-of select="substring-before($tokens, string(','))" />
        </token>
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-after($tokens, string(','))" />
          <xsl:with-param name="delim" select="string(',')" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string(' and '))">
        <token>
          <xsl:value-of select="substring-before($tokens, string(' and '))" />
        </token>
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-after($tokens, string(' and '))" />
          <xsl:with-param name="delim" select="string(' and ')" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <token>
          <xsl:value-of select="normalize-space($tokens)" />
        </token>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
