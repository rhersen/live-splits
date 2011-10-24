<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="HandheldFriendly" content="true"/>
    <title>Resultat</title>
    <link rel="stylesheet" href="${rc.contextPath}/resources/main.css"/>
    <meta name="layout" content="main"/>
</head>
<body>

<#list classes as c>
    <h2>${c.name}</h2>
    <table>
        <#list c.list as p>
            <tr>
                <td>${p.id}</td>
                <td>
                    <a href="c?id=${p.id}">${p.name}</a>
                </td>
                <td class="time">${p.time}</td>
            </tr>
        </#list>
    </table>
</#list>

</body>
</html>
