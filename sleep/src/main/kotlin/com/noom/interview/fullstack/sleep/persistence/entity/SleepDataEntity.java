package com.noom.interview.fullstack.sleep.persistence.entity;

import com.noom.interview.fullstack.sleep.model.Feeling;
import com.noom.interview.fullstack.sleep.util.DurationConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "sleep_data")
public class SleepDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private UUID uuid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    private LocalDate dateOfSleep;
    @Convert(converter = DurationConverter.class)
    private Duration timeInBed;
    private Instant timeInBedStart;
    private Instant timeInBedEnd;
    private Feeling feeling;

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        if (dateOfSleep == null) {
            dateOfSleep = LocalDate.now();
        }
    }
}
