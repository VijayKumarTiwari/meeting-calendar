package org.vijayt.meetingcalendar.service;

import org.junit.Before;
import org.junit.Test;
import org.vijayt.meetingcalendar.model.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MeetingRequestParserTest {
    private MeetingRequestParser meetingRequestParser;

    @Before
    public void setUp() {
        meetingRequestParser = new MeetingRequestParser();
    }

    @Test(expected = RuntimeException.class)
    public final void emptyStringThrowsException() {
        meetingRequestParser.parse("", "");
    }

    @Test(expected = RuntimeException.class)
    public final void lessThanThreePartsForEmpInfoLineThrowsException() {
        meetingRequestParser.parse("a", "");
    }

    @Test(expected = RuntimeException.class)
    public final void moreThanThreePartsForEmpInfoLineThrowsException() {
        meetingRequestParser.parse("a b c d", "");
    }

    @Test
    public final void thirdPartOfEmpInfoLineIsEmpId() {
        Meeting meeting = meetingRequestParser.parse("a b c", "2015-08-21 09:00 2");
        assertThat(meeting.getRequesterEmployeeId(), is("c"));
    }

    @Test(expected = RuntimeException.class)
    public final void lessThanThreePartsOfMeetingInfoLineThrowsException() {
        meetingRequestParser.parse("a b c", "a");
    }

    @Test(expected = RuntimeException.class)
    public final void moreThanThreePartsOfMeetingInfoLineThrowsException() {
        meetingRequestParser.parse("a b c", "a b c d");
    }

    @Test(expected = DateTimeParseException.class)
    public final void invalidDateFormatOfMeetingInfoLineThrowsException() {
        meetingRequestParser.parse("a b c", "2015/08/21 09:00 2");
    }

    @Test(expected = DateTimeParseException.class)
    public final void invalidTimeFormatOfMeetingInfoLineThrowsException() {
        meetingRequestParser.parse("a b c", "2015-08-21 0900 2");
    }

    @Test
    public final void validInputIsProcessedProperly() {
        Meeting meeting = meetingRequestParser.parse("a b c", "2015-08-21 09:00 2");
        assertThat(meeting.getRequesterEmployeeId(), is("c"));
        assertThat(meeting.getMeetingDate(), is(LocalDate.of(2015, 8, 21)));
        assertThat(meeting.getMeetingStart(), is(LocalTime.of(9, 0)));
        assertThat(meeting.getMeetingEnd(), is(LocalTime.of(11, 0)));
    }
}