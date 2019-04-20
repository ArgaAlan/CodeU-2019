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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
    // check if request is for message edit information
    // If so, reload the page with necessary information about the message
    if ("getEditable".equals(request.getParameter("action"))) {
      String messageID = request.getParameter("messageID");
      String messageText = datastore.getMessageTextByID(messageID);
      request.setAttribute("editText", messageText);
      request.setAttribute("editID", messageID);
      request.setAttribute("lat", request.getParameter("lat"));
      request.setAttribute("lng", request.getParameter("lng"));
      request.setAttribute("imageUrl", request.getParameter("imageUrl"));
      try {
        String editURL =
            "/country/"
                + request.getParameter("country")
                + "/c/"
                + request.getParameter("category");
        // Reload page with message information in form
        request.getRequestDispatcher(editURL).forward(request, response);
        return;
      } catch (Exception e) {
        System.err.println("Error in edit message - requestDispatcher failed");
        e.printStackTrace();
      }
    }
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

  /**
   * Edits a {@link Message}. Either creates/edits and stores the message, or Deletes the message
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    User currentUser = datastore.getCurrentUser();

    // redirect to home if user is not logged in
    if (currentUser == null) {
      response.sendRedirect("/");
      return;
    }

    // Delete message if delete parameter is present and refresh
    if ("delete".equals(request.getParameter("action"))) {
      datastore.deleteMessageWithID(request.getParameter("messageID"));
      response.sendRedirect(request.getParameter("callee"));
      return;
    }

    String text =
        Jsoup.clean(
            request.getParameter("text"),
            Whitelist.relaxed().addTags("oembed").addAttributes("oembed", "url"));
    String textWithMedia = getMediaEmbeddedText(text);

    String countryCode = request.getParameter("countryCode");
    String category = request.getParameter("category");

    String lat = request.getParameter("lat");
    String lng = request.getParameter("lng");

    // Redirect to home on invalid country
    if (datastore.getCountry(countryCode) == null) {
      response.sendRedirect("/");
      return;
    }

    if (!datastore.getCountry(countryCode).getCategories().contains(category)) {
      response.sendRedirect("/");
      return;
    }

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    Message message;
    if (request.getParameter("messageID") == null)
      message = new Message(currentUser.getEmail(), textWithMedia, countryCode, category, lat, lng);
    else {
      // message being edited - already has ID
      message =
          new Message(
              request.getParameter("messageID"),
              currentUser.getEmail(),
              textWithMedia,
              countryCode,
              category,
              lat,
              lng);
      if (blobKeys == null || blobKeys.isEmpty())
        message.setImageUrl(request.getParameter("imageUrl"));
    }

    try {
      if (blobKeys != null && !blobKeys.isEmpty()) {
        BlobKey blobKey = blobKeys.get(0);
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
        String imageUrl = imagesService.getServingUrl(options);
        message.setImageUrl(imageUrl);
      }
    } catch (Exception e) {
      System.err.println("Invalid image upload");
      e.printStackTrace();
    }
    datastore.storeMessage(message);

    response.sendRedirect("/country/" + countryCode + "/c/" + category);
  }

  private String getMediaEmbeddedText(String text) {
    String regexImage = "(https?://\\S+\\.(png|jpg|gif))";
    String replacementImage = "<img src=\"$1\" />";
    String regexVideo = "(https?://www.youtube.com/\\S+)";
    String replacementVideo = "<iframe width=\"960\" height=\"690\" src=\"$1\">" + "</iframe>";

    // Validation of URL
    Pattern patternImg = Pattern.compile(regexImage);
    Pattern patternVid = Pattern.compile(regexVideo);
    Matcher matcherImg = patternImg.matcher(text);
    Matcher matcherVid = patternVid.matcher(text);

    // Checks if the URL is valid and if it´s then it changes to insert the image
    if (matcherImg.find() && urlValidator(matcherImg.group())) {
      text = text.replaceAll(regexImage, replacementImage);
    }

    // && urlValidator(matcherVid.group())
    // Checks if the URL is valid and if it´s then it changes to insert the video
    if (matcherVid.find()) {
      // Change the format of the normal Youtube URL to an embed one
      text = text.replace("<oembed url=\"", "");
      text = text.replace("\"></oembed>", "");
      text = text.replace("watch?v=", "embed/");
      text = text.replaceAll(regexVideo, replacementVideo);
    }

    return text;
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
