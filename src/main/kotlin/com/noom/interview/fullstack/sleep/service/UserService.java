package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.exception.AppException;
import com.noom.interview.fullstack.sleep.persistence.entity.UserEntity;
import com.noom.interview.fullstack.sleep.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //TODO: get only userID
    public UserEntity getUser(UUID userUuid){
        return userRepository.findByUuid(userUuid).orElseThrow(() -> new AppException(String.format("User with id: %s not found.", userUuid)));
    }
}
