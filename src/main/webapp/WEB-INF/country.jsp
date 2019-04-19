<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>

<% String countryCode = (String) request.getAttribute("countryCode"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% Set<String> categories = (HashSet) request.getAttribute("categories"); %>

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
      var withoutCategoryURL = "/messages?countryCode=<%= countryCode %>&category="
      var withCategoryURL = "/messages?countryCode=<%= countryCode %>&category=" + selectedCategory;

      /* When the user clicks on the button,
          toggle between hiding and showing the dropdown content */
      function updateMessageCategory() {
        var e = document.getElementById("myDropdown");
        selectedCategory = e.options[e.selectedIndex].value;
        withCategoryURL = withoutCategoryURL + selectedCategory;
        document.getElementById("message-form").action = withCategoryURL;
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
    <form id="message-form" action="" method="POST" class>
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
      <select id="myDropdown" onchange="updateMessageCategory()">
    <%  Iterator iter1 = categories.iterator();
        while (iter1.hasNext()) {
        String categoryList = (String) iter1.next();  %>
        <option><%= categoryList %></option>
        <% }  %>
      </select>
      <input type="submit" value="Submit">
    </form>
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
  </body>
</html>
