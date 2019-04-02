package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Country;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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
	System.err.println(countryCode);
	System.err.println(category);
	Country country = datastore.getCountry(countryCode);
	Set<String> categories = country.getCategories();
	System.err.println(!categories.contains(category));
	System.err.println(categories);
	if(!categories.contains(category)) {
      System.err.println("Invalid path requested:");
      System.err.println("\tpath " + request.getServletPath());
      System.err.println("\turl " + request.getRequestURL());
      response.sendRedirect("/invalid-category/");
      return;
	}
    
    String requestUrl = request.getRequestURI();
    System.err.println(requestUrl);
    System.err.println("Category: " + request.getAttribute("category"));

    request.getRequestDispatcher("/WEB-INF/category.jsp").forward(request, response);
  }
}
