package com.codeU.demo;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;

public class SentimentTest {

  public static void main(String[] args) throws IOException {

    // The string to test
    String text = "I love to code!";

    // Turn the string into a Document for the sentiment to process
    Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

    // Get the sentiment information of the string
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    languageService.close();

    System.out.println("Score: " + sentiment.getScore());
  }
}
