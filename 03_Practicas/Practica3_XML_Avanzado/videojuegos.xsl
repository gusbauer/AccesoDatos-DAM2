<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Videojuegos</title>
                <style>
                    body { font-family: Arial, sans-serif; }
                    table { border-collapse: collapse; margin-bottom: 20px; }
                    th, td { border: 1px solid #999; padding: 5px; }
                </style>
            </head>
            <body>
                <h1>Catálogo de videojuegos</h1>

                <xsl:for-each select="desarrolladoras/desarrolladora">
                    <h2>
                        <xsl:value-of select="@nombre"/> -
                        <xsl:value-of select="pais"/>
                    </h2>

                    <table>
                        <tr>
                            <th>Código</th>
                            <th>Título</th>
                            <th>Plataforma</th>
                            <th>Año</th>
                            <th>Precio</th>
                        </tr>

                        <xsl:for-each select="videojuegos/videojuego">
                            <tr>
                                <td><xsl:value-of select="@codigo"/></td>
                                <td><xsl:value-of select="titulo"/></td>
                                <td><xsl:value-of select="plataforma"/></td>
                                <td><xsl:value-of select="anio"/></td>
                                <td><xsl:value-of select="precio"/> €</td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
