<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.*" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="com.google.codeu.data.Country" %>

<% String countryCode = (String) request.getAttribute("code"); %>
<% String countryName = (String) request.getAttribute("name"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% String currentUser = (String) request.getAttribute("currentUser"); %>
<% Country countryData = (Country) request.getAttribute("countryData"); %>
<% String category = "General"; %>      <!--Default category is General is if User doesn't select one-->

<!DOCTYPE html>
<html>
  <head>
    <title>User Page</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <link rel="stylesheet" href="/css/country.css">
    <script src="/js/message-loader.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
  </head>
  <body onload="buildUI()">
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
        <% if (currentUser != null) { %>
          <li><a href="/users/<%=currentUser%>">Your Page</a></li>
        <% } else { %>
          <li><a href="/login">Login</a></li>
        <% } %>
        <li><a href="/country/<%=countryCode%>/c/Food">Food</a></li>
        <li><a href="/country/<%=countryCode%>/c/Attractions">Attractions</a></li>
        <li><a href="/country/<%=countryCode%>/c/Culture">Culture</a></li>
      </ul>
    </nav>
    <h1 id="page-title"><%= countryName %></h1>

    <% if (currentUser != null) { %>
    <form id="message-form" action="/messages?countryCode=<%= countryCode %>&category=<%= category %>" method="POST" class>
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
    </form>
    <form  method="get">
      <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
      <input list="browsers" name="browser" placeholder="Select a Category">
      <datalist id="browsers">
        <% for (int i = 0; i < countryData.getCategories().size(); i++) {   %>
          <option><%= countryData.getCategories().get(i) %></option>
        <% }  %>
      </datalist>
      <input type="submit" name="categorySelect" value="Select">
    </form>
    <% }  %>
    <br/>
    <input type="submit" value="Submit">
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
              Time: <%= new Date(messages.get(i).getTimestamp()) %> -
              Sentiment Score: <%= messages.get(i).getSentimentScore() %>
            </div>
            <div class="message-body">
              <%= messages.get(i).getText() %>
            </div>
            <% if (currentUser != null && currentUser.equals(messages.get(i).getUser())) { %>
            <form id="delete-form" action="/messages" method="POST">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="callee" value="/country/<%=countryCode%>"/>
            <input type="hidden" name="messageID" value="<%=messages.get(i).getId()%>"/>
            <button type="submit" value="Submit">Delete</button>
          </form>
          <% } %>
            </div>
      <% }  %>
      </div>
    <script>
    /* When the user clicks on the button,
        toggle between hiding and showing the dropdown content */
      function myFunction() {
        document.getElementById("myDropdown").classList.toggle("show");
      }

      function filterFunction() {
        var input, filter, ul, li, a, i;
        input = document.getElementById("myInput");
        filter = input.value.toUpperCase();
        div = document.getElementById("myDropdown");
        a = div.getElementsByTagName("a");
        for (i = 0; i < a.length; i++) {
          txtValue = a[i].textContent || a[i].innerText;
          if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
          } else {
            a[i].style.display = "none";
          }
        }
      }

    </script>

  </body>
</html>
