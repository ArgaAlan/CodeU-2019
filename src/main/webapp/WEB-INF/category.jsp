<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>

<% String countryCode = (String) request.getAttribute("countryCode"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% String category = (String) request.getAttribute("category"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
<% String uploadUrl = blobstoreService.createUploadUrl("/messages?countryCode="+countryCode + "&category="+category); %>

<!DOCTYPE html>
<html>
  <head>
    <title><%=countryCode%> <%=category%></title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <script src="/js/message-loader.js"></script>
    <script src="/js/location.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    <style>
      #map {
        height: 75px;
      }
    </style>
  </head>
  <body onload="buildUI()">
    <div class="navbar">
      <a href="/">Home</a>
      <a href="/country/<%=countryCode%>"><%=countryName%></a>
    <% if (currentUser != null) { %>
      <a href="/users/<%=currentUser%>">Your Page</a>
    <% } else { %>
      <a href="/login">Login</a>
    <% } %>
    </div>
    <h1 id="page-title"><%= countryName %></h1>
    <h2 id="category"><%= category %></h2>

    <% if(currentUser != null){ %>
    <form id="message-form" action="<%= uploadUrl %>" method="POST" enctype="multipart/form-data">
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
    <br/>
    <input type="hidden" name="lat" value="" id="lat">
    <input type="hidden" name="lng" value="" id="lng">
    <button type="submit" value="Submit"> SUBMIT </button>
    Add an image to your message:
  <input type="file" name="image">
    <br/>
    </form>
    <button onclick="getLocation()">Add your location</button>
    <div id="map"></div>
    <% }  %>
    <div id="message-container">
    <%  if (messages.isEmpty()) { %>
          <p>No posts in this category yet.</p>
          <p><strong> Be the first to post </strong> </p>
    <%  }
        for(int i = 0; i < messages.size(); i++) {
          if(messages.get(i).getCategory().equals(category)) {
    %>
          <div class="message-div">
            <div class="message-header">
              User: <%= messages.get(i).getUser() %> -
              Time: <%= new Date(messages.get(i).getTimestamp()) %>
            </div>
            <div class="message-body">
              <% if(messages.get(i).hasAnImage()){ %>
            	  <%= messages.get(i).getText() + "<br/>" + "<img src=\"" + messages.get(i).getImageUrl() + "\"/>"%>
              <% } else { %>
              <%= messages.get(i).getText() %>
              <% } %>
            </div>
            <% if (currentUser != null && currentUser.equals(messages.get(i).getUser())) { %>
            <form id="delete-form" action="/messages" method="POST">
              <input type="hidden" name="action" value="delete"/>
              <input type="hidden" name="callee" value="/country/<%=countryCode%>/c/<%=category%>"/>
              <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
              <button type="submit" value="Submit">DELETE</button>
            </form>
            <% } %>
          </div>
    <%    }
        }  %>
    </div>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBCK_yt5P_kfz23tAb8tE_fptjRAn5jaB0">
    </script>
  </body>
</html>
