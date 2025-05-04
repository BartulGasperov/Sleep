package com.noom.interview.fullstack.sleep.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sleep_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private UUID uuid;
    private String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<SleepDataEntity> sleepDatas;
}
