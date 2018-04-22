package org.vijayt.meetingcalendar.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
@EqualsAndHashCode(of = {"meetingDate", "meetingStart"})
@ToString
public class Meeting implements Comparable<Meeting> {
    private LocalDate meetingDate;
    private LocalTime meetingStart;
    private LocalTime meetingEnd;
    private String requesterEmployeeId;

    @Override
    public int compareTo(Meeting meeting) {
        if (meetingDate.equals(meeting.getMeetingDate())) {
            return meetingStart.compareTo(meeting.getMeetingStart());
        } else {
            return meetingDate.compareTo(meeting.getMeetingDate());
        }
    }
}
