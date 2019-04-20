package com.google.codeu.servlets;

import com.google.codeu.data.Country;
import com.google.codeu.data.Datastore;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/category")
public class NewCategoryServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /** Makes a new category if user is logged in */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String newCategory = request.getParameter("category");
    String countryCode = request.getParameter("countryCode");

    // redirect to home if user is not logged in
    if (datastore.getCurrentUser() == null) {
      response.sendRedirect("/");
      return;
    }

    if (newCategory == null || newCategory.equals("")) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-category/" + newCategory);
    }

    // Replace spaces with underscores
    String regex = " ";
    String subst = "_";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(newCategory);

    String noSpaceCategory = matcher.replaceAll(subst);

    Country country = datastore.getCountry(countryCode);
    country.addCategory(noSpaceCategory);
    datastore.storeCountry(country);

    response.sendRedirect("/country/" + countryCode);
  }
}
