package com.budiwebsite.model;

public class Comment {

  String message;
  double score;
  
  public Comment (String message, double score) {
      this.message = message;
      this.score = score;
  }

  public String getMessage() {
      return message;
  }

  public double getScore() {
      return score;
  }
}