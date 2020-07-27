package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/genre-chart")
public class GenreChartServlet extends HttpServlet {
  private Map<String, Integer> genreVotes = new HashMap<>();
  private final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String json = gson.toJson(genreVotes);
    response.getWriter().println(json);
  }

   @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String genre = request.getParameter("genre");
    int currentVotes = getCurrentVotes(genreVotes, genre);
    genreVotes.put(genre, currentVotes + 1);

    response.sendRedirect("/genre-chart.html");
  }

  public int getCurrentVotes(Hashmap<String, Integer> genreVotes, String genre) {
      return genreVotes.containsKey(genre) ? genreVotes.get(genre) : 0;
  }
}