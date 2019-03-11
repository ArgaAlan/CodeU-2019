package com.google.codeu.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.codeu.data.Datastore;
import com.google.gson.JsonObject;

/**
 * Handles fetching site statistics.
 */
@WebServlet("/stats")
public class StatsPageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with site statistics in JSON.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    // List of all messages
    List<com.google.codeu.data.Message> allmessages = datastore.getAllMessages();

    // Json for the JS
    JsonObject jsonObject = new JsonObject();

    // Number of messages
    int messageCount = datastore.getTotalMessageCount();

    // Number of messages property added to the Json
    jsonObject.addProperty("messageCount", messageCount);

    // Store of the biggest message
    com.google.codeu.data.Message biggest = ((allmessages.size() == 0) ? null : allmessages.get(0));

    /*
     * countM, total number of messages users, register of all users
     */
    int countM = 0;
    HashMap<String, Integer> usersAndNumOfMessages = new HashMap<>();

    String userMostMessages = "";
    int mostMessages = 0;

    for (int i = 0; i < allmessages.size(); i++) {
      // count for the avg of all messages
      countM += allmessages.get(i).getText().length();

      // getting the biggest message
      if (allmessages.get(i).getText().length() > biggest.getText().length()) {
        biggest = allmessages.get(i);
      }


      // getting and saving the user and message count and checking who has the most sent messages
      if (!usersAndNumOfMessages.containsKey(allmessages.get(i).getUser())) {
        usersAndNumOfMessages.put(allmessages.get(i).getUser(), 1);
        if (1 >= mostMessages) {
          mostMessages = 1;
          userMostMessages = allmessages.get(i).getUser();
        }
      } else {
        usersAndNumOfMessages.put(allmessages.get(i).getUser(),
            usersAndNumOfMessages.get(allmessages.get(i).getUser()) + 1);
        if (usersAndNumOfMessages.get(allmessages.get(i).getUser()) >= mostMessages) {
          mostMessages = usersAndNumOfMessages.get(allmessages.get(i).getUser());
          userMostMessages = allmessages.get(i).getUser();
        }
      }
    }

    /*
     * Adding properties to Json: Average length of messages Biggest message found Total users List
     * of users User with most messages sent
     */

    jsonObject.addProperty("messageAvg",
        ((allmessages.size() == 0) ? 0 : countM / allmessages.size()));
    jsonObject.addProperty("biggestMessage", ((biggest == null) ? "" : biggest.getText()));
    jsonObject.addProperty("usersCount", usersAndNumOfMessages.size());
    jsonObject.addProperty("usersList",
        ((usersAndNumOfMessages.size() == 0) ? "" : usersAndNumOfMessages.keySet().toString()));
    jsonObject.addProperty("mostActiveUser", userMostMessages);
    response.getOutputStream().println(jsonObject.toString());

  }
}
