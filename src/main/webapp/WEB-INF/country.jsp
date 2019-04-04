<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% String countryCode = (String) request.getAttribute("code"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
<% String uploadUrl = blobstoreService.createUploadUrl("/messages?countryCode="+countryCode); %>

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
    <h1 id="page-title"><%= countryName %></h1>

    <% if(isUserLoggedIn){ %>
    <form id="message-form" action="<%= uploadUrl %>" method="POST" enctype="multipart/form-data">
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
    <br/>
    Add an image to your message:
  <input type="file" name="image">
  <br/>
    <input type="submit" value="Submit">
    </form>
    <% }  %>

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
          </div>
    <% }  %>
    </div>

  </body>
</html>