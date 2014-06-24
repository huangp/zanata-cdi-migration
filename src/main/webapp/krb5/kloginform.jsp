<%
    String queryStr = request.getQueryString(); // keep the original request's
// query string
    if (queryStr != null && !queryStr.isEmpty()) {
        queryStr = "?" + queryStr;
    }
    else {
        queryStr = "";
    }
%>
<html>
<head>
    <meta http-equiv="refresh" content="0; url=login.xhtml<%=queryStr%>">
</head>
</html>