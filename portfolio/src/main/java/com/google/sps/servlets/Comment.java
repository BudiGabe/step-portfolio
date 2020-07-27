public class Comment {

  String message;
  float score;
  
  public Comment (String message, float score) {
      message = this.message;
      score = this.score;
  }

  public String getMessage() {
      return message;
  }

  public float getScore() {
      return score;
  }
}