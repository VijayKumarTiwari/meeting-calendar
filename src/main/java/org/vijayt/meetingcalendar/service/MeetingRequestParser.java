package org.vijayt.meetingcalendar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vijayt.meetingcalendar.model.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class MeetingRequestParser {
    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final String TIME_FORMAT = "HH:mm";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public Meeting parse(String lineWithEmpInfo, String lineWithMeetingTimeInfo) {
        String[] submissionTimeAndEmpInfo = lineWithEmpInfo.split(" ");
        String empId;
        LocalDate meetingDate;
        LocalTime meetingStart;
        LocalTime meetingEnd;
        if (submissionTimeAndEmpInfo.length == 3) {
            empId = submissionTimeAndEmpInfo[2];
        } else {
            log.error("The employee info line is not valid, {}", lineWithEmpInfo);
            throw new RuntimeException("The employee info line is not valid");
        }

        String[] meetingInfo = lineWithMeetingTimeInfo.split(" ");
        if (meetingInfo.length == 3) {
            meetingDate = parseMeetingDate(meetingInfo[0]);
            meetingStart = parseMeetingTime(meetingInfo[1]);
            meetingEnd = meetingStart.plusHours(Integer.parseInt(meetingInfo[2]));
        } else {
            log.error("Meeting info line is not valid, {}", lineWithMeetingTimeInfo);
            throw new RuntimeException("Meeting info line is not valid");
        }

        return Meeting.builder()
                .meetingDate(meetingDate)
                .meetingStart(meetingStart)
                .meetingEnd(meetingEnd)
                .requesterEmployeeId(empId)
                .build();
    }

    private LocalTime parseMeetingTime(String timeString) {
        try {
            return LocalTime.parse(timeString, timeFormatter);
        } catch (DateTimeParseException e) {
            log.error("The meeting start time format is invalid, required HH:mm found {}", timeString);
            throw e;
        }
    }

    private LocalDate parseMeetingDate(String dateString) {
        try {
            return LocalDate.parse(dateString, dateFormatter);
        } catch (DateTimeParseException e) {
            log.error("The meeting date format is invalid, required yyyy-MM-dd found {}", dateString);
            throw e;
        }
    }
}
