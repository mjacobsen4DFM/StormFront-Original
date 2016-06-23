<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="@* | node()">
    <article>
      <cId>
        <xsl:value-of select="cId"/>
      </cId>
      <delete>
        <xsl:value-of select="delete"/>
      </delete>
      <blurb>
        <xsl:value-of select="blurb"/>
      </blurb>
      <body>
        <xsl:choose>
          <xsl:when test="body">
            <xsl:value-of select="body"/>
          </xsl:when>
          <xsl:when test="bodyEncoded">
            <xsl:value-of select="bodyEncoded"/>
          </xsl:when>
        </xsl:choose>
      </body>
      <byline>
        <xsl:choose>
          <xsl:when test="byline">
            <xsl:value-of select="byline"/>
          </xsl:when>
          <xsl:when test="bylineEncoded">
            <xsl:value-of select="bylineEncoded"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="siteInformation/siteName"/>
          </xsl:otherwise>
        </xsl:choose>
      </byline>
      <canonicalUrl>
        <xsl:value-of select="canonicalUrl"/>
      </canonicalUrl>
      <!--<contentGroups>
        <xsl:for-each select="contentGroups/contentGroup">
          <contentGroup>
            <xsl:choose>
              <xsl:when test="./id">
                <id>
                  <xsl:value-of select="./id"/>
                </id>
                <name>
                  <xsl:value-of select="./name"/>
                </name>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </contentGroup>
        </xsl:for-each>
      </contentGroups>-->
      <endDateISO8601>
        <xsl:choose>
          <xsl:when test="endDateISO8601">
            <xsl:choose>
              <xsl:when test="endDateISO8601 != ''">
                <xsl:value-of select="endDateISO8601"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>2025-01-09T13:43:50-05:00</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>2025-01-09T13:43:50-05:00</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="endDateISO8601"/>
      </endDateISO8601>
      <headline>
        <xsl:choose>
          <xsl:when test="headline">
            <xsl:value-of select="headline"/>
          </xsl:when>
          <xsl:when test="headlineEncoded">
            <xsl:value-of select="headlineEncoded"/>
          </xsl:when>
        </xsl:choose>
      </headline>
      <xsl:if test="images/image">
        <images>
          <mediaCount>
            <xsl:value-of select="mediaCount"/>
          </mediaCount>
          <xsl:for-each select="images/image">
            <image>
              <id>
                <xsl:value-of select="id"/>
              </id>
              <url>
                <xsl:value-of select="url"/>
              </url>
              <caption>
                <xsl:value-of select="caption"/>
              </caption>
              <credit>
                <xsl:value-of select="credit"/>
              </credit>
              <filesize>
                <xsl:value-of select="filesize"/>
              </filesize>
              <height>
                <xsl:value-of select="height"/>
              </height>
              <width>
                <xsl:value-of select="width"/>
              </width>
            </image>
          </xsl:for-each>
        </images>
      </xsl:if>
      <keyword>
        <xsl:value-of select="keyword"/>
      </keyword>
      <firstPubDateISO8601>
        <xsl:value-of select="firstPubDateISO8601"/>
      </firstPubDateISO8601>
      <launchDateISO8601>
        <xsl:value-of select="launchDateISO8601"/>
      </launchDateISO8601>
      <startDate>
        <xsl:value-of select="startDate"/>
      </startDate>
      <title>
        <xsl:value-of select="title"/>
      </title>
      <updateDateISO8601>
        <xsl:value-of select="updateDateISO8601"/>
      </updateDateISO8601>
    </article>
  </xsl:template>
</xsl:stylesheet>
