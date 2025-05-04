package com.noom.interview.fullstack.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SleepDataResponse {
    LocalDate dateOfSleep;
    Instant timeInBedStart;
    Instant timeInBedEnd;
    long timeInBed; // TODO: in minutes, can be more readable
    Feeling feeling;
}
