<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% boolean isUserLoggedIn = (boolean) request.getAttribute("isUserLoggedIn"); %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Team JADANG</title>
    <link rel="stylesheet" href="/css/main.css">
      <link rel="stylesheet" href="/css/map.css">
        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCgr6Zutm1Os7q9V5Fi9N_g2poKcDG2G0A">
        </script>

        <script>
          var map;
          var markers = [];
          var count = 0;
          //uses window.setTimeout() to drop markers consecutively
          function addMarkerWithTimeout(position, timeout) {
            window.setTimeout(function() {
              markers.push(new google.maps.Marker({
                position: position,
                map: map,
                animation: google.maps.Animation.DROP
              }));
            }, timeout);
          }
          //initializes the map with specified center and zoom
          function createCountriesMap(){
            fetch('/Countries').then(function(response) {
              return response.json();
            }).then((CountryMaps) => {
                var prev_infowindow = null;
                map = new google.maps.Map(document.getElementById('map'), {
                center: {lat: 16.4774, lng: -24.97},
                zoom: 3
              });
              CountryMaps.forEach((CountryMap) => {
                 var countryUrl = '/country/' + CountryMap.code;
                 const marker = new google.maps.Marker({
                  position: {lat: CountryMap.lat, lng: CountryMap.lng},
                  map: map
                });
                 marker.addListener('click', function() {
                  content: CountryMap.name
                  window.location.href = countryUrl;
                  //prevents multiple infowindows from being open at the same time
                  if (prev_infowindow) {
                    prev_infowindow.close();
                  }
                  prev_infowindow = infoWindow;
                  infoWindow.open(map, marker);
                 });
                 var infoWindow = new google.maps.InfoWindow({
                  content: CountryMap.name
                 });
              });
            });
          }
        </script>
  </head>
  <body onload="createCountriesMap()">
    <div class="navbar">
      <a href="/">Home</a>
      <a href="/feed.html">Public Feed</a>
    <%
      if (isUserLoggedIn) {
        String userEmail = (String) request.getAttribute("userEmail");
    %>
      <a href="/users/<%= userEmail %>">Your Page</a>
      <a href="/logout">Logout</a>
    <% } else {   %>
      <a href="/login">Login</a>
    <% } %>
    </div>

    <h1>Welcome!</h1>
    <h3>Click on a country to start chatting!</h3>
    <div id="map"></div>

    <h3> Our goal was to create a user-friendly experience for travelers all over the world to discuss various aspects they enjoyed about their trips, like great foods and "must-see" attractions. We also hoped that through this public forum type experience, we would be able to eliminate some of the negative culture shock that people sometimes encounter when traveling to new places. Travelers can discuss the negatives and positives about their trip, and can warn others who are visiting the same places about the negatives while also sharing their favorite parts of their journey! You can add images, videos, or your current location to the post. To add images, simply copy and paste the image address or upload a file from your computer. To add videos, paste the youtube URL into the message box. To add your location, just press the button "Add your location".
    	You can go to any country page and see the most recent messages for every category.
    	We hope that you have a good time at our site, have fun!</h3>

    <font color = "#544610">
    <h1>
      About Team JADANG
    </h1>
    <center><p>
      <b>Jess Torres, <i> Cohort Lead </i></b>
      <br />
      Jess is on the main CodeU team, so she works on CodeU all year round,
      and seems to have done a fantastic job so far. She's from the Central
      Valley of California, and went to school at U.C. Santa Barbara.
      <br />
      Fun Fact: Jess loves traveling, is terrified of frogs, and had a pet calf
      when she was young.
    </p>
    <p>
      <b>Aaron Colwell, <i> Project Advisor </i></b>
      <br />
      Aaron works at Google in Seattle, and has been at Google for
      8.5 years. He's worked on Google Chrome, Google Clips, and Daydream VR.
      <br />
      Fun Fact: Aaron just won an Emmy this April!
      <br />
      <i> Extra Fun Fact that Nicole initially made up because Aaron forgot to introduce himself with one: </i>
      Alongside his time at Google, Aaron has also led a very successful
      career in part-time professional yodeling. It has been rumored that, on
      a clear day, his yodels can be heard over five miles away.
    </p>
    <p>
      <b> Drew Bernard, <i> Student </i> </b>
      <br />
        Drew is a third-year student studying computer science at UT Austin.
        He enjoys working out, basketball, traveling, and recreational ping-pong.
        <br />
        Fun Fact: Drew has an unheatlhy sweet tooth; will probably have some type
        of chocolate snack or sweet on him at any given moment.
      </p>
      <p>
        <b> Alan González, <i> Student </i> </b>
        <br />
        Second-year student in Tecnologico de Monterrey Campus Guadalajara. Living in Guadalajara but born in Tepic, Nayarit.
        Passion for CS and main hobbie Videogames.
        <br />
        Fun Fact: First time turning on a car, first time the car crashed.
      </p>
      <p>
       <b> Nicole Baldy, <i> Student </i> </b>
       <br />
        Nicole is a second-year student at the University of Akron, studying
        Computer Engineering. She loves hiking, board games, rollar blading, and
        fiction writing.
        <br />
        Fun Fact: Nicole accidentally took her cat camping once
      </p>
      <p>
        <b> Grace Chong, <i> Student </i> </b>
        <br />
          Grace is a second year student at the University of Pennsylvania studying computer science and cognitive science. She is passionate about technology for social impact, design, and playing the ukelele.
        <br />
          Fun Fact: Grace loves driving and traveling to new places - during this summer, she drove 8 hours to Montreal, Canada with her friends!
      </p>
    </center>
    </font>
    </body>
