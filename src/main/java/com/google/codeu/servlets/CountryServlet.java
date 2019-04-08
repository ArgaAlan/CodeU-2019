package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Country;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/country/*")
public class CountryServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();

    Scanner scanner =
        new Scanner(getServletContext().getResourceAsStream("/WEB-INF/countries.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      String code = cells[0];
      String name = cells[1];
      double lat = Double.parseDouble(cells[2]);
      double lng = Double.parseDouble(cells[3]);

      Country country = new Country(code, name, lat, lng);
      datastore.storeCountry(country);
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();

    String requestUrl = request.getRequestURI();
    String countryCode = requestUrl.substring("/country/".length());
    String category = null;

    // Confirm valid url format
    if (countryCode == null || countryCode.equals("")) {
      response.sendRedirect("/");
      return;
    }

    // Redirect to category servlet if requesting category
    String regexURL = "/c/(.*)";
    Pattern urlPattern = Pattern.compile(regexURL);
    Matcher urlMatcher = urlPattern.matcher(requestUrl);
    if (urlMatcher.find()) {
      String[] urlParts = countryCode.split("/c");
      countryCode = urlParts[0];
      category = urlParts[1].substring("/".length());
      if(urlParts.length > 2) {
        System.err.println("Invalid path requested:");
        System.err.println("\tpath " + request.getServletPath());
        System.err.println("\turl " + request.getRequestURL());
        response.sendRedirect("/invalid-category/");
        return;
      }
    }

    Country countryData = datastore.getCountry(countryCode);
    // Send to error page if country does not exist
    if (countryData == null) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-country/" + countryCode);
      return;
    }

    request.setAttribute("code", countryCode);
    request.setAttribute("name", countryData.getName());
    request.setAttribute("isUserLoggedIn", userService.isUserLoggedIn());

    if(category != null && !category.isEmpty()) {
      request.setAttribute("category", category);
      request.setAttribute("countryCode", countryCode);
      List<Message> messages = datastore.getMessagesByCategory(countryCode, category);
      request.setAttribute("messages", messages);
      request.getRequestDispatcher("/c").forward(request, response);
    } else {
      List<Message> messages = datastore.getCountryMessages(countryCode);
      request.setAttribute("messages", messages);
      request.getRequestDispatcher("/WEB-INF/country.jsp").forward(request, response);
    }
  }
}
