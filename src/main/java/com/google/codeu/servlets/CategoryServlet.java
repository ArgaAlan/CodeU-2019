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
public class CategoryServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();
    boolean isUserLoggedIn = userService.isUserLoggedIn();
    
    String requestUrl = request.getRequestURI();
    String countryCode = requestUrl.substring("/country/".length());
    String category = null;

    // Confirm valid url format
    if (countryCode == null || countryCode.equals("")) {
      response.sendRedirect("/");
      return;
    }
    
    // Redirect to categories servlet if requesting category
    CharSequence catagoryURL = "/c";
    if (countryCode.contains(catagoryURL)) {
      String[] urlParts = countryCode.split("/c");
      countryCode = urlParts[0];
      category = urlParts[1];
      if(urlParts.length > 2) {
        System.err.println("Invalid path requested:");
        System.err.println("\tpath " + request.getServletPath());
        System.err.println("\turl " + request.getRequestURL());
        response.sendRedirect("/invalid-category/");
        return;
      }
    }

    System.err.println("Country: " + countryCode);

    request.setAttribute("category", category);
    request.getRequestDispatcher("/WEB-INF/category.jsp").forward(request, response);
  }
}
