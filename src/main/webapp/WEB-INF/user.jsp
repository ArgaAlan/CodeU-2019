<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String user = (String) request.getAttribute("user"); %>
<% boolean isViewingSelf = (boolean) request.getAttribute("isViewingSelf"); %>

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
      </ul>
    </nav>
    <h1 id="page-title"><%= user %></h1>
    <b>About Me: </b>
    <div id="about-me-container"><%= (String) request.getAttribute("aboutMe") %></div>
    <br/>

    <% if(isViewingSelf){ %>
    <form id="about-me-form" action="/about" method="POST" class>
      <br/>
      Update AboutMe:
      <br/>
      <textarea name="about-me" placeholder="About me" id="about-me-input"></textarea>
      <br/>
      <input type="submit" value="Submit">
    </form>
    <hr/>
    <% }  %>

    <div id="message-container">
    <%  if (messages.isEmpty()) { %>
          <p>This user has no posts yet.</p>
    <%  } else { %>
          <p>User has messages:</p>
    <%  }
        for(int i = 0; i < messages.size(); i++) {
    %>
          <div class="message-div">
            <div class="message-header">
              Country: <%= messages.get(i).getCountry() %> -
              Time: <%= new Date(messages.get(i).getTimestamp()) %> -
              Sentiment Score: <%= messages.get(i).getSentimentScore() %>
            </div>
            <div class="message-body">
              <%= messages.get(i).getText() %>
            </div>
          </div>
    <% }  %>
    </div>

  </body>
</html>
