package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/thread")
public class CategoryServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String parentID = requestUrl.substring("/thread/".length());

    // Confirm valid url format
    if (parentID == null || parentID.isEmpty()) {
      response.sendRedirect("/");
      return;
    }

    Message parent = datastore.getMessageByID(parentID);
    if (parent == null) {
      response.sendRedirect("/invalid-id/" + parentID);
      return;
    }

    request.setAttribute("parent", parent);
    List<Message> replies = datastore.getRepliesByID(parentID);
    request.setAttribute("replies", replies);

    request.setAttribute("isLoggedIn", datastore.getCurrentUser() != null);
    request.getRequestDispatcher("/WEB-INF/message-thread.jsp").forward(request, response);
  }
}
