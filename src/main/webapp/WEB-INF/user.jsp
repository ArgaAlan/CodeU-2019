<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String user = (String) request.getAttribute("user"); %>

<!DOCTYPE html>
<html>
  <head>
    <title>User Page</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <script src="/js/user-page-loader.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
  </head>
  <body onload="buildUI()">
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
      </ul>
    </nav>
    <h1 id="page-title"><%= user %></h1>
    <b>About Me: </b>
    <div id="about-me-container"><%= (String) request.getAttribute("aboutMe") %></div>
    <br/>

    <% if(isUserLoggedIn){ %>
    <form id="about-me-form" action="/about" method="POST" class>
    <% } else { %>
    <form id="about-me-form" action="/about" method="POST" class="hidden">
    <% } %>
      <br/>
      Update AboutMe:
      <br/>
      <textarea name="about-me" placeholder="About me" id="about-me-input"></textarea>
      <br/>
      <input type="submit" value="Submit">
    </form>
    <hr/>

    <div id="message-container">
    <%  if (true) { %>
          <p>This user has no posts yet.</p>
    <%  } else { %>
          <p>User has messages</p>;
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
          </div>
    <%
        }
    %>
    </div>
    <% if(isUserLoggedIn){ %>
    <form id="message-form" action="/messages?recipient=<%= request.getAttribute("user") %>" method="POST" class>
    <% } else { %>
    <form id="message-form" action="/messages?recipient=" method="POST" class="hidden">
    <% } %>
      Enter a new message:
      <br/>
      <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
      <br/>
      <input type="submit" value="Submit">
    </form>
    <hr/>


  </body>
</html>
