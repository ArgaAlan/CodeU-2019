package com.google.codeu.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns Restaurant data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}] */
@WebServlet("/Countries")
public class MapServlet extends HttpServlet {

  JsonArray countriesArray;

  @Override
  public void init() {
    countriesArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner =
        new Scanner(getServletContext().getResourceAsStream("/WEB-INF/countries.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      String code = cells[0];
      String name = cells[1];
      double lat = Double.parseDouble(cells[2]);
      double lng = Double.parseDouble(cells[3]);

      countriesArray.add(gson.toJsonTree(new CountryMap(code, name, lat, lng)));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getOutputStream().println(countriesArray.toString());
  }

  private static class CountryMap {
    String code;
    String name;
    double lat;
    double lng;

    private CountryMap(String code, String name, double lat, double lng) {
      this.code = code;
      this.name = name;
      this.lat = lat;
      this.lng = lng;
    }
  }
}
