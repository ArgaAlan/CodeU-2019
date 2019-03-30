package com.google.codeu.data;

public class Country {
  private String code;
  private String name;
  private double lat;
  private double lng;

  public Country(String code, String name, double lat, double lng) {
    this.code = code;
    this.name = name;
    this.lat = lat;
    this.lng = lng;
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
}
