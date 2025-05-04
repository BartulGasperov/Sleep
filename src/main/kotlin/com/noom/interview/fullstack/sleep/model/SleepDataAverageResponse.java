package com.noom.interview.fullstack.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class SleepDataAverageResponse {
    private LocalDate rangeStart;
    private LocalDate rangeEnd;
    private long averageTimeInBed;
    private LocalTime averageTimeInBedStart;
    private LocalTime averageTimeInBedEnd;
    private Map<Feeling, Long> feelingFrequency;
}
