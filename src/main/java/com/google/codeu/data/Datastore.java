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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("country", message.getCountry());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());
    messageEntity.setProperty("image", message.getImageUrl());

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted to a specific country page.
   *
   * @return a list of messages posted to country page, or empty list if no messages posted to
   *         country page. List is sorted by time descending.
   */
  public List<Message> getCountryMessages(String countryCode) {
    Query query = new Query("Message")
        .setFilter(new Query.FilterPredicate("country", FilterOperator.EQUAL, countryCode))
        .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by user, or empty list if no messages posted to country page.
   *         List is sorted by time descending.
   */
  public List<Message> getMessagesByUser(String user) {
    Query query = new Query("Message")
        .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
        .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets messages posted by all users.
   *
   * @return a list of messages posted by all users, or empty list if no user has ever posted a
   *         message. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {

    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    return convertEntitiesToMessages(results);
  }

  /**
   * Gets messages of all users specified by s/users/users/
   *
   * @return null, updates List<Message> messages
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
        String recipient = (String) entity.getProperty("recipient");
        float sentimentScore = entity.getProperty("sentimentScore") == null ? (float) 0.0
            : ((Double) entity.getProperty("sentimentScore")).floatValue();
        String imageUrl = (String) entity.getProperty("imageUrl");
        Message message = new Message(id, user, text, timestamp, recipient, sentimentScore);
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
    datastore.put(countryEntity);
  }

  /** Returns the User of email address with aboutMe, or null if no matching User was found. */
  public User getUser(String email) {
    Query query = new Query("User")
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
    Query query = new Query("Country")
        .setFilter(new Query.FilterPredicate("code", FilterOperator.EQUAL, countryCode));
    PreparedQuery results = datastore.prepare(query);
    Entity countryEntity = results.asSingleEntity();
    if (countryEntity == null) {
      return null;
    }
    String name = (String) countryEntity.getProperty("name");
    double lat = (double) countryEntity.getProperty("lat");
    double lng = (double) countryEntity.getProperty("lng");

    Country country = new Country(countryCode, name, lat, lng);
    return country;
  }
}
