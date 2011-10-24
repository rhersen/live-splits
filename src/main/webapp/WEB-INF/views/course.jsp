<%@page contentType="text/html;charset=UTF-8" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-type" content="text/html;charset=ISO-8859-1"/>
    <meta name="HandheldFriendly" content="true"/>
    <title>Sträcktider</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/main.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/javascript/canvas.js"></script>
    <meta name="layout" content="main"/>
</head>
<body onload="init(${id});">
<div class="fullscreen">
    <canvas>
    </canvas>
</div>
</body>
</html>
