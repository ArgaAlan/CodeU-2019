package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.Country;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/category")
public class NewCategoryServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Edits a {@link Message}. Either creates/edits and stores the message, or Deletes the message
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    String newCategory = request.getParameter("category");
    String countryCode = request.getParameter("countryCode");

    if(newCategory == null || newCategory.equals("")) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-category/" + newCategory);
    }

    Country country = datastore.getCountry(countryCode);
    country.addCategory(newCategory);
    datastore.storeCountry(country);

    response.sendRedirect("/country/"+countryCode);
  }
}
