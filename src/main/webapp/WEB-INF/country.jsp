<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>

<% String countryCode = (String) request.getAttribute("code"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>

<!DOCTYPE html>
<html>
  <head>
    <title>User Page</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <script src="/js/message-loader.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
  </head>
  <body onload="buildUI()">
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
        <% if (currentUser != null) { %>
          <li><a href="/users/<%=currentUser%>">Your Page</a></li>
        <% } else { %>
          <li><a href="/login">Login</a></li>
        <% } %>
        <li><a href="/country/<%=countryCode%>/c/Food">Food</a></li>
        <li><a href="/country/<%=countryCode%>/c/Attractions">Attractions</a></li>
        <li><a href="/country/<%=countryCode%>/c/Culture">Culture</a></li>
      </ul>
    </nav>
    <h1 id="page-title"><%= countryName %></h1>

    <div id="message-container">
    <%  if (messages.isEmpty()) { %>
          <p>No posts about this country yet.</p>
          <p><strong> Be the first to post </strong> </p>
    <%  }

        for(int i = 0; i < messages.size(); i++) {
    %>
          <div class="message-div">
            <div class="message-header">
              User: <%= messages.get(i).getUser() %> -
              Time: <%= new Date(messages.get(i).getTimestamp()) %> -
              Sentiment Score: <%= messages.get(i).getSentimentScore() %>
            </div>
            <div class="message-body">
              <%= messages.get(i).getText() %>
            </div>
            <% if (currentUser != null && currentUser.equals(messages.get(i).getUser())) { %>
            <form id="delete-form" action="/messages" method="POST">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
            <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
            <button type="submit" value="Submit">Delete</button>
          </form>
          <% } %>
            </div>
      <% }  %>
      </div>

  </body>
</html>
