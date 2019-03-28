package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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

    UserService userService = UserServiceFactory.getUserService();
    String requestUrl = request.getRequestURI();
    String user = requestUrl.substring("/users/".length());
    request.setAttribute("user", user);

    // Confirm that user is valid
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    User userData = datastore.getUser(user);

    // Send to error page if user does not exist
    if (userData == null) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-user/" + user);
      return;
    }

    // Fetch user messages
    List<Message> messages = datastore.getMessages(user);
    String aboutMe = userData.getAboutMe();
    boolean isViewingSelf =
        userService.isUserLoggedIn()
            && userData.getEmail().equals(userService.getCurrentUser().getEmail());

    // Add attributes to the request
    request.setAttribute("messages", messages);
    request.setAttribute("aboutMe", aboutMe);
    request.setAttribute("isUserLoggedIn", userService.isUserLoggedIn());
    request.setAttribute("isViewingSelf", isViewingSelf);
    request.getRequestDispatcher("/WEB-INF/user.jsp").forward(request, response);
  }
}
