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
  <xsl:output method="xml" indent="yes" cdata-section-elements="content excerpt caption"/>

  <xsl:template match="@* | node()">
    <xsl:variable name="apos">'</xsl:variable>
    <xsl:variable name="quot">"</xsl:variable>
    <wp-api>
      <guid>
        <xsl:value-of select="normalize-space(/article/cId)"/>
      </guid>
      <sequence type="int">
        <xsl:value-of select="0"/>
      </sequence>
      <date>
        <xsl:value-of select="normalize-space(/article/firstPubDateISO8601)"/>
      </date>
      <title>
        <xsl:value-of select="normalize-space(/article/title)"/>
      </title>
      <byline>
        <xsl:value-of select="normalize-space(/article/byline)"/>
      </byline>
      <source>
        <xsl:value-of select="normalize-space(siteInformation/siteName)"/>
      </source>
      <content>
        <xsl:value-of select="normalize-space(/article/body)"/>
      </content>
      <excerpt>
        <xsl:value-of select="normalize-space(/article/blurb)"/>
      </excerpt>
      <images>
        <xsl:for-each select="/article/images/image">
          <image>
            <source>
              <xsl:value-of select="normalize-space(url)"/>
            </source>
            <guid>
              <xsl:value-of select="normalize-space(id)"/>
            </guid>
            <caption>
              <xsl:value-of select="normalize-space(caption)"/>
            </caption>
            <credit>
              <xsl:value-of select="normalize-space(credit)"/>
            </credit>
            <name>
              <xsl:choose>
                <xsl:when test="contains(normalize-space(url), '__')">
                  <xsl:value-of select="substring-after(normalize-space(url), '__')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="normalize-space(id)"/>
                </xsl:otherwise>
              </xsl:choose>
            </name>
            <title>
              <xsl:choose>
                <xsl:when test="contains(normalize-space(url), '__')">
                  <xsl:value-of
                          select="substring-before(substring-after(normalize-space(url), '__'), '.')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="normalize-space(id)"/>
                </xsl:otherwise>
              </xsl:choose>
            </title>
          </image>
        </xsl:for-each>
      </images>
      <metadata>
        <field>
          <key>
            <xsl:text>source_id</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(cId)"/>
          </value>
        </field>
        <field>
          <key>
            <xsl:text>source_name</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(siteInformation/siteName)"/>
          </value>
        </field>

        <field>
          <key>
            <xsl:text>dfm_SlugLine</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(slug)"/>
          </value>
        </field>

        <field>
          <key>
            <xsl:text>source_link</xsl:text>
          </key>
          <value>
            <xsl:value-of select="normalize-space(canonicalUrl)"/>
          </value>
        </field>

        <xsl:variable name="byline">
          <xsl:value-of select="normalize-space(bylineEncoded)"/>
        </xsl:variable>

        <xsl:variable name="authors">
          <tokens>
            <xsl:choose>
              <xsl:when test="$byline = ''">
                <token>
                  <xsl:value-of select="normalize-space(siteInformation/siteName)"/>                  
                </token>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="tokenizeAuthors">
                  <xsl:with-param name="tokens" select="$byline"/>
                </xsl:call-template>                
              </xsl:otherwise>
            </xsl:choose>
          </tokens>
        </xsl:variable>

        <xsl:for-each select="exsl:node-set($authors)/tokens/token">
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

  <xsl:template name="tokenizeAuthors">
    <xsl:param name="tokens"/>
    <xsl:choose>
      <xsl:when test="contains($tokens, string(' By '))">
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-after($tokens, string(' By '))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string(' by '))">
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-after($tokens, string(' by '))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string('   '))">
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-before($tokens, string('   '))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string(', and '))">
        <token>
          <xsl:value-of select="substring-after($tokens, string(', and '))"/>
        </token>
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-before($tokens, string(', and '))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($tokens, string('&amp;#44;'))">
        <xsl:choose>
          <xsl:when test="contains(substring-before($tokens, string('&amp;#44;')), string(' and '))">
            <token>
              <xsl:value-of select="substring-after($tokens, string('&amp;#44;'))"/>
            </token>
            <xsl:call-template name="tokenizeAuthors">
              <xsl:with-param name="tokens" select="substring-before($tokens, string('&amp;#44;'))"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <token>
              <xsl:value-of select="substring-before($tokens, string('&amp;#44;'))"/>
            </token>
            <xsl:call-template name="tokenizeAuthors">
              <xsl:with-param name="tokens" select="substring-after($tokens, string('&amp;#44;'))"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="contains($tokens, string(','))">
        <xsl:choose>
          <xsl:when test="contains(substring-before($tokens, string(',')), string(' and '))">
            <token>
              <xsl:value-of select="substring-after($tokens, string(','))"/>
            </token>
            <xsl:call-template name="tokenizeAuthors">
              <xsl:with-param name="tokens" select="substring-before($tokens, string(','))"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <token>
              <xsl:value-of select="substring-before($tokens, string(','))"/>
            </token>
            <xsl:call-template name="tokenizeAuthors">
              <xsl:with-param name="tokens" select="substring-after($tokens, string(','))"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="contains($tokens, string(' and '))">
        <token>
          <xsl:value-of select="substring-before($tokens, string(' and '))"/>
        </token>
        <xsl:call-template name="tokenizeAuthors">
          <xsl:with-param name="tokens" select="substring-after($tokens, string(' and '))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <token>
          <xsl:value-of select="normalize-space($tokens)"/>
        </token>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>