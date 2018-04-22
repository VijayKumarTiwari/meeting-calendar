package org.vijayt.meetingcalendar.repository;

import org.junit.Before;
import org.junit.Test;
import org.vijayt.meetingcalendar.model.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

public class MeetingCalendarRepositoryTest {
    private MeetingCalendarRepository meetingCalendarRepository;

    @Before
    public void setUp() {
        meetingCalendarRepository = new MeetingCalendarRepository();
    }

    @Test
    public final void meetingsAddedForSameDateAreOrderedByStartTime() {
        Meeting meeting1 = buildMeeting(LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(2, 0), null);
        Meeting meeting2 = buildMeeting(LocalDate.now(), LocalTime.of(3, 0), LocalTime.of(4, 0), null);
        meetingCalendarRepository.save(meeting2);
        meetingCalendarRepository.save(meeting1);

        assertThat(meetingCalendarRepository.getSortedMeetingDates(), contains(LocalDate.now()));
        assertThat(meetingCalendarRepository.getMeetingsForDay(LocalDate.now()), contains(meeting1, meeting2));
    }

    @Test
    public final void lastMeetingIsFetchedProperly() {
        Meeting meeting1 = buildMeeting(LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(2, 0), null);
        Meeting meeting2 = buildMeeting(LocalDate.now(), LocalTime.of(3, 0), LocalTime.of(4, 0), null);
        meetingCalendarRepository.save(meeting2);
        meetingCalendarRepository.save(meeting1);

        Optional<Meeting> lastMeeting = meetingCalendarRepository.getLastMeetingBefore(LocalDate.now(), LocalTime.of(2, 0));
        assertThat(lastMeeting.isPresent(), is(true));
        assertThat(lastMeeting.get(), is(meeting1));

        lastMeeting = meetingCalendarRepository.getLastMeetingBefore(LocalDate.now(), LocalTime.of(1, 0));
        assertThat(lastMeeting.isPresent(), is(false));
    }

    @Test
    public final void nextMeetingIsFetchedProperly() {
        Meeting meeting1 = buildMeeting(LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(2, 0), null);
        Meeting meeting2 = buildMeeting(LocalDate.now(), LocalTime.of(3, 0), LocalTime.of(4, 0), null);
        meetingCalendarRepository.save(meeting2);
        meetingCalendarRepository.save(meeting1);

        Optional<Meeting> nextMeeting = meetingCalendarRepository.getNextMeetingAfter(LocalDate.now(), LocalTime.of(3, 0));
        assertThat(nextMeeting.isPresent(), is(true));
        assertThat(nextMeeting.get(), is(meeting2));

        nextMeeting = meetingCalendarRepository.getNextMeetingAfter(LocalDate.now(), LocalTime.of(4, 0));
        assertThat(nextMeeting.isPresent(), is(false));
    }

    @Test
    public final void immediateNextMeetingIsFetchedProperly() {
        Meeting meeting1 = buildMeeting(LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(2, 0), null);
        Meeting meeting2 = buildMeeting(LocalDate.now(), LocalTime.of(3, 0), LocalTime.of(4, 0), null);
        meetingCalendarRepository.save(meeting2);
        meetingCalendarRepository.save(meeting1);

        Optional<Meeting> nextBookedMeeting = meetingCalendarRepository.getNextMeeting(meeting1);
        assertThat(nextBookedMeeting.isPresent(), is(true));
        assertThat(nextBookedMeeting.get(), is(meeting2));
    }

    @Test
    public final void immediatePrevMeetingIsFetchedProperly() {
        Meeting meeting1 = buildMeeting(LocalDate.now(), LocalTime.of(1, 0), LocalTime.of(2, 0), null);
        Meeting meeting2 = buildMeeting(LocalDate.now(), LocalTime.of(3, 0), LocalTime.of(4, 0), null);
        meetingCalendarRepository.save(meeting2);
        meetingCalendarRepository.save(meeting1);

        Optional<Meeting> nextBookedMeeting = meetingCalendarRepository.getPreviousMeeting(meeting2);
        assertThat(nextBookedMeeting.isPresent(), is(true));
        assertThat(nextBookedMeeting.get(), is(meeting1));
    }

    private Meeting buildMeeting(LocalDate meetingDate, LocalTime meetingStart, LocalTime meetingEnd, String empId) {
        return Meeting.builder()
                .meetingDate(meetingDate)
                .meetingStart(meetingStart)
                .meetingEnd(meetingEnd)
                .requesterEmployeeId(empId)
                .build();
    }
}