package org.vijayt.meetingcalendar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vijayt.meetingcalendar.model.OfficeTime;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class OfficeTimingStringParser {

    private final String OFFICE_TIME_FORMAT = "HHmm";
    private final DateTimeFormatter officeTimeFormatter = DateTimeFormatter.ofPattern(OFFICE_TIME_FORMAT);

    public OfficeTime parse(String input) {
        String[] officeTimingAsStr = input.trim().split(" ");
        if (officeTimingAsStr.length == 2) {
            return OfficeTime.builder()
                    .start(parseOfficeTime(officeTimingAsStr[0]))
                    .end(parseOfficeTime(officeTimingAsStr[1]))
                    .build();
        } else {
            log.error("Office start and end time not provided, line read {}", input);
            throw new RuntimeException("Office start and end time not provided");
        }
    }

    private LocalTime parseOfficeTime(String text) {
        try {
            return LocalTime.parse(text, officeTimeFormatter);
        } catch (DateTimeParseException e) {
            log.error("The office time format is invalid required format HHmm, office ");
            throw e;
        }
    }
}
