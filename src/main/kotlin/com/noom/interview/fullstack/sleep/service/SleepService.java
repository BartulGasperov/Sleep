package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.AppException;
import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.model.SleepDataAverageResponse;
import com.noom.interview.fullstack.sleep.model.SleepDataInput;
import com.noom.interview.fullstack.sleep.model.SleepDataResponse;
import com.noom.interview.fullstack.sleep.persistence.entity.SleepDataEntity;
import com.noom.interview.fullstack.sleep.util.SleepDataMapper;
import com.noom.interview.fullstack.sleep.persistence.repository.SleepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SleepService {
    private final UserService userService;
    private final SleepRepository sleepRepository;

    public SleepDataResponse getLastNight(UUID userUuid){
        var user = userService.getUser(userUuid);
        var sleepDataEntity = sleepRepository.findByUserIdAndDateOfSleep(user.getId(), LocalDate.now()).orElseThrow(() -> new AppException("Last night log not found."));
        return SleepDataMapper.fromEntity(sleepDataEntity);
    }

    public SleepDataAverageResponse getAverages(UUID userUuid){
        var rangeStart = LocalDate.now().minusDays(30);
        var rangeEnd = LocalDate.now();
        var user = userService.getUser(userUuid);
        var sleepDataEntities = sleepRepository.findAllByUserIdAndDateOfSleepBetween(user.getId(), rangeStart, rangeEnd);

        if(sleepDataEntities.isEmpty()) throw new AppException("No sleep logs in last 30 days.");

        var averageTimeInBed = getAverageTimeInBed(sleepDataEntities);
        var averageTimeInBedStart = averageTimeInBedInstant(sleepDataEntities.stream()
                .map(SleepDataEntity::getTimeInBedStart).collect(Collectors.toList()));
        var averageTimeInBedEnd = averageTimeInBedInstant(sleepDataEntities.stream()
                .map(SleepDataEntity::getTimeInBedEnd).collect(Collectors.toList()));
        return new SleepDataAverageResponse(rangeStart,
                rangeEnd,
                averageTimeInBed,
                SleepDataMapper.convertToReadableTime(averageTimeInBed),
                averageTimeInBedStart,
                averageTimeInBedEnd,
                feelingFrequency(sleepDataEntities)
        );
    }

    public SleepDataResponse logSleepData(UUID userUuid, SleepDataInput sleepData){
        var user = userService.getUser(userUuid);
        if(sleepRepository.existsByUserIdAndDateOfSleep(user.getId(), LocalDate.now())) throw new AppException("Sleep log already exists for last night.");
        var entity = SleepDataMapper.toEntity(sleepData, user);
        return SleepDataMapper.fromEntity(sleepRepository.save(entity));
    }

    private Duration getAverageTimeInBed(List<SleepDataEntity> sleepDatas){
        var averageSeconds = sleepDatas.stream()
                .map(SleepDataEntity::getTimeInBed)
                .mapToLong(Duration::toSeconds)
                .average()
                .orElseThrow(() -> new AppException("Couldn't calculate average time in bed."));
        return Duration.ofSeconds(Math.round(averageSeconds));
    }

    private LocalTime averageTimeInBedInstant(List<Instant> instants) {
        double sumSin = 0;
        double sumCos = 0;

        for (Instant instant : instants) {
            LocalTime time = instant.atZone(ZoneId.of("Europe/Zagreb")).toLocalTime(); //TODO: maybe expose timezone through headers to simulate user
            double angle = 2 * Math.PI * time.toSecondOfDay() / (24 * 60 * 60);

            sumSin += Math.sin(angle);
            sumCos += Math.cos(angle);
        }

        double avgAngle = Math.atan2(sumSin / instants.size(), sumCos / instants.size());
        if (avgAngle < 0) avgAngle += 2 * Math.PI;

        int avgSeconds = (int) Math.round((avgAngle / (2 * Math.PI)) * (24 * 60 * 60));
        return LocalTime.ofSecondOfDay(avgSeconds % (24 * 60 * 60));
    }

    private Map<Feeling, Long> feelingFrequency(List<SleepDataEntity> sleepDatas) {
        return sleepDatas.stream()
                .map(SleepDataEntity::getFeeling)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
