<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>

<% String countryCode = (String) request.getAttribute("countryCode"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% Set<String> categories = (HashSet) request.getAttribute("categories"); %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
<% String uploadUrl = blobstoreService.createUploadUrl("/messages"); %>

<!DOCTYPE html>
<html>
  <head>
    <title><%=countryName%></title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <script src="/js/message-loader.js"></script>
    <script src="/js/location.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    <script>

      var selectedCategory = "General";
      /* When the user clicks on the button,
          toggle between hiding and showing the dropdown content */
      function updateMessageCategory() {
        var e = document.getElementById("myDropdown");
        selectedCategory = e.options[e.selectedIndex].value;
        document.getElementById("message-category").value = selectedCategory;
      }

      function onLoad(){
        buildUI();
        updateMessageCategory();
      }

    </script>
  </head>
  <body onload="onLoad()">
    <div class="navbar">
      <a href="/">Home</a>
    <% if (currentUser != null) { %>
      <a href="/users/<%=currentUser%>">Your Page</a>
    <% } else { %>
      <a href="/login">Login</a>
    <% } %>
      <div class="dropdown">
        <button class="dropbtn">Categories
          <i class="fa fa-caret-down"></i>
        </button>
        <div class="dropdown-content">
          <div>
    <%  Iterator iter = categories.iterator();
        while (iter.hasNext()) {
          String categoryList = (String) iter.next();    %>
          <a href="/country/<%= countryCode %>/c/<%= categoryList %>"><%= categoryList %></a>
    <%  }   %>
  </div>
        </div>
      </div>
    </div>
    <h1 id="page-title"><%= countryName %></h1>

    <% if (currentUser != null) { %>
      <form id="message-form" action="<%=uploadUrl%>" method="POST" enctype="multipart/form-data">
        Enter a new message:
        <br/>
        <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
        <input type="hidden" name="category" value="" id="message-category">
        <input type="hidden" name="countryCode" value="<%=countryCode%>">
        <input type="hidden" name="lat" value="" id="lat">
        <input type="hidden" name="lng" value="" id="lng">
        <select id="myDropdown" onchange="updateMessageCategory()">
        <%
          Iterator iter1 = categories.iterator();
          while (iter1.hasNext()) {
          String categoryList = (String) iter1.next();
        %>
          <option><%= categoryList %></option>
        <% }  %>
        </select>
        <input type="submit" value="Submit">
        <br/>
        Add an image to your message:
        <input type="file" name="image">
        <br/>
      </form>
      <button onclick="getLocation()">Add your location</button>
      <div id="map"></div>
    <% }  %>
    <br/>
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
                <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
                <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
                <button type="submit" value="Submit">DELETE</button>
              </form>
          <% } %>
          </div>
      <% }  %>
    </div>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBCK_yt5P_kfz23tAb8tE_fptjRAn5jaB0">
    </script>
  </body>
</html>
