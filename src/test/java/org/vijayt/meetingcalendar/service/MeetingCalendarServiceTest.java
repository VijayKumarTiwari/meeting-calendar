package org.vijayt.meetingcalendar.service;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vijayt.meetingcalendar.model.Meeting;
import org.vijayt.meetingcalendar.repository.MeetingCalendarRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeetingCalendarServiceTest {
    @Mock
    private MeetingCalendarRepository meetingCalendarRepository;

    @InjectMocks
    private MeetingCalendarService meetingCalendarService;

    @Test
    public final void meetingStartingBeforeOfficeStartIsNotSaved() {
        givenOfficeStart(LocalTime.now());
        Meeting meeting = buildMeeting(null, LocalTime.now().minusHours(1), null, null);
        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(false));
    }

    private Meeting buildMeeting(LocalDate meetingDate, LocalTime meetingStart, LocalTime meetingEnd, String empId) {
        return Meeting.builder()
                .meetingDate(meetingDate)
                .meetingStart(meetingStart)
                .meetingEnd(meetingEnd)
                .requesterEmployeeId(empId)
                .build();
    }

    @Test
    public final void meetingEndingAfterOfficeEndIsNotSaved() {
        givenOfficeStart(LocalTime.now().minusHours(1));
        givenOfficeEnd(LocalTime.now().plusHours(1));
        Meeting meeting = buildMeeting(null, LocalTime.now(), LocalTime.now().plusHours(2), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(false));
    }

    @Test
    public final void noLastAndNextMeetingConsidersNewMeetingTimeAsAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(2));
        givenNoLastMeeting();
        givenNoNextMeeting();

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(true));
    }

    @Test
    public final void noImmediateNextMeetingToLastConsidersNewMeetingTimeAsAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(2));
        givenLastMeeting();
        givenNoImmediateNextMeeting();

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(true));
    }

    @Test
    public final void immediateNextMeetingStartsAfterNewMeetingIsConsideredAsAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(3));
        givenLastMeeting();
        givenImmediateNextMeeting(buildMeeting(null, LocalTime.now().plusHours(2), null, null));

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(true));
    }

    @Test
    public final void immediateNextMeetingStartsBeforeNewMeetingIsConsideredAsNotAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(3));
        givenLastMeeting();
        givenImmediateNextMeeting(buildMeeting(null, LocalTime.now(), null, null));

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(false));
    }

    @Test
    public final void noImmediatePreviousMeetingToNextConsidersNewMeetingTimeAsAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(2));
        givenNoLastMeeting();
        givenNextMeeting();
        givenNoImmediatePreviousMeeting();

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(true));
    }

    @Test
    public final void immediatePreviousMeetingEndsBeforeNewMeetingIsConsideredAsAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(3));
        givenNoLastMeeting();
        givenNextMeeting();
        givenImmediatePreviousMeeting(buildMeeting(null, null, LocalTime.now().minusHours(1), null));

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(true));
    }

    @Test
    public final void immediatePreviousMeetingEndsAfterNewMeetingIsConsideredAsNotAvailable() {
        givenOfficeStart(LocalTime.now().minusHours(2));
        givenOfficeEnd(LocalTime.now().plusHours(3));
        givenNoLastMeeting();
        givenNextMeeting();
        givenImmediatePreviousMeeting(buildMeeting(null, null, LocalTime.now(), null));

        Meeting meeting = buildMeeting(LocalDate.now(), LocalTime.now().minusHours(1), LocalTime.now().plusHours(1), null);

        boolean saved = meetingCalendarService.save(meeting);
        assertThat(saved, is(false));
    }

    @Test
    public final void validatePrettyPrint() {
        LocalDate meetingDate1 = LocalDate.of(2018, 1, 1);
        LocalDate meetingDate2 = LocalDate.of(2018, 1, 2);
        givenSortedMeetingDates(meetingDate1, meetingDate2);
        givenMeetingFor(meetingDate1, buildMeeting(meetingDate1, LocalTime.of(1, 0), LocalTime.of(2, 0), "EMP1"),
                buildMeeting(meetingDate1, LocalTime.of(3, 0), LocalTime.of(4, 0), "EMP2"));

        givenMeetingFor(meetingDate2,
                buildMeeting(meetingDate2, LocalTime.of(1, 0), LocalTime.of(2, 0), "EMP3"),
                buildMeeting(meetingDate2, LocalTime.of(3, 0), LocalTime.of(4, 0), "EMP4"));

        String expectedOutput = "2018-01-01" + System.lineSeparator() + "01:00 02:00 EMP1" + System.lineSeparator() + "03:00 04:00 EMP2" + System.lineSeparator() +
                "2018-01-02" + System.lineSeparator() + "01:00 02:00 EMP3" + System.lineSeparator() + "03:00 04:00 EMP4" + System.lineSeparator();

        String prettyPrintMeetings = meetingCalendarService.getPrettyPrintMeetings();

        assertThat(prettyPrintMeetings, Is.is(expectedOutput));
    }

    private void givenMeetingFor(LocalDate forDate, Meeting... meetings) {
        SortedSet<Meeting> meetingSortedSet = new TreeSet<>();
        meetingSortedSet.addAll(Arrays.asList(meetings));
        when(meetingCalendarRepository.getMeetingsForDay(forDate)).thenReturn(Collections.unmodifiableSortedSet(meetingSortedSet));
    }

    private void givenSortedMeetingDates(LocalDate... dates) {
        when(meetingCalendarRepository.getSortedMeetingDates()).thenReturn(Collections.unmodifiableList(Arrays.asList(dates)));
    }

    private void givenImmediatePreviousMeeting(Meeting meeting) {
        when(meetingCalendarRepository.getPreviousMeeting(any(Meeting.class))).thenReturn(Optional.of(meeting));
    }

    private void givenNoImmediatePreviousMeeting() {
        when(meetingCalendarRepository.getPreviousMeeting(any(Meeting.class))).thenReturn(Optional.empty());
    }

    private void givenNextMeeting() {
        when(meetingCalendarRepository.getNextMeetingAfter(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.of(Meeting.builder().build()));
    }

    private void givenImmediateNextMeeting(Meeting meeting) {
        when(meetingCalendarRepository.getNextMeeting(any(Meeting.class))).thenReturn(Optional.of(meeting));
    }

    private void givenNoImmediateNextMeeting() {
        when(meetingCalendarRepository.getNextMeeting(any(Meeting.class))).thenReturn(Optional.empty());
    }

    private void givenLastMeeting() {
        when(meetingCalendarRepository.getLastMeetingBefore(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.of(Meeting.builder().build()));
    }

    private void givenNoNextMeeting() {
        when(meetingCalendarRepository.getNextMeetingAfter(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.empty());
    }

    private void givenNoLastMeeting() {
        when(meetingCalendarRepository.getLastMeetingBefore(any(LocalDate.class), any(LocalTime.class))).thenReturn(Optional.empty());
    }

    private void givenOfficeEnd(LocalTime officeEnd) {
        when(meetingCalendarRepository.getOfficeEnd()).thenReturn(officeEnd);
    }

    private void givenOfficeStart(LocalTime officeStart) {
        when(meetingCalendarRepository.getOfficeStart()).thenReturn(officeStart);
    }
}