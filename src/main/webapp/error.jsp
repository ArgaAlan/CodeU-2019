<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>

<!DOCTYPE html>
<html>
  <head>
    <link rel="stylesheet" href="/css/main.css">
  </head>

  <body>

    <center>
    <font color="#4c008e">
      <h1>Page not found</h1>
    </font>
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
    <%
      if (isUserLoggedIn) {
        String username = (String) request.getAttribute("username");
    %>
        <a href="/user-page.html?user=<%= username %>">Your Page</a>
    <% } %>

      </ul>
    </nav>
  </center
  </body>
