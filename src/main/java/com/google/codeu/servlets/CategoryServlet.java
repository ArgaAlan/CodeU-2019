package com.google.codeu.servlets;

import com.google.codeu.data.Country;
import com.google.codeu.data.Datastore;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/c")
public class CategoryServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String category = (String) request.getAttribute("category");
    String countryCode = (String) request.getAttribute("countryCode");

    Country country = datastore.getCountry(countryCode);
    Set<String> categories = country.getCategories();

    // redirect if category doesn't exist for the country
    if (!categories.contains(category)) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-category/");
      return;
    }

    request.getRequestDispatcher("/WEB-INF/category.jsp").forward(request, response);
  }
}
