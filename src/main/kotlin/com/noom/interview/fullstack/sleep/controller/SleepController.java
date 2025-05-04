package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.model.SleepDataAverageResponse;
import com.noom.interview.fullstack.sleep.model.SleepDataInput;
import com.noom.interview.fullstack.sleep.model.SleepDataResponse;
import com.noom.interview.fullstack.sleep.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/sleep")
@RequiredArgsConstructor
public class SleepController {

    private final SleepService sleepService;

    @GetMapping
    public ResponseEntity<SleepDataResponse> getLastNight(@RequestHeader(name="userId") UUID userId){
        return ResponseEntity.ok(sleepService.getLastNight(userId));
    }

    @GetMapping("/average")
    public ResponseEntity<SleepDataAverageResponse> getAverages(@RequestHeader(name="userId") UUID userId){
        return ResponseEntity.ok(sleepService.getAverages(userId));
    }

    @PostMapping
    public ResponseEntity<SleepDataResponse> createSleepDataLog(@RequestHeader(name="userId") UUID userId, @RequestBody SleepDataInput sleepData){
        return ResponseEntity.ok(sleepService.logSleepData(userId, sleepData));
    }

}
