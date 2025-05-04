package com.noom.interview.fullstack.sleep.util;

import com.noom.interview.fullstack.sleep.model.SleepDataInput;
import com.noom.interview.fullstack.sleep.model.SleepDataResponse;
import com.noom.interview.fullstack.sleep.persistence.entity.SleepDataEntity;
import com.noom.interview.fullstack.sleep.persistence.entity.UserEntity;

import java.time.Duration;

public class SleepDataMapper {

    public static SleepDataEntity toEntity(SleepDataInput sleepData, UserEntity user){
        return SleepDataEntity.builder()
                .timeInBedStart(sleepData.getTimeInBedStart())
                .timeInBedEnd(sleepData.getTimeInBedEnd())
                .timeInBed(Duration.between(sleepData.getTimeInBedStart(), sleepData.getTimeInBedEnd()))
                .user(user)
                .feeling(sleepData.getFeeling())
                .build();
    }

    public static SleepDataResponse fromEntity(SleepDataEntity entity){
        return new SleepDataResponse(entity.getDateOfSleep(), entity.getTimeInBedStart(), entity.getTimeInBedEnd(), entity.getTimeInBed().toMinutes(), entity.getFeeling());
    }
}
