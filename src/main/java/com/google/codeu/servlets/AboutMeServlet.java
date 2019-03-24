package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Fetches and saves user data */
@WebServlet("/about")
public class AboutMeServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /** Responds with "about me" section for the user */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("text/html");

    String user = request.getParameter("user");
    if (user == null || user.equals("")) {
      System.err.println("Error from AboutMeServlet: User parameter invalid.");
      response.sendRedirect("/");
      return;
    }

    User userData = datastore.getUser(user);
    if (userData == null || userData.getAboutMe() == null) {
      response.getOutputStream().println("This \"About me\" page is empty :(");
      return;
    }

    response.getOutputStream().println(userData.getAboutMe());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.relaxed());

    User user = new User(userEmail, aboutMe);
    datastore.storeUser(user);

    response.sendRedirect("/users/" + userEmail);
  }
}
