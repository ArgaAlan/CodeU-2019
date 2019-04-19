package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/replies")
public class RepliesServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
    datastore.addIDAllMessages();
  }

  /** Edits a {@link Message}. Either creates and stores the message, or Deletes the message */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    User currentUser = datastore.getCurrentUser();

    // redirect to home if user is not logged in
    if (currentUser == null) {
      response.sendRedirect("/");
      return;
    }

    Message reply = (Message) request.getAttribute("reply");
    UUID parentUUID = (UUID) request.getAttribute("parent");
    reply.setParent(parentUUID);

    Message parent = datastore.getMessageByUUID(parentUUID.toString());
    parent.addReply(reply.getId());

    datastore.storeMessage(reply);
    datastore.storeMessage(parent);
  }
}

