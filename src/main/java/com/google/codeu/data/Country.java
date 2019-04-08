package com.google.codeu.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Country {
  private String code;
  private String name;
  private double lat;
  private double lng;
  ArrayList<String> categories = new ArrayList<String>();

  public Country(String code, String name, double lat, double lng) {
    this.code = code;
    this.name = name;
    this.lat = lat;
    this.lng = lng;
    categories.add("General");
    categories.add("Food");
    categories.add("Attractions");
    categories.add("Culture");
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }
  public ArrayList<String> getCategories(){
	return categories;
  }
}
