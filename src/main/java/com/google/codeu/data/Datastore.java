/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.*;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    // Do not post message if user is not message poster or not logged on
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      System.err.println("Invalid Credentials: attempt to post message while not logged in");
      return;
    }
    String userEmail = userService.getCurrentUser().getEmail();
    if (!message.getUser().equals(userEmail)) {
      System.err.println(
          "Invalid Credentials: User "
              + userEmail
              + " attempt to post message by "
              + message.getUser());
      return;
    }

    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("ID", message.getId().toString());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("country", message.getCountry());
    messageEntity.setProperty("category", message.getCategory());
    messageEntity.setProperty("lat", message.getLat());
    messageEntity.setProperty("lng", message.getLng());
    messageEntity.setProperty("replies", message.getReplies());
    messageEntity.setProperty("image", message.getImageUrl());

    datastore.put(messageEntity);
  }

  /** Stores the Reply in Datastore. */
  public void storeReply(Reply reply) {
    // Do not post message if user is not message poster or not logged on
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      System.err.println("Invalid Credentials: attempt to post replyEntity while not logged in");
      return;
    }
    String userEmail = userService.getCurrentUser().getEmail();
    if (!reply.getUser().equals(userEmail)) {
      System.err.println(
          "Invalid Credentials: User "
              + userEmail
              + " attempt to post reply by "
              + reply.getUser());
      return;
    }
    Entity replyEntity = new Entity("Reply", reply.getId());
    replyEntity.setProperty("user", reply.getUser());
    replyEntity.setProperty("ID", reply.getId());
    replyEntity.setProperty("text", reply.getText());
    replyEntity.setProperty("timestamp", reply.getTimestamp());
    replyEntity.setProperty("parentID", reply.getParentId());
    datastore.put(replyEntity);
  }

  public String getMessageTextByID(String messageID) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("ID", FilterOperator.EQUAL, messageID));

    PreparedQuery results = datastore.prepare(query);
    Entity messageEntity = results.asSingleEntity();

    if (messageEntity == null) {
      System.err.println("Invalid Message ID - " + messageID);
      return null;
    }

    return (String) messageEntity.getProperty("text");
  }

  public void deleteMessageWithID(String messageID) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("ID", FilterOperator.EQUAL, messageID));

    PreparedQuery results = datastore.prepare(query);
    Entity messageEntity = results.asSingleEntity();

    if (messageEntity == null) {
      System.err.println("Invalid Message ID - " + messageID);
      return;
    }
    // Ensure that message poster is the same as deleter
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      System.err.println("Invalid Credentials: attempt to delete message while not logged in");
      return;
    }
    String userEmail = userService.getCurrentUser().getEmail();
    if (!messageEntity.getProperty("user").equals(userEmail)) {
      System.err.println(
          "Invalid Credentials: User "
              + userEmail
              + " attempt to delete message by "
              + messageEntity.getProperty("user"));
      return;
    }
    datastore.delete(messageEntity.getKey());
  }

  /**
   * Gets messages posted to a specific country page.
   *
   * @return a list of messages posted to country page, or empty list if no messages posted to
   *     country page. List is sorted by time descending.
   */
  public List<Message> getCountryMessages(String countryCode) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("country", FilterOperator.EQUAL, countryCode))
            .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets message with a specific ID
   *
   * @param messageID ID of message which is being returned
   * @return the message corresponding with the ID
   */
  public Message getMessageByID(String messageID) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("ID", FilterOperator.EQUAL, messageID));

    PreparedQuery results = datastore.prepare(query);
    Entity messageEntity = results.asSingleEntity();
    if (messageEntity == null) {
      System.err.println("Invalid Message ID - " + messageID);
      return null;
    }

    List<Message> list = convertEntitiesToMessages(results);
    return list.get(0);
  }

  /**
   * Gets messages posted as replies to a parent message.
   *
   * @return a list of replies to the parent message, or empty list if there are no reply messages.
   *     List is sorted by time descending.
   */
  public List<Reply> getRepliesByID(String parentID) {
    Query query =
        new Query("Reply")
            .setFilter(
                new Query.FilterPredicate("parentID", FilterOperator.EQUAL, parentID.toString()))
            .addSort("timestamp", SortDirection.ASCENDING);

    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToReplies(results);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by user, or empty list if no messages posted to country page.
   *     List is sorted by time descending.
   */
  public List<Message> getMessagesByUser(String user) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
            .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by user, or empty list if no messages posted to country page.
   *     List is sorted by time descending.
   */
  public List<Message> getMessagesByCategory(String countryCode, String category) {

    Query.Filter countryFilter =
        new Query.FilterPredicate("country", FilterOperator.EQUAL, countryCode);
    Query.Filter categoryFilter =
        new Query.FilterPredicate("category", FilterOperator.EQUAL, category);

    Query.Filter combinedFilter = Query.CompositeFilterOperator.and(countryFilter, categoryFilter);

    Query query =
        new Query("Message")
            .setFilter(combinedFilter)
            .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets messages posted by all users.
   *
   * @return a list of messages posted by all users, or empty list if no user has ever posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {

    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /** Gets all messages and adds the "ID" property if it does not exist */
  public void addIDAllMessages() {

    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      try {
        if (entity.getProperty("ID") == null || ((String) entity.getProperty("ID")).isEmpty()) {
          String idString = entity.getKey().getName();
          entity.setProperty("ID", idString);
          datastore.put(entity);
        }
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
  }

  /**
   * Gets messages of from a prepared query of messages
   *
   * @return List<Message> from the query results
   */
  public List<Message> convertEntitiesToMessages(PreparedQuery results) {
    List<Message> messages = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String country = (String) entity.getProperty("country");
        String category = (String) entity.getProperty("category");
        String lat = (String) entity.getProperty("lat");
        String lng = (String) entity.getProperty("lng");
        String imageUrl = (String) entity.getProperty("image");
        Message message = new Message(id, user, text, timestamp, country, category, lat, lng);
        message.setImageUrl(imageUrl);
        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return messages;
  }

  /**
   * Gets Replies from a prepared query for replies
   *
   * @return List<Replies> from the query results
   */
  public List<Reply> convertEntitiesToReplies(PreparedQuery results) {
    List<Reply> replies = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String parentIDStr = (String) entity.getProperty("parentID");
        UUID parentID = UUID.fromString(parentIDStr);
        Reply reply = new Reply(id, parentID, user, text, timestamp);
        replies.add(reply);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return replies;
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /** Stores the User in Datastore Entity */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    datastore.put(userEntity);
  }

  /** Stores the Country in Datastore Entity */
  public void storeCountry(Country country) {
    Entity countryEntity = new Entity("Country", country.getCode());
    countryEntity.setProperty("code", country.getCode());
    countryEntity.setProperty("name", country.getName());
    countryEntity.setProperty("lat", country.getLat());
    countryEntity.setProperty("lng", country.getLng());
    countryEntity.setProperty("categories", country.getCategories());
    datastore.put(countryEntity);
  }

  /** Returns the current user or null if not logged in */
  public User getCurrentUser() {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) return null;

    String userEmail = userService.getCurrentUser().getEmail();
    Query query =
        new Query("User")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, userEmail));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();

    // User does not yet exist - make and return user
    if (userEntity == null) {
      User currentUser = new User(userEmail, "This \"About me\" page is empty :(");
      storeUser(currentUser);
      return currentUser;
    }

    // User exists - return user
    String aboutMe = (String) userEntity.getProperty("aboutMe");
    User currentUser = new User((String) userEntity.getProperty("email"), aboutMe);
    return currentUser;
  }

  /** Returns the User of email address with aboutMe, or null if no matching User was found. */
  public User getUser(String email) {
    Query query =
        new Query("User")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    if (userEntity == null) {
      return null;
    }
    // Return aboutMe
    String aboutMe = (String) userEntity.getProperty("aboutMe");
    User user = new User((String) userEntity.getProperty("email"), aboutMe);
    return user;
  }

  /**
   * Returns the country entity associated with the country code, or null if no matching Country was
   * found
   */
  public Country getCountry(String countryCode) {
    Query query =
        new Query("Country")
            .setFilter(new Query.FilterPredicate("code", FilterOperator.EQUAL, countryCode));
    PreparedQuery results = datastore.prepare(query);
    Entity countryEntity = results.asSingleEntity();
    if (countryEntity == null) {
      return null;
    }
    String name = (String) countryEntity.getProperty("name");
    double lat = (double) countryEntity.getProperty("lat");
    double lng = (double) countryEntity.getProperty("lng");
    List<String> list = (List<String>) countryEntity.getProperty("categories");
    HashSet<String> categories = new HashSet<String>(list);

    Country country = new Country(countryCode, name, lat, lng, categories);
    return country;
  }
}
