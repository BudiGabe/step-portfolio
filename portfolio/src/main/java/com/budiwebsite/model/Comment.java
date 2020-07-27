package com.budiwebsite.model;

/**
 * Class stores comments inputted with their user along with their morality rating.
 * This allows for easier conversion from list of objects to JSON.
 */
public class Comment {

  private final String message;
  private final double score;
  
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