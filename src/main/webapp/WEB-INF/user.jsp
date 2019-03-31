<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="java.io.IOException" %>
<%@ page import="javax.servlet.annotation.WebServlet" %>
<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String user = (String) request.getAttribute("user"); %>
<% boolean isViewingSelf = (boolean) request.getAttribute("isViewingSelf"); %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
<% String uploadUrl = blobstoreService.createUploadUrl("/messages"); %>
<% Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request); %>


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

    <% if(isUserLoggedIn && isViewingSelf){ %>
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

    <% if(isUserLoggedIn){ %>
    <form id="message-form" action="/messages?recipient=<%= user %>" method="POST" enctype="multipart/form-data" class>
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
    <br/>
    Add an image to your message:
    <input type="file" name="image">
    <% const messageForm = document.getElementById('message-form'); %>
    <% messageForm.action = imageUploadUrl; %>
    <% messageForm.classList.remove('hidden'); %>
    <br/>
    <input type="submit" value="Submit">
    </form>
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
              User: <%= messages.get(i).getUser() %> -
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
