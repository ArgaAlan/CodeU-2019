package com.google.codeu.servlets;

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

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    User currentUser = datastore.getCurrentUser();
    if (currentUser == null) {
      response.sendRedirect("/");
      return;
    }

    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.relaxed());

    User userNewAboutMe = new User(currentUser.getEmail(), aboutMe);
    datastore.storeUser(userNewAboutMe);

    response.sendRedirect("/users/" + currentUser.getEmail());
  }
}
