package org.vijayt.meetingcalendar.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vijayt.meetingcalendar.model.Meeting;
import org.vijayt.meetingcalendar.model.OfficeTime;
import org.vijayt.meetingcalendar.repository.MeetingCalendarRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class MeetingRequestFileReader {
    private final MeetingCalendarRepository meetingCalendarRepository;
    private final MeetingCalendarService meetingCalendarService;
    private final OfficeTimingStringParser officeTimingStringParser;
    private final MeetingRequestParser meetingRequestParser;


    public void parseFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                log.error("Empty file");
                throw new RuntimeException("Empty file");
            }
            OfficeTime officeTime = officeTimingStringParser.parse(firstLine);
            meetingCalendarRepository.setOfficeStart(officeTime.getStart());
            meetingCalendarRepository.setOfficeEnd(officeTime.getEnd());

            String line1 = reader.readLine();
            while (line1 != null) {
                String line2 = reader.readLine();
                if (line2 != null) {
                    Meeting meeting = meetingRequestParser.parse(line1, line2);
                    meetingCalendarService.save(meeting);
                    line1 = reader.readLine();
                } else {
                    log.error("Invalid meeting request, meeting time is missing");
                    throw new RuntimeException("Invalid meeting request, meeting time is missing");
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }
}
