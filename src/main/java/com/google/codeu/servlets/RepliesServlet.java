package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.Reply;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving {@link Reply} instances. */
@WebServlet("/reply")
public class RepliesServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /** Stores a {@link Reply} in Datastore */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    User currentUser = datastore.getCurrentUser();

    // redirect to home if user is not logged in
    if (currentUser == null) {
      response.sendRedirect("/");
      return;
    }

    String parentID = (String) request.getParameter("parentID");

    if (parentID == null) {
      response.sendRedirect("/");
      return;
    }

    // This is a reply - image upload and location finding is disabled
    Message parent = datastore.getMessageByID(parentID);
    if (parent == null) {
      response.sendRedirect("/");
      return;
    }
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.relaxed());

    Reply reply = new Reply(parentID, currentUser.getEmail(), text);
    parent.addReply(reply.getId());

    datastore.storeMessage(parent);
    datastore.storeReply(reply);

    response.sendRedirect("/thread/" + parentID);
    return;
  }

  /**
   * Responds with a JSON representation of {@link Reply} data for a specific parent ID string so
   * that the parent message is first and then the replies. Responds with an empty array if the
   * parentID is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String parentID = (String) request.getParameter("parentID");

    if (parentID == null || parentID.isEmpty()) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    Message parent = datastore.getMessageByID(parentID);
    List<Reply> replies = datastore.getRepliesByID(parentID);
    Gson gson = new Gson();
    String jsonParent = gson.toJson(parent);
    String jsonReplies = gson.toJson(replies);

    response.getWriter().println(jsonParent + jsonReplies);
  }
}
