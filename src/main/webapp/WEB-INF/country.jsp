<% String country = (String) request.getAttribute("country"); %>
<% List<Message> messages = (List<Message>) request.getAttribute("messages"); %>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>

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
    <h1 id="page-title"><%= country %></h1>

    <% if(isUserLoggedIn){ %>
    <form id="message-form" action="/messages?country=<%= country %>" method="POST" class>
    Enter a new message:
    <br/>
    <textarea name="text" placeholder="Enter a message" id="message-input"></textarea>
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
