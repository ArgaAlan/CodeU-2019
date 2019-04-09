<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>

<% String countryCode = (String) request.getAttribute("countryCode"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% Set<String> categories = (HashSet) request.getAttribute("categories"); %>

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
    <div class="navbar">
      <a href="/">Home</a>
    <% if (currentUser != null) { %>
      <a href="/users/<%=currentUser%>">Your Page</a>
    <% } else { %>
      <a href="/login">Login</a>
    <% } %>
      <div class="dropdown">
        <button class="dropbtn">Categories 
          <i class="fa fa-caret-down"></i>
        </button>
        <div class="dropdown-content">
    <%  Iterator iter = categories.iterator();
        while (iter.hasNext()) {    
          String category = (String) iter.next();    %>
          <a href="/country/<%= countryCode %>/c/<%= category %>"><%= category %></a>
    <%  }   %>
        </div>
      </div> 
    </div>
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
