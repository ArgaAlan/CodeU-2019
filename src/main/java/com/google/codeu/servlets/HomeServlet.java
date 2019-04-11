package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class HomeServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    User loggedInUser = datastore.getCurrentUser();

    request.setAttribute("isUserLoggedIn", loggedInUser != null);

    if (loggedInUser != null) {
      String userEmail = loggedInUser.getEmail();
      request.setAttribute("userEmail", userEmail);
    }

    // If not home page, we know page does not exist - send to error page
    if (request.getServletPath() != "/") {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
      return;
    }

    request.getRequestDispatcher("/index.jsp").forward(request, response);
  }
}
