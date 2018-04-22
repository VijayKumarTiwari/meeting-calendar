package org.vijayt.meetingcalendar.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;
import org.vijayt.meetingcalendar.model.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.stream.Collectors.toList;

@Repository
public class MeetingCalendarRepository {
    private ConcurrentMap<LocalDate, ConcurrentSkipListSet<Meeting>> meetings;

    @Setter
    @Getter
    private LocalTime officeStart;

    @Setter
    @Getter
    private LocalTime officeEnd;

    public MeetingCalendarRepository() {
        meetings = new ConcurrentHashMap<>();
    }

    public void save(Meeting meeting) {
        meetings.putIfAbsent(meeting.getMeetingDate(), new ConcurrentSkipListSet<>());
        getMeetingsForDay(meeting.getMeetingDate()).add(meeting);
    }

    public SortedSet<Meeting> getMeetingsForDay(LocalDate date) {
        return meetings.getOrDefault(date, new ConcurrentSkipListSet<>());
    }

    public Optional<Meeting> getLastMeetingBefore(LocalDate date, LocalTime time) {
        return getMeetingsForDay(date).stream()
                .filter(meeting -> !meeting.getMeetingEnd().isAfter(time))
                .sorted(Comparator.comparing(Meeting::getMeetingEnd).reversed())
                .findFirst();
    }

    public Optional<Meeting> getNextMeetingAfter(LocalDate date, LocalTime time) {
        return getMeetingsForDay(date).stream()
                .filter(meeting -> !meeting.getMeetingStart().isBefore(time))
                .sorted(Comparator.comparing(Meeting::getMeetingStart))
                .findFirst();
    }

    public Optional<Meeting> getNextMeeting(Meeting meeting) {
        return Optional.ofNullable(meetings.get(meeting.getMeetingDate()).higher(meeting));
    }

    public Optional<Meeting> getPreviousMeeting(Meeting meeting) {
        return Optional.ofNullable(meetings.get(meeting.getMeetingDate()).lower(meeting));
    }

    public List<LocalDate> getSortedMeetingDates() {
        return meetings.keySet().stream().sorted(LocalDate::compareTo).collect(toList());
    }
}
