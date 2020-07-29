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

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    if(events.isEmpty() && request.getDuration() <= TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }
    
    List<TimeRange> availableTimes = new ArrayList<TimeRange>();

    //Store in ArrayList for easier access to elements
    List<Event> eventList = new ArrayList<>(events);

    //Add the first time slot, from the start of the day to the start of the first event.
    availableTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY,
      eventList.get(0).getWhen().start(), false));

    for (int i = 0; i < eventList.size() - 1; i++) {
      // If our event fully contains the next one, ignore the next event and connect events 1 and 3
      if(eventList.get(i).getWhen().contains(eventList.get(i + 1).getWhen())) {
        // Check if there are any events left. Otherwise, connect with the end of day
        if(i + 2 < eventList.size() - 1) {
          availableTimes.add(TimeRange.fromStartEnd(eventList.get(i).getWhen().end(),
            eventList.get(i + 2).getWhen().start(), true));
          i++;
        } else {
          availableTimes.add(TimeRange.fromStartEnd(eventList.get(i).getWhen().end(),
            TimeRange.END_OF_DAY, true));
        }
      } else {
          //If the current event and next one overlap, just skip that time slot
          if(eventList.get(i).getWhen().overlaps(eventList.get(i + 1).getWhen())) {
            continue;
          }

          //Connect the end of current event with the start of the next event
          availableTimes.add(TimeRange.fromStartEnd(eventList.get(i).getWhen().end(),
            eventList.get(i + 1).getWhen().start(), false));   
        }
    }

    //Add the last time slot, from the end of the last event, to the end of the day.
    if(availableTimes.get(availableTimes.size() - 1).end() != TimeRange.END_OF_DAY + 1) {
      availableTimes.add(TimeRange.fromStartEnd(eventList.get(eventList.size() - 1).getWhen().end(),
        TimeRange.END_OF_DAY, true));
    }

    return availableTimes;
  }
}