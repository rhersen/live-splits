<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-type" content="text/html;charset=ISO-8859-1"/>
    <meta name="HandheldFriendly" content="true"/>
    <title>Resultat</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css"/>
    <meta name="layout" content="main"/>
</head>
<body>

<c:forEach items="${classes}" var="c">
    <h2>${c.name}</h2>
    <table>
        <c:forEach items="${c.list}" var="p">
            <tr>
                <td>${p.id}</td>
                <td>
                    <a href="c?id=${p.id}">${p.name}</a>
                </td>
                <td class="time">${p.time}</td>
            </tr>
        </c:forEach>
    </table>
</c:forEach>

</body>
</html>
