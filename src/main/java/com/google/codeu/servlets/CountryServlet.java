package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import java.io.IOException;
import java.util.List;
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
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();
    String requestUrl = request.getRequestURI();
    String country = requestUrl.substring("/country/".length());

    // Confirm valid url format
    if (country == null || country.equals("")) {
      response.sendRedirect("/");
      return;
    }

    // Send to error page if user does not exist
    if (!datastore.isValidCountry(country)) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-country/" + country);
      return;
    }

    List<Message> messages = datastore.getMessages(country);

    request.setAttribute("country", country);
    request.setAttribute("messages", messages);
    request.setAttribute("isUserLoggedIn", userService.isUserLoggedIn());
    request.getRequestDispatcher("/WEB-INF/country.jsp").forward(request, response);
  }
}
