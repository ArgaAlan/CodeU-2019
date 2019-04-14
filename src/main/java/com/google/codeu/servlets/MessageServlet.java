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

package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
    datastore.addIDAllMessages();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific country. Responds
   * with an empty array if the countryCode is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");
    String countryCode = request.getParameter("countryCode");

    if (countryCode == null || countryCode.isEmpty()) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getCountryMessages(countryCode);
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
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

    // Delete message if delete parameter is present
    if ("delete".equals(request.getParameter("action"))) {
      // then refresh page
      datastore.deleteMessageWithID(request.getParameter("messageID"));
      response.sendRedirect(request.getParameter("callee"));
      return;
    }

    String text = Jsoup.clean(request.getParameter("text"), Whitelist.relaxed());
    String textWithMedia = getMediaEmbeddedText(text);

    String countryCode = request.getParameter("countryCode");
    String category = request.getParameter("category");

    // Redirect to home on invalid country
    if (datastore.getCountry(countryCode) == null) {
      response.sendRedirect("/");
      return;
    }

    if (!datastore.getCountry(countryCode).getCategories().contains(category)) {
      response.sendRedirect("/");
      return;
    }

    Message message = new Message(currentUser.getEmail(), textWithMedia, countryCode, category);
    datastore.storeMessage(message);

    response.sendRedirect("/country/" + countryCode + "/c/" + category);
  }

  private String getMediaEmbeddedText(String text) {
    String regexImage = "(https?://\\S+\\.(png|jpg|gif))";
    String replacementImage = "<img src=\"$1\" />";
    String regexVideo = "(https?://www.youtube.com/\\S+)";
    String replacementVideo = "<iframe width=\"420\" height=\"345\" src=\"$1\">" + "</iframe>";

    // Validation of URL
    Pattern patternImg = Pattern.compile(regexImage);
    Pattern patternVid = Pattern.compile(regexVideo);
    Matcher matcherImg = patternImg.matcher(text);
    Matcher matcherVid = patternVid.matcher(text);

    // Checks if the URL is valid and if it´s then it changes to insert the image
    if (matcherImg.find() && urlValidator(matcherImg.group())) {
      text = text.replaceAll(regexImage, replacementImage);
    }

    // Checks if the URL is valid and if it´s then it changes to insert the video
    if (matcherVid.find() && urlValidator(matcherVid.group())) {
      // Change the format of the normal Youtube URL to an embed one
      text = text.replace("watch?v=", "embed/");
      text = text.replaceAll(regexVideo, replacementVideo);
    }

    String regex = "(https?://\\S+\\.(png|jpg))";
    String replacement = "<img src=\"$1\" />";
    String mediaEmbeddedText = text.replaceAll(regex, replacement);

    return mediaEmbeddedText;
  }

  // Validates if an URL posted is valid
  private static boolean urlValidator(String url) {

    try {
      new URL(url).toURI();
      return true;
    } catch (URISyntaxException exception) {
      return false;
    } catch (MalformedURLException exception) {
      return false;
    }
  }
}
