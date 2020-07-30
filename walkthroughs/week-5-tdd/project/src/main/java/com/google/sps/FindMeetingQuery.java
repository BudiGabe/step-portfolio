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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if(events.isEmpty() && request.getDuration() <= TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }
    
    List<TimeRange> availableTimes = new ArrayList<TimeRange>();
    List<Event> eventList = new ArrayList<>(events);
    Event firstEvent = eventList.get(0);
    Event lastEvent = eventList.get(eventList.size() - 1);

    int eventsSkipped = 0;

    if(eventList.size() == 1) {
      if(!requestHasAttendees(request, firstEvent)) {
        eventsSkipped++;
      }
    }

    // Add the first time slot, from the start of the day to the start of the first event,
    // only if there's no event that starts the day.
    if(dayStartsWithEvent(eventList) && requestHasAttendees(request, firstEvent)) {
      availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY,
        eventList.get(0).getWhen().start(), false));
    }

    for (int i = 0; i < eventList.size() - 1; i++) {
      TimeRange currEventTimeRange = eventList.get(i).getWhen();
      TimeRange nextEventTimeRange = eventList.get(i + 1).getWhen();

      if(!requestHasAttendees(request, currEvent)) {
        eventsSkipped ++;
        continue;
      }

      if(currEventTimeRange.contains(nextEventTimeRange)) {
        // If there is a 3rd event, connect events 1 and 3. Otherwise, connect with the end of day.
        if(i + 2 < eventList.size() - 1) {
          TimeRange nextnextEventTimeRange = eventList.get(i + 2).getWhen();
          availableTimes.add(TimeRange.fromStartEnd(currEventTimeRange.end(),
            nextnextEventTimeRange.start(), true));
          i++;
        } else {
          availableTimes.add(TimeRange.fromStartEnd(currEventTimeRange.end(),
            TimeRange.END_OF_DAY, true));
        }
      } else {
          // If the current event and next one overlap, just skip that time slot.
          if(currEventTimeRange.overlaps(nextEventTimeRange)) {
            continue;
          }

          // Connect the end of current event with the start of the next event
          // only if the request fits.
          if(nextEventTimeRange.start() - currEventTimeRange.end() >= request.getDuration()) {
            availableTimes.add(TimeRange.fromStartEnd(currEventTimeRange.end(),
              nextEventTimeRange.start(), false));  
          } 
        }
    }

    if(requestHasAttendees(request, lastEvent)) {
      if(availableTimes.size() != 0){
        if(nothingEndsTheDay(availableTimes, eventList)) {
          availableTimes.add(TimeRange.fromStartEnd(eventList.get(eventList.size() - 1).getWhen().end(),
            TimeRange.END_OF_DAY, true));
        } 
      } else {
          if(fitsOnlyAtTheEnd(request, availableTimes, eventList)) {
              availableTimes.add(TimeRange.fromStartEnd(eventList.get(eventList.size() - 1).getWhen().end(),
                TimeRange.END_OF_DAY, true));
            }
        } 
    } 

    if(eventsSkipped == eventList.size()) {
      availableTimes.add(TimeRange.WHOLE_DAY);
    }

      return availableTimes;
  }

  private boolean requestHasAttendees(MeetingRequest request, Event currEvent) {
    List<String> requestAttendees = new ArrayList<>(request.getAttendees());
    List<String> eventAttendees = new ArrayList<>(currEvent.getAttendees());

    List<String> commonAttendees = requestAttendees.stream()
      .filter(eventAttendees::contains)
      .collect(Collectors.toList());
    
    return !commonAttendees.isEmpty();
  }

  private boolean dayStartsWithEvent(List<Event> eventList) {
    return eventList.get(0).getWhen().start() != TimeRange.START_OF_DAY;
  }

  private boolean nothingEndsTheDay(List<TimeRange> availableTimes, List<Event> eventList) {
    return (availableTimes.get(availableTimes.size() - 1).end() != TimeRange.END_OF_DAY + 1) &&
      (eventList.get(eventList.size() - 1).getWhen().end() != TimeRange.END_OF_DAY + 1);
  }

  private boolean fitsOnlyAtTheEnd(MeetingRequest request, List<TimeRange> availableTimes,
    List<Event> eventList) {
    return request.getDuration() < TimeRange.END_OF_DAY - eventList.get(eventList.size() - 1)
      .getWhen().end();
  }
}