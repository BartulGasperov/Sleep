package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.model.SleepDataAverageResponse;
import com.noom.interview.fullstack.sleep.model.SleepDataInput;
import com.noom.interview.fullstack.sleep.model.SleepDataResponse;
import com.noom.interview.fullstack.sleep.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sleep")
@RequiredArgsConstructor
public class SleepController {

    private final SleepService sleepService;

    @GetMapping
    public ResponseEntity<SleepDataResponse> getLastNight(@NotNull @RequestHeader(name="userId") UUID userId){
        return ResponseEntity.ok(sleepService.getLastNight(userId));
    }

    @GetMapping("/average")
    public ResponseEntity<SleepDataAverageResponse> getAverages(@NotNull @RequestHeader(name="userId") UUID userId){
        return ResponseEntity.ok(sleepService.getAverages(userId));
    }

    @PostMapping
    public ResponseEntity<SleepDataResponse> createSleepDataLog(@NotNull @RequestHeader(name="userId") UUID userId, @NotNull @RequestBody SleepDataInput sleepData){
        return ResponseEntity.ok(sleepService.logSleepData(userId, sleepData));
    }

}
