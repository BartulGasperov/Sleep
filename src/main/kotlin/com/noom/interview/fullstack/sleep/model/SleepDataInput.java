package com.noom.interview.fullstack.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SleepDataInput {
    Instant timeInBedStart;
    Instant timeInBedEnd;
    Feeling feeling;
}
