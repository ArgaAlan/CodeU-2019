package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Country;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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

    // Confirm valid url format
    if (countryCode == null || countryCode.equals("")) {
      response.sendRedirect("/");
      return;
    }

    System.err.println("Country: " + countryCode);

    Country countryData = datastore.getCountry(countryCode);
    // Send to error page if country does not exist
    if (countryData == null) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-country/" + countryCode);
      return;
    }

    List<Message> messages = datastore.getMessages(countryCode);

    request.setAttribute("code", countryCode);
    request.setAttribute("name", countryData.getName());
    request.setAttribute("messages", messages);
    request.setAttribute("isUserLoggedIn", userService.isUserLoggedIn());
    request.getRequestDispatcher("/WEB-INF/country.jsp").forward(request, response);
  }
}
