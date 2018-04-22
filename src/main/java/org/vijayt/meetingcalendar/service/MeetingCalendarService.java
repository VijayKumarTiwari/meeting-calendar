package org.vijayt.meetingcalendar.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vijayt.meetingcalendar.model.Meeting;
import org.vijayt.meetingcalendar.repository.MeetingCalendarRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

@Service
@AllArgsConstructor
public class MeetingCalendarService {
    private final MeetingCalendarRepository meetingCalendarRepository;
    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final String TIME_FORMAT = "HH:mm";
    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public boolean save(Meeting meeting) {
        if (isMeetingWithinOfficeHours(meeting) && isMeetingTimeAvailable(meeting)) {
            meetingCalendarRepository.save(meeting);
            return true;
        } else {
            return false;
        }
    }

    private boolean isMeetingTimeAvailable(Meeting meeting) {
        boolean meetingTimeAvailable = true;
        Optional<Meeting> lastMeeting = meetingCalendarRepository.getLastMeetingBefore(meeting.getMeetingDate(), meeting.getMeetingStart());
        if (lastMeeting.isPresent()) {
            Optional<Meeting> immediateNextMeeting = meetingCalendarRepository.getNextMeeting(meeting);
            meetingTimeAvailable = immediateNextMeeting.map(meeting1 -> meeting1.getMeetingStart().isAfter(meeting.getMeetingEnd())).orElse(true);
        } else {
            Optional<Meeting> nextMeeting = meetingCalendarRepository.getNextMeetingAfter(meeting.getMeetingDate(), meeting.getMeetingEnd());
            if (nextMeeting.isPresent()) {
                Optional<Meeting> immediatePreviousMeeting = meetingCalendarRepository.getPreviousMeeting(meeting);
                meetingTimeAvailable = immediatePreviousMeeting.map(meeting1 -> meeting1.getMeetingEnd().isBefore(meeting.getMeetingStart())).orElse(true);
            }
        }
        return meetingTimeAvailable;
    }

    private boolean isMeetingWithinOfficeHours(Meeting meeting) {
        return !meeting.getMeetingStart().isBefore(meetingCalendarRepository.getOfficeStart()) &&
                !meeting.getMeetingEnd().isAfter(meetingCalendarRepository.getOfficeEnd());
    }

    public String getPrettyPrintMeetings() {
        StringBuilder meetingsPrint = new StringBuilder();
        List<LocalDate> meetingDatesInOrder = meetingCalendarRepository.getSortedMeetingDates();
        meetingDatesInOrder.forEach(meetingDate -> {
            meetingsPrint.append(dateFormatter.format(meetingDate));
            meetingsPrint.append(System.lineSeparator());
            SortedSet<Meeting> meetingsForDate = meetingCalendarRepository.getMeetingsForDay(meetingDate);
            meetingsForDate.forEach(meeting -> {
                meetingsPrint.append(timeFormatter.format(meeting.getMeetingStart()));
                meetingsPrint.append(" ");
                meetingsPrint.append(timeFormatter.format(meeting.getMeetingEnd()));
                meetingsPrint.append(" ");
                meetingsPrint.append(meeting.getRequesterEmployeeId());
                meetingsPrint.append(System.lineSeparator());
            });
        });
        return meetingsPrint.toString();
    }
}
