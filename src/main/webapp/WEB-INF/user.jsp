<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>
<% List<Message> messages = (ArrayList<Message>) request.getAttribute("messages"); %>

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
    <h1 id="page-title">User Page</h1>

    <b>About Me: <%= isUserLoggedIn %></b>
    <div id="about-me-container">Loading...</div>
    <br/>
    <form id="about-me-form" action="/about" method="POST" class="hidden">
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
          <% System.err.out("Here"); %>
    <%  }
        for(int i = 0; i < messages.size(); i++)
    %>
          <div class="message-div">
            <div class="message-header">
              <%= messages.get(i).user %> - <%= new Date(messages.get(i).timestamp) %>
            </div>
            <div class="message-body">
              <%= messages.get(i).text %>
            </div>
          </div>
    <%=
        }
    %>
    </div>
    <form id="message-form" action="/messages" method="POST" class="hidden">
      Enter a new message:
      <br/>
      <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
      <br/>
      <input type="submit" value="Submit">
    </form>
    <hr/>



  </body>
</html>
