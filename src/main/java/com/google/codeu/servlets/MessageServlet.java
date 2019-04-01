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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
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
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
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

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Only allow users to post messages if they are logged in
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.relaxed());
    String textWithMedia = getMediaEmbeddedText(text);

    String countryCode = request.getParameter("countryCode");

    // Redirect to home on invalid country
    if (datastore.getCountry(countryCode) == null) {
      response.sendRedirect("/");
      return;
    }
    float sentimentScore = this.getSentimentScore(text);

    Message message = new Message(user, textWithMedia, countryCode, sentimentScore);
    datastore.storeMessage(message);

    response.sendRedirect("/country/" + countryCode);
  }

  /**
   * Takes a string (text) and analyzes the sentiment within it using Google Cloud's
   * LanguageServiceClient Returns this sentiment as a float
   */
  private float getSentimentScore(String text) throws IOException {
    float score = 0.0f;
    try {
      Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

      LanguageServiceClient languageService = LanguageServiceClient.create();
      Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
      languageService.close();

      score = sentiment.getScore();
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }

    return score;
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
