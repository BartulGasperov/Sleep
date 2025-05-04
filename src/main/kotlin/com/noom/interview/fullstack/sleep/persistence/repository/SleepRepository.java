package com.noom.interview.fullstack.sleep.persistence.repository;

import com.noom.interview.fullstack.sleep.persistence.entity.SleepDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SleepRepository extends JpaRepository<SleepDataEntity, Integer> {
    Optional<SleepDataEntity> findByUserIdAndDateOfSleep(int userId, LocalDate dateOfSleep);
    List<SleepDataEntity> findAllByUserIdAndDateOfSleepBetween(int userId, LocalDate startDate, LocalDate endDate);
}
