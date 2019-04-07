package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    User currentUser = datastore.getCurrentUser();
    String requestUrl = request.getRequestURI();
    String userPageEmail = requestUrl.substring("/users/".length());
    User loggedInUser = datastore.getCurrentUser();
    User viewedUser = datastore.getUser(userPageEmail);

    // Confirm that the user page url is valid
    if (userPageEmail == null || userPageEmail.isEmpty()) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    // Send to error page if user does not exist
    if (viewedUser == null) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-user/" + userPageEmail);
      return;
    }

    // Fetch user messages
    List<Message> messages = datastore.getMessagesByUser(userPageEmail);
    String aboutMe = viewedUser.getAboutMe();
    boolean isViewingSelf = loggedInUser != null && userPageEmail.equals(loggedInUser.getEmail());

    request.setAttribute("user", userPageEmail);
    request.setAttribute("messages", messages);
    request.setAttribute("aboutMe", aboutMe);
    request.setAttribute("isViewingSelf", isViewingSelf);
    request.getRequestDispatcher("/WEB-INF/user.jsp").forward(request, response);
  }
}
