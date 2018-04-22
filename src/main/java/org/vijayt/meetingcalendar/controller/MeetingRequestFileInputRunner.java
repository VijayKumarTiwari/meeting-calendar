package org.vijayt.meetingcalendar.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.vijayt.meetingcalendar.service.MeetingCalendarService;
import org.vijayt.meetingcalendar.service.MeetingRequestFileReader;

@Controller
@Slf4j
public class MeetingRequestFileInputRunner implements CommandLineRunner {
    private final MeetingRequestFileReader meetingRequestFileReader;
    private final MeetingCalendarService meetingCalendarService;

    @Value("${inputFilePath:input.txt}")
    @Setter
    private String inputFilePath;

    @Getter
    private String output;

    public MeetingRequestFileInputRunner(MeetingRequestFileReader meetingRequestFileReader, MeetingCalendarService meetingCalendarService) {
        this.meetingRequestFileReader = meetingRequestFileReader;
        this.meetingCalendarService = meetingCalendarService;
    }

    @Override
    public void run(String... strings) throws Exception {
        try {
            meetingRequestFileReader.parseFile(inputFilePath);
            output = meetingCalendarService.getPrettyPrintMeetings();
            log.info("{}{}", System.lineSeparator(), output);
        } catch (Exception e) {
            output = e.getMessage();
            throw e;
        }

    }
}
