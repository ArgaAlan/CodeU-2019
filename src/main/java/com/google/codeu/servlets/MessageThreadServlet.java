package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.Reply;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/thread/*")
public class MessageThreadServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();

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
    List<Reply> replies = datastore.getRepliesByID(parentID);
    request.setAttribute("replies", replies);
    if (datastore.getCurrentUser() == null) request.setAttribute("currentUser", null);
    else request.setAttribute("currentUser", datastore.getCurrentUser().getEmail());
    request.getRequestDispatcher("/WEB-INF/message-thread.jsp").forward(request, response);
  }
}
