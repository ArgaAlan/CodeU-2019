package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class HomeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    UserService userService = UserServiceFactory.getUserService();

    boolean isUserLoggedIn = userService.isUserLoggedIn();
    request.setAttribute("isUserLoggedIn", isUserLoggedIn);

    if (userService.isUserLoggedIn()) {
      String username = userService.getCurrentUser().getEmail();
      request.setAttribute("username", username);
    }

    // If page does not exist
    if (request.getServletPath() != "/") {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      request.getRequestDispatcher("/error.jsp").forward(request, response);
      return;
    }

    request.getRequestDispatcher("/index.jsp").forward(request, response);
  }
}
