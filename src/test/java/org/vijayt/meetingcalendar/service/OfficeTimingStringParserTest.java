package org.vijayt.meetingcalendar.service;

import org.junit.Before;
import org.junit.Test;
import org.vijayt.meetingcalendar.model.OfficeTime;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OfficeTimingStringParserTest {
    private OfficeTimingStringParser officeTimingStringParser;

    @Before
    public void setUp() {
        officeTimingStringParser = new OfficeTimingStringParser();
    }

    @Test(expected = RuntimeException.class)
    public final void emptyStringThrowsException() {
        officeTimingStringParser.parse("");
    }

    @Test(expected = RuntimeException.class)
    public final void lessThanTwoStringPartsThrowsException() {
        officeTimingStringParser.parse("a");
    }

    @Test(expected = RuntimeException.class)
    public final void moreThanTwoStringPartsThrowsException() {
        officeTimingStringParser.parse("a b c");
    }

    @Test(expected = DateTimeParseException.class)
    public final void invalidTimeFormatThrowsException() {
        officeTimingStringParser.parse("9 10:03");
    }

    @Test
    public final void validTimeFormatIsHandledProperly() {
        OfficeTime officeTime = officeTimingStringParser.parse("0900 1003");
        assertThat(officeTime.getStart(), is(LocalTime.of(9, 0)));
        assertThat(officeTime.getEnd(), is(LocalTime.of(10, 3)));
    }
}