/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import java.util.UUID;

public class Reply {

  private UUID replyID;
  private UUID parentID;
  private long timestamp;
  private String user;
  private String text;

  public Reply(String parentID, String user, String text) {
    this(UUID.randomUUID(), UUID.fromString(parentID), user, text, System.currentTimeMillis());
  }

  public Reply(UUID replyID, UUID parentID, String user, String text, long timestamp) {
    this.parentID = parentID;
    this.replyID = replyID;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
  }

  public String getParentId() {
    return parentID.toString();
  }

  public String getId() {
    return replyID.toString();
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
}
