package com.noom.interview.fullstack.sleep;

import com.noom.interview.fullstack.sleep.exception.AppException;
import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.model.SleepDataAverageResponse;
import com.noom.interview.fullstack.sleep.model.SleepDataInput;
import com.noom.interview.fullstack.sleep.persistence.entity.SleepDataEntity;
import com.noom.interview.fullstack.sleep.persistence.entity.UserEntity;
import com.noom.interview.fullstack.sleep.persistence.repository.SleepRepository;
import com.noom.interview.fullstack.sleep.service.SleepService;
import com.noom.interview.fullstack.sleep.service.UserService;
import com.noom.interview.fullstack.sleep.util.SleepDataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class SleepServiceUnitTest {

    @Mock
    SleepRepository sleepRepository;

    @Mock
    UserService userService;

    @InjectMocks
    SleepService sleepService;

    @Test
    void shouldCreateSleepDataLog(){
        // given
        var givenRequest = new SleepDataInput(Instant.now().minus(Duration.ofHours(8)), Instant.now(), Feeling.GOOD);
        var givenEntity = givenSleepDataEntity(givenRequest);
        var givenUser = givenUser();
        given(userService.getUser(givenUser.getUuid())).willReturn(givenUser);
        given(sleepRepository.save(any())).willReturn(givenEntity);

        // when
        var actual = sleepService.logSleepData(givenUser.getUuid(), givenRequest);

        // then
        assertNotNull(actual);
        assertEquals(SleepDataMapper.fromEntity(givenEntity), actual);
    }

    @Test
    void shouldThrowOnInvalidUser(){
        // given
        var givenRequest = givenSleepDataInput();
        var givenUser = givenUser();
        given(userService.getUser(givenUser.getUuid())).willThrow(new AppException("Sleep log already exists for last night."));

        // when
        var actual = assertThrows(AppException.class, () -> sleepService.logSleepData(givenUser.getUuid(), givenRequest));

        // then
        assertEquals("Sleep log already exists for last night.", actual.getMessage());
    }

    @Test
    void shouldReturnLastNightSleepData() {
        // given
        var givenUser = givenUser();
        var givenEntity = givenSleepDataEntity(givenSleepDataInput());
        given(userService.getUser(givenUser.getUuid())).willReturn(givenUser);
        given(sleepRepository.findByUserIdAndDateOfSleep(givenUser.getId(), LocalDate.now()))
                .willReturn(Optional.of(givenEntity));

        // when
        var actual = sleepService.getLastNight(givenUser.getUuid());

        // then
        assertNotNull(actual);
        assertEquals(SleepDataMapper.fromEntity(givenEntity), actual);
    }

    @Test
    void shouldThrowWhenLastNightSleepDataNotFound() {
        // given
        var givenUser = givenUser();
        given(userService.getUser(givenUser.getUuid())).willReturn(givenUser);
        given(sleepRepository.findByUserIdAndDateOfSleep(givenUser.getId(), LocalDate.now()))
                .willReturn(Optional.empty());

        // when
        var exception = assertThrows(AppException.class, () -> sleepService.getLastNight(givenUser.getUuid()));

        // then
        assertEquals("Last night log not found.", exception.getMessage());
    }

    @Test
    void shouldReturnSleepAverages() {
        // given
        var givenUser = givenUser();
        var sleepEntities = List.of(
                givenSleepDataEntity(givenSleepDataInput(Instant.parse("2025-05-04T22:32:00Z"), Instant.parse("2025-05-05T08:01:00Z"))),
                givenSleepDataEntity(givenSleepDataInput(Instant.parse("2025-05-04T23:02:00Z"), Instant.parse("2025-05-05T07:15:00Z")))
        );
        var rangeStart = LocalDate.now().minusDays(30);
        var rangeEnd = LocalDate.now();

        given(userService.getUser(givenUser.getUuid())).willReturn(givenUser);
        given(sleepRepository.findAllByUserIdAndDateOfSleepBetween(givenUser.getId(), rangeStart, rangeEnd))
                .willReturn(sleepEntities);

        // when
        var actual = sleepService.getAverages(givenUser.getUuid());

        // then
        assertNotNull(actual);
        assertEquals(new SleepDataAverageResponse(rangeStart,
                        rangeEnd,
                        Duration.ofMinutes(531),
                        "8 hours, 51 minutes",
                        LocalTime.of(0,47),
                        LocalTime.of(9, 38),
                        Map.of(Feeling.GOOD, 2L))
                    , actual);

    }

    private UserEntity givenUser(){
        var user = new UserEntity();
        user.setId(1);
        user.setUuid(UUID.randomUUID());
        user.setName("givenName");
        return user;
    }

    private SleepDataInput givenSleepDataInput(Instant timeInBedStart, Instant timeInBedEnd){
        return new SleepDataInput(timeInBedStart, timeInBedEnd, Feeling.GOOD);
    }

    private SleepDataInput givenSleepDataInput(){
        return givenSleepDataInput(Instant.now().minus(Duration.ofHours(8)), Instant.now());
    }

    private SleepDataEntity givenSleepDataEntity(SleepDataInput input){
        return SleepDataEntity.builder()
                .id(1)
                .uuid(UUID.randomUUID())
                .user(givenUser())
                .dateOfSleep(LocalDate.now())
                .timeInBed(Duration.between(input.getTimeInBedStart(), input.getTimeInBedEnd()))
                .timeInBedStart(input.getTimeInBedStart())
                .timeInBedEnd(input.getTimeInBedEnd())
                .feeling(input.getFeeling())
                .build();

    }
}
