package com.konstde00.auth.service;

import com.konstde00.auth.repository.UserRepository;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public User getByEmail(String email) {

        log.info("'getByEmail' invoked with email - {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(format("User with email - %s does not exist. ", email));
                    return new ResourceNotFoundException(format("User with email - %s does not exist. ", email));
                });

        log.info("'getByEmail' returned 'Success'");

        return user;
    }
}
