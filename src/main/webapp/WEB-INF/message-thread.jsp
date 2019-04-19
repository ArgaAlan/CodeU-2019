<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>


<% Message parent = (Message) request.getAttribute("parent"); %>
<% List<Reply> replies = (List<Reply>) request.getAttribute("replies"); %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isLoggedIn"); %>


<!DOCTYPE html>
<html>
  <head>
    <title><%=countryCode%> <%=category%></title>
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

    <% if(isUserLoggedIn){ %>
      <form id="reply-form" action="reply/<%= parent.getId() %>" method="POST">
        Add a reply:
        <br/>
        <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
        <br/>
        <button type="submit" value="Submit"> SUBMIT </button>
    <% }  %>

    <div id="message-container">
      <div class="message-div">
        <div class="message-header">
          User: <%= parent.getUser() %> --
          Time: <%= new Date(parent.getTimestamp()) %> --
          Country: <%= parent.getCountry() %> --
          Category: <%= new Date(parent.getCategory()) %>
        </div>
        <div class="message-body">
          <%= parent.getText() %>
        </div>
      </div>
      <% for(int i = 0; i < replies.size(); i++) { %>
          <div class="reply-div">
            <div class="reply-header">
              User: <%= replies.get(i).getUser() %> --
              Time: <%= new Date(replies.get(i).getTimestamp()) %>
            </div>
            <div class="reply-body">
              <%= messages.get(i).getText() %>
            </div>
          </div>
    <%    }
        }  %>
    </div>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBCK_yt5P_kfz23tAb8tE_fptjRAn5jaB0">
    </script>
  </body>
</html>

