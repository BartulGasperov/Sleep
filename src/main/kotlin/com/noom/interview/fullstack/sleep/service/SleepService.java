package com.noom.interview.fullstack.sleep.service;

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
        var sleepDataEntity = sleepRepository.findByUserIdAndDateOfSleep(user.getId(), LocalDate.now()).orElseThrow(); //TODO: handle exception
        return SleepDataMapper.fromEntity(sleepDataEntity);
    }

    public SleepDataAverageResponse getAverages(UUID userUuid){
        var rangeStart = LocalDate.now().minusDays(30);
        var rangeEnd = LocalDate.now();
        var user = userService.getUser(userUuid);
        var sleepDataEntities = sleepRepository.findAllByUserIdAndDateOfSleepBetween(user.getId(), rangeStart, rangeEnd); // TODO: handle enpty list
        var averageTimeInBedStart = averageTimeInBedInstant(sleepDataEntities.stream()
                .map(SleepDataEntity::getTimeInBedStart).collect(Collectors.toList()));
        var averageTimeInBedEnd = averageTimeInBedInstant(sleepDataEntities.stream()
                .map(SleepDataEntity::getTimeInBedEnd).collect(Collectors.toList()));
        return new SleepDataAverageResponse(rangeStart,
                rangeEnd,
                getAverageTimeInBed(sleepDataEntities),
                averageTimeInBedStart,
                averageTimeInBedEnd,
                feelingFrequency(sleepDataEntities)
        );
    }

    public SleepDataResponse logSleepData(UUID userUuid, SleepDataInput sleepData){
        // TODO: What if already exist?
        var entity = SleepDataMapper.toEntity(sleepData, userService.getUser(userUuid));
        return SleepDataMapper.fromEntity(sleepRepository.save(entity));
    }

    private long getAverageTimeInBed(List<SleepDataEntity> sleepDatas){
        var average = sleepDatas.stream()
                .map(SleepDataEntity::getTimeInBed)
                .mapToLong(Duration::toSeconds)
                .average(); //TODO: handle exception
        return (long) average.orElse(0);
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
