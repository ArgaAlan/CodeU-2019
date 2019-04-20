<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.codeu.data.Reply" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>


<% Message parent = (Message) request.getAttribute("parent"); %>
<% List<Reply> replies = (List<Reply>) request.getAttribute("replies"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>


<!DOCTYPE html>
<html>
  <head>
    <title>Message Thread</title>
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
    </div>
    <h1 id="page-title"> Message Thread </h1>
    <% if(currentUser != null){ %>
    <% String parentID = parent.getId().toString(); %>
      <form id="reply-form" action="/reply" method="POST">
        Add a reply:
        <br/>
        <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
        <input type="hidden" name="parentID" value="<%=parentID%>">
        <br/>
        <button type="submit" value="Submit"> SUBMIT </button>
      </form>
    <% }  %>

    <div id="message-container">
      <div class="message-div">
        <div class="message-header">
          User: <a href="/users/<%= parent.getUser() %>"><%= parent.getUser() %></a> --
          Time: <%= new Date(parent.getTimestamp()) %> --
          Country: <a href="/country/<%= parent.getCountry() %>"><%= parent.getCountry() %></a> --
          Category: <%= parent.getCategory() %>
        </div>
        <div class="message-body">
        <% if(parent.hasAnImage()){ %>
          <%= parent.getText() + "<br/>" + "<img src=\"" + parent.getImageUrl() + "\"/>"%>
        <% } else { %>
          <%= parent.getText() %>
        <% } %>
        </div>
      </div>
      <% for(int i = 0; i < replies.size(); i++) { %>
          <div class="message-div">
            <div class="message-header">
              User: <%= replies.get(i).getUser() %> --
              Time: <%= new Date(replies.get(i).getTimestamp()) %>
            </div>
            <div class="reply-body">
              <%= replies.get(i).getText() %>
            </div>
          </div>
      <% } %>
    </div>
    </script>
  </body>
</html>

