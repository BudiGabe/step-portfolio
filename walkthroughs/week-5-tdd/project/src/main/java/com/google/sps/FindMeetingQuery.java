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

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if (events.isEmpty() && request.getDuration() <= TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Collections.emptyList();
    }
    
    // First we do the check with ALL our attendees.
    List<Event> eventListWithAllAttendees = getEventsWithRequestAttendees(request, events, true);

    if (eventListWithAllAttendees.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
 
    List<TimeRange> availableTimesForAll = new ArrayList<TimeRange>();

    handleBeginningOfDay(availableTimesForAll, eventListWithAllAttendees, request);
    handleMiddleOfDay(availableTimesForAll, eventListWithAllAttendees, request);
    handleEndOfDay(availableTimesForAll, eventListWithAllAttendees, request);

    if (!availableTimesForAll.isEmpty()) {
      return availableTimesForAll;
    }

    // If no time was found for all attendees, then we go and check again only with mandatory ones.
    List<Event> eventListWithMandatoryAttendees = getEventsWithRequestAttendees(request, events, false);
    List<TimeRange> availableTimesForMandatory = new ArrayList<TimeRange>();

    if (eventListWithMandatoryAttendees.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    handleBeginningOfDay(availableTimesForMandatory, eventListWithMandatoryAttendees, request);
    handleMiddleOfDay(availableTimesForMandatory, eventListWithMandatoryAttendees, request);
    handleEndOfDay(availableTimesForMandatory, eventListWithMandatoryAttendees, request);

    return availableTimesForMandatory;
  }

  private static boolean requestHasEventAttendees(MeetingRequest request, Event currEvent,
    boolean addAllAttendees) {
    List<String> requestAttendees = new ArrayList<>(request.getAttendees());
    List<String> eventAttendees = new ArrayList<>(currEvent.getAttendees());
    if (addAllAttendees) {
      requestAttendees.addAll(request.getOptionalAttendees());
    }

    return requestAttendees.stream().anyMatch(eventAttendees::contains);
  }

  /**
   * Check if the first event in our list already includes the start of the day.
   */ 
  private static boolean startOfDayIsFree(List<Event> eventList) {
    return eventList.get(0).getWhen().start() != TimeRange.START_OF_DAY;
  }

  /**
   * Check if there are no available times already added in our list which contain the end of day
   * and any events that end the day.
   */ 
  private static boolean nothingEndsTheDay(List<TimeRange> availableTimes, List<Event> eventList) {
    int endOfDay = TimeRange.WHOLE_DAY.duration();

    return (availableTimes.get(availableTimes.size() - 1).end() != endOfDay) &&
      (eventList.get(eventList.size() - 1).getWhen().end() != endOfDay);
  }

  /**
   * Check if the duration of our request fits between the end of the day and the end of the last event.
   */ 
  private static boolean fitsOnlyAtTheEnd(MeetingRequest request, List<TimeRange> availableTimes,
    List<Event> eventList) {
    return request.getDuration() <= TimeRange.END_OF_DAY - eventList.get(eventList.size() - 1)
      .getWhen().end();
  }

  private static List getEventsWithRequestAttendees(MeetingRequest request, Collection<Event> events,
    boolean addAllAttendees) {
    return events.stream().filter(event -> requestHasEventAttendees(request, event, addAllAttendees))
      .collect(Collectors.toList());
  }

  private static boolean eventsOverlap(TimeRange currEventTimeRange,
    TimeRange nextEventTimeRange){
    return currEventTimeRange.overlaps(nextEventTimeRange);
  }

  /**
   * Check if our request fits between the end of current event and the start of next event.
   */
  private static boolean requestFits(MeetingRequest request, int nextEventStart, int currEventEnd) {
    return request.getDuration() <= nextEventStart - currEventEnd;
  }

  private static void handleBeginningOfDay(List<TimeRange> availableTimes, List<Event> eventList,
    MeetingRequest request) {
    Collections.sort(eventList, Event.ORDER_BY_START);
    TimeRange firstEventTimeRange = eventList.get(0).getWhen();
    
    // Add the first time slot, from the start of the day to the start of the first event,
    // only if there's no event that starts the day.
    if (startOfDayIsFree(eventList) && requestFits(request, firstEventTimeRange.start(), TimeRange.START_OF_DAY)) {
      availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY,
        firstEventTimeRange.start(), false));
    }
  }
  
  /**
   * Go through each event and check if it overlaps with another. If it does, we connect
   * the end of our event with the start of the next one that is not overlapped. Otherwise,
   * just connect the 2 events to create a time slot, if the request fits.
   */
  private static void handleMiddleOfDay(List<TimeRange> availableTimes, List<Event> eventList,
    MeetingRequest request) {
    for (int i = 0; i < eventList.size() - 1; i++) {
      TimeRange currEventTimeRange = eventList.get(i).getWhen();
      TimeRange nextEventTimeRange = eventList.get(i + 1).getWhen();

      if (eventsOverlap(currEventTimeRange, nextEventTimeRange)){
        removeOverlappedEvents(i, eventList, currEventTimeRange); 
      }

      // The next event can be different from the one above, if it was overlapped
      TimeRange actualNextEventTimeRange = eventList.get(i + 1).getWhen();
      if (requestFits(request, actualNextEventTimeRange.start(), currEventTimeRange.end())) {
        availableTimes.add(TimeRange.fromStartEnd(currEventTimeRange.end(),
          nextEventTimeRange.start(), false));  
      }
    }
  }

  /**
   * Method links the end of the last event to the end of the day.
   * If there already are some available time slots added, we must check if nothing ends the day already
   * If nothing was added yet, maybe our request fits only at the end of the day
   */
  private static void handleEndOfDay(List<TimeRange> availableTimes, List<Event> eventList,
    MeetingRequest request) {
    // Sort them again to be able to get the last end. Might be not that efficient.
    Collections.sort(eventList, Event.ORDER_BY_END);
    TimeRange lastEventTimeRange = eventList.get(eventList.size() - 1).getWhen();
    
    // Handle the case where the meeting can take place at the end of the day
    if (!availableTimes.isEmpty() && requestFits(request, TimeRange.END_OF_DAY, lastEventTimeRange.end())) {
      if (nothingEndsTheDay(availableTimes, eventList)) {
        availableTimes.add(TimeRange.fromStartEnd(lastEventTimeRange.end(),
          TimeRange.END_OF_DAY, true));
      } 
    } else if (fitsOnlyAtTheEnd(request, availableTimes, eventList)) {
        availableTimes.add(TimeRange.fromStartEnd(lastEventTimeRange.end(),
          TimeRange.END_OF_DAY, true));
      }
  }

  private static void removeOverlappedEvents(int currEventPosition, List<Event> eventList,
    TimeRange currEventTimeRange) {
    for (int j = currEventPosition + 2; j < eventList.size(); j++) {
      if (eventsOverlap(currEventTimeRange, eventList.get(j).getWhen())) {
        eventList.remove(j);
      }
    }
  }
}