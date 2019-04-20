<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String user = (String) request.getAttribute("user"); %>
<% boolean isLoggedIn = (boolean) request.getAttribute("isLoggedIn"); %>
<% boolean isViewingSelf = (boolean) request.getAttribute("isViewingSelf"); %>

<!DOCTYPE html>
<html>
  <head>
    <title><%=user%></title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <script src="/js/message-loader.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
  </head>
  <body onload="buildUI()">
    <div class="navbar">
      <a href="/">Home</a>
    <% if (isLoggedIn) { %>
      <a href="/logout">Logout</a>
    <% } else { %>
      <a href="/login">Login</a>
    <% } %>
    </div>
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
      <!--<input type="submit" value="Submit">-->
      <button type="submit" value="Submit"> SUBMIT </button>
    </form>
    <hr/>
    <% }  %>

    <div id="message-container">
    <%  if (messages.isEmpty()) { %>
          <p>This user has no posts yet.</p>
    <%  } else { %>
          <p>User has messages:</p>
    <%  }
        for (int i = 0; i < messages.size(); i++) {
    %>
          <div class="message-div">
            <div class="message-header">
              Country: <%= messages.get(i).getCountry() %> -
              Time: <%= new Date(messages.get(i).getTimestamp()) %>
            </div>
            <div class="message-body">
              <%= messages.get(i).getText() %>
            </div>
        <% if (isViewingSelf) { %>
          <form id="edit-form" action="/messages" method="GET">
            <input type="hidden" name="action" value="getEditable"/>
            <input type="hidden" name="country" value="<%=messages.get(i).getCountry()%>"/>
            <input type="hidden" name="category" value="<%=messages.get(i).getCategory()%>"/>
            <input type="hidden" name="lat" value="<%=messages.get(i).getLat()%>"/>
            <input type="hidden" name="lng" value="<%=messages.get(i).getLng()%>"/>
            <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
            <button type="submit">EDIT</button>
          </form>
          <form id="delete-form" action="/messages" method="POST">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="callee" value="/users/<%=user%>"/>
            <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
            <button type="submit" value="Submit">Delete</button>
          </form>
        <% } %>
          </div>
    <% }  %>
    </div>

  </body>
</html>
