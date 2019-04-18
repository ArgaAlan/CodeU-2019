<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>

<% String countryCode = (String) request.getAttribute("countryCode"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% String category = (String) request.getAttribute("category"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% String editText = (String) request.getAttribute("editText"); %>

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
      <% if (editText == null) { %>
        Enter a new message:
          <form id="message-form" action="/messages?countryCode=<%= countryCode %>&category=<%= category %>" method="POST" class>
            <br/>
            <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
            <br/>
            <input type="hidden" name="lat" value="" id="lat">
            <input type="hidden" name="lng" value="" id="lng">
            <button type="submit" value="Submit"> SUBMIT </button>
            <br/>
          </form>
          <button onclick="getLocation()">Add your location</button>
          <div id="map"></div>
      <% } else { %>
        <form id="edit-message-form" action="/messages?countryCode=<%= countryCode %>&category=<%= category %>" method="POST">
          Edit your message:
          <br/>
          <textarea name="text" id="edit-message-input">
          <%=editText%>
          </textarea>
          <input type="hidden" name="messageID" value="<%=(String) request.getAttribute("editID")%>"/>
          <input type="hidden" name="lat" value="<%=(String) request.getAttribute("lat") %>" />
          <input type="hidden" name="lng" value="<%=(String) request.getAttribute("lng") %>" />
          <% String lng = (String) request.getAttribute("lng"); %>

          <button type="submit" value="Submit">Submit</button>
        </form>
        <% } %>
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
              <%= messages.get(i).getText() %>
            </div>
            <% if (currentUser != null && currentUser.equals(messages.get(i).getUser())) { %>
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
