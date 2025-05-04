package com.noom.interview.fullstack.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SleepDataInput {
    @NotNull Instant timeInBedStart;
    @NotNull Instant timeInBedEnd;
    @NotNull Feeling feeling;
}
