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
  <body onload="buildUI()">
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


    <% 
    //limit to 5 posts per subchannel in main country page
    int limit = 5;
    %>
     <h4 id = "food-div">Food Thread</h4>
    <div class="message-container">
      <% 
      int categorySize = 0; 
      for (int j = 0; j < messages.size(); j++) {
            if (messages.get(j).getCategory().equals("Food")) {
              categorySize++;
          }
       }
       //no posts yet, display this button
      if (categorySize == 0) { %>
          <a href="/country/<%= countryCode %>/c/Food"><button class="limitPosts">Be the first to post on this thread.</button></a>
    <%  }
 
        limit = 5;
        for(int i = 0; i < messages.size(); i++) {
          if (limit == 0) {
            break;
          }
          if (messages.get(i).getCategory().equals("Food")) {
            limit--;
    %>
          <div class="message-div">
            <div class="message-header">
              User: <%= messages.get(i).getUser() %> |
              Time: <%= new Date(messages.get(i).getTimestamp()) %> |
              Category: <%= messages.get(i).getCategory() %>
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
              <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
              <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
              <button type="submit" value="Submit">DELETE</button>
            </form>
          <% } %>
            </div>
      <%    }
       }  %>
      </div>

      <% 
      //at least one post in Food
      if (categorySize != 0) { %>   
        <a href="/country/<%= countryCode %>/c/Food"><button class="limitPosts">Click to see full thread</button></a>
       <%  }
    %>

      <h4 id = "food-div">Culture Thread</h4>
      <div class="message-container">
      <% 
      int categorySizeC = 0; 
      for (int j = 0; j < messages.size(); j++) {
            if (messages.get(j).getCategory().equals("Culture")) {
              categorySizeC++;
          }
       }

      if (categorySizeC == 0) { %>
        <a href="/country/<%= countryCode %>/c/Culture"><button class="limitPosts">Be the first to post on this thread.</button></a>
    <%  }

        limit = 5;
        for(int i = 0; i < messages.size(); i++) {
          if (limit == 0) {
            break;
          }
          if (messages.get(i).getCategory().equals("Culture")) {
            limit--;
    %>
          <div class="message-div">
            <div class="message-header">
              User: <%= messages.get(i).getUser() %> |
              Time: <%= new Date(messages.get(i).getTimestamp()) %> |
              Category: <%= messages.get(i).getCategory() %>
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
              <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
              <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
              <button type="submit" value="Submit">DELETE</button>
            </form>
          <% } %>
            </div>
      <%    }
       }  %>
      </div>
    <% 
    //if there is at least one category size
    if (categorySizeC != 0) { %>
          <a href="/country/<%= countryCode %>/c/Culture"><button class="limitPosts">Click here to see full thread</button></a>
    <%  }


    //Attractions 
    %>
      <h4 id = "food-div">Attractions Thread</h4>
      <div class="message-container">
      <% 
      int categorySizeA = 0; 
      for (int j = 0; j < messages.size(); j++) {
            if (messages.get(j).getCategory().equals("Attractions")) {
              categorySizeA++;
          }
       }
       //if no posts, button displays this message
      if (categorySizeA == 0) { %>
        <a href="/country/<%= countryCode %>/c/Attractions"><button class="limitPosts">Be the first to post on this thread.</button></a>
    <%  }

        limit = 5;
        for(int i = 0; i < messages.size(); i++) {
          //stops creating new posts after 5 most recent in that subcategory
          if (limit == 0) {
            break;
          }
          if (messages.get(i).getCategory().equals("Attractions")) {
            limit--;
    %>
          <div class="message-div">
            <div class="message-header">
              User: <%= messages.get(i).getUser() %> |
              Time: <%= new Date(messages.get(i).getTimestamp()) %> |
              Category: <%= messages.get(i).getCategory() %>
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
              <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
              <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
              <button type="submit" value="Submit">DELETE</button>
            </form>
          <% } %>
            </div>
      <%    }
          }  %>
      </div>
    <% 
      
      //if there is at least one post, show this button
      if (categorySizeA != 0) { %>
          <a href="/country/<%= countryCode %>/c/Attractions"><button class="limitPosts">Click here to see full thread</button></a>
     <% } %>
     <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBCK_yt5P_kfz23tAb8tE_fptjRAn5jaB0">
    </script>
  </body>
</html>
