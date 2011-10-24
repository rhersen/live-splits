<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="HandheldFriendly" content="true"/>
    <title>Sträcktider</title>
    <link rel="stylesheet" href="${rc.contextPath}/resources/main.css"/>
    <meta name="layout" content="main"/>
</head>
<body>

<#list classes as c>
    <h2>${c.name}</h2>
    <table>
        <#list c.list as p>
            <tr>
                <td class="split">${p.name}</td>
                <#list p.splits as s>
                    <td class="split">${s.time}</td>
                </#list>
                <td class="split">${p.time}</td>
            </tr>
        </#list>
    </table>
</#list>

</body>
</html>
