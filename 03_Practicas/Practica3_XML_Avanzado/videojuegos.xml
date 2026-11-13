<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output
            method="html"
            encoding="UTF-8"
            indent="yes"/>

    <xsl:template match="/">

        <html>

        <head>

            <meta charset="UTF-8"/>

            <title>
                Catálogo de videojuegos
            </title>

            <style>

                body {
                    font-family: Arial, sans-serif;
                    margin: 30px;
                    background-color: #f4f4f4;
                }

                h1 {
                    text-align: center;
                }

                h2 {
                    margin-top: 35px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    background-color: white;
                }

                th, td {
                    border: 1px solid #cccccc;
                    padding: 8px;
                    text-align: left;
                }

                th {
                    background-color: #dddddd;
                }

            </style>

        </head>

        <body>

            <h1>
                Catálogo de videojuegos
            </h1>

            <xsl:for-each
                    select="desarrolladoras/desarrolladora">

                <h2>

                    <xsl:value-of
                            select="@nombre"/>

                    -

                    <xsl:value-of
                            select="pais"/>

                </h2>

                <table>

                    <tr>
                        <th>Código</th>
                        <th>Título</th>
                        <th>Plataforma</th>
                        <th>Año</th>
                        <th>Precio</th>
                        <th>Multijugador</th>
                        <th>Valoración</th>
                        <th>Género</th>
                        <th>PEGI</th>
                        <th>Ventas</th>
                    </tr>

                    <xsl:for-each
                            select="videojuegos/videojuego">

                        <tr>

                            <td>
                                <xsl:value-of
                                        select="@codigo"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="titulo"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="plataforma"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="anio"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="precio"/>
                                EUR
                            </td>

                            <td>
                                <xsl:value-of
                                        select="multijugador"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="valoracion"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="genero"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="pegi"/>
                            </td>

                            <td>
                                <xsl:value-of
                                        select="ventas"/>
                            </td>

                        </tr>

                    </xsl:for-each>

                </table>

            </xsl:for-each>

        </body>

        </html>

    </xsl:template>

</xsl:stylesheet>