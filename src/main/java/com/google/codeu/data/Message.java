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

import java.util.UUID;

/** A single message posted by a user. */
public class Message {

  private UUID id;
  private String user; // author of message
  private String text;
  private long timestamp;
  private String country;
  private float sentimentScore;
  private String imageUrl;

  /**
   * Constructs a new {@link Message} posted by {@code user} to {@code recipient} with {@code text}
   * content and {@code sentimentScore}. Generates a random ID and uses the current system time for
   * the creation time.
   */
  public Message(String user, String text, String country, float sentimentScore) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), country, sentimentScore);
  }

  public Message(
      UUID id, String user, String text, long timestamp, String country, float sentimentScore) {
    this.id = id;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
    this.country = country;
    this.sentimentScore = sentimentScore;
    this.imageUrl = null;
  }

  public String getCountry() {
    return country;
  }

  public UUID getId() {
    return id;
  }

  public String getUser() {
    return user;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public float getSentimentScore() {
    return sentimentScore;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String newImageUrl) {
    imageUrl = newImageUrl;
  }
}
