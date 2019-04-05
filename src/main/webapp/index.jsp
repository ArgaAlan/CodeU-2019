<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>CodeU Starter Project</title>
    <link rel="stylesheet" href="/css/main.css">
  </head>
  <body>
    <nav>
      <ul id="navigation">
        <li><a href="/">Home</a></li>
        <li><a href="/map.html">Map</a></li>
        <li><a href="/feed.html">Public Feed</a></li>
        <li><a href="/country/MX">Mexico</a></li>
        <li><a href="/country/US">United States</a></li>
        <li><a href="/country/CA">Canada</a></li>

    <%
      if (isUserLoggedIn) {
        String username = (String) request.getAttribute("username");
    %>
        <li><a href="/users/<%= username %>">Your Page</a></li>
        <li><a href="/logout">Logout</a></li>
    <% } else {   %>
        <li><a href="/login">Login</a></li>
    <% } %>

      </ul>
    </nav>

    <center>
    <font color="#4c008e">
      <h1>ABOUT THE TEAM</h1>
      <p>Welcome to CodeU 2019 Team 6!</p>
    </font>

      <p>We are composed of four student members, one Project Advisor, and
        one Cohort Lead</p> </center>
      <font color = "#544610">
      <center><p><b>STUDENTS</b></center>
        <li> <b> Alan González: </b>
          Second-year student in Tecnologico de Monterrey Campus Guadalajara. Living in Guadalajara but born in Tepic, Nayarit.
          Passion for CS and main hobbie Videogames.
          <br /> &emsp;&emsp;
          Fun Fact: First time turning on a car, first time the car crashed.
          <br><br>
        </li>
        <li> <b> Drew Bernard: </b>
          Drew is a third-year student studying computer science at UT Austin.
          He enjoys working out, basketball, traveling, and recreational ping-pong.
          <br /> &emsp;&emsp;
          Fun Fact: Drew has an unheatlhy sweet tooth; will probably have some type
          of chocolate snack or sweet on him at any given moment.
        </li>
        <br>
        <li> <b> Grace Chong: </b>
          Grace is a second year student at the University of Pennsylvania studying computer science and cognitive science. She is passionate about technology for social impact, design, and playing the ukelele.
        <br /> &emsp;&emsp;
          Fun Fact: Grace loves driving and traveling to new places - during this summer, she drove 8 hours to Montreal, Canada with her friends!
        </li>
        <br>
        <li> <b> Nicole Baldy:</b>
          Nicole is a second-year student at the University of Akron, studying
          Computer Engineering. She loves hiking, board games, rollar blading, and
          the Lord of The Rings.
          <br /> &emsp;&emsp;
          Fun Fact: Nicole accidentally took her cat camping once
        </li>
      </p>
      <center><p><b> PROJECT ADVISOR</b><br /></p></center>
        <b>Aaron Colwell: </b>
        Aaron works at Google in Seattle, and has been at Google for
        8.5 years. He's worked on Google Chrome, Google Clips, and Daydream VR.
        He forgot to intruduce himself with a fun fact, so Nicole decided to make
        one up.
        <br />
        Fun Fact: Alongside his time at Google, Aaron has also led a very successful
        career in part-time professional yodeling. It has been rumored that, on
        a clear day, his yodels can be heard over five miles away.
      </p>

      <center><p><b> COHORT LEAD </b><br /></center>
        <b>Jess Torres: </b>
        Jess is on the main CodeU team, so she works on CodeU all year round,
        and seems to have done a fantastic job so far. She's from the Central
        Valley of California, and went to school at U.C. Santa Barbara.
        <br />
        Fun Fact: Jess loves traveling, is terrified of frogs, and had a pet calf
        when she was young.
      </p>
    </font>
    </body>
</html>
