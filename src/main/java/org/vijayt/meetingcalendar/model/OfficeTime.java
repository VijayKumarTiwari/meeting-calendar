package org.vijayt.meetingcalendar.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OfficeTime {
    private LocalTime start;
    private LocalTime end;
}
