// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final Gson gson = new Gson();
  private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final int DEFAULT_MAX_COMMS = 0;
  private static final String MESSAGE = "message";
  private static final String TIMESTAMP = "timestamp";
  private static final String COMMENT = "Comment";
  /**
   * Get the list of comments from server as a Json
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query(COMMENT).addSort(TIMESTAMP, SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    int maxComms = getMaxComms(request, DEFAULT_MAX_COMMS);

    // Create a new array, otherwise there are duplicate comments
    ArrayList<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if(comments.size() == maxComms) {
          break;
      }

      String comment = (String) entity.getProperty(MESSAGE);
      comments.add(comment);
    }

    String json = gson.toJson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Let the user post their comment to the server
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getUserComment(request, "comment-input", "");
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity(COMMENT);
    commentEntity.setProperty(MESSAGE, comment);
    commentEntity.setProperty(TIMESTAMP, timestamp);
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }

  private String getUserComment(HttpServletRequest request, String commentForm, String defaultValue) {
    String comment = request.getParameter(commentForm);
    return comment == null ? defaultValue : comment;
  }

  private int getMaxComms(HttpServletRequest request, int defaultValue) {
    int maxComms = Integer.parseInt(request.getParameter("maxComms"));
    return maxComms < 0 ? defaultValue : maxComms;
  }
}