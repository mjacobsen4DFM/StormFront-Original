<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="@* | node()">
        <story>
            <id>
                <xsl:value-of select="post_id"/>
            </id>
            <guid>
                <xsl:value-of select="concat(site_url, ':', post_id)"/>
            </guid>
            <siteName>
                <xsl:value-of select="site_name"/>
            </siteName>
            <title>
                <xsl:value-of select="title"/>
            </title>
            <viewURI>
                <xsl:value-of select="source_link"/>
            </viewURI>
            <fileName>
                <xsl:value-of select="remote_file_name"/>
            </fileName>
            <subjects>
                <subject>
                    <xsl:text>News</xsl:text>
                </subject>
                <subject>
                    <xsl:text>test</xsl:text>
                </subject>
                <subject>
                    <xsl:text>here</xsl:text>
                </subject>
            </subjects>
            <images>
                <xsl:for-each select="//images">
                    <image>
                        <source>
                            <xsl:value-of select="source"/>
                        </source>
                        <caption>
                            <xsl:value-of select="caption"/>
                        </caption>
                        <credit>
                            <xsl:value-of select="credit"/>
                        </credit>
                    </image>
                </xsl:for-each>
            </images>
        </story>
    </xsl:template>
</xsl:stylesheet>

