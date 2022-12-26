package com.konstde00.lab.controller;

import com.konstde00.application.Application;
import com.konstde00.auth.service.TokenService;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import com.konstde00.lab.config.TestTenantConfig;
import com.konstde00.lab.util.DatabaseContainerInitializer;
import com.konstde00.lab.util.JsonReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Slf4j
@NoArgsConstructor
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = NONE)
@FieldDefaults(level = AccessLevel.PROTECTED)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ContextConfiguration(
        initializers = DatabaseContainerInitializer.class,
        classes = Application.class
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class AbstractApiTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JsonReader jsonReader;
    @Autowired
    TokenService tokenService;

    public static final User USER = User
            .builder()
            .id(TestTenantConfig.userId)
            .roles(List.of(Role.USER))
            .build();

    public static final User ADMIN = User
            .builder()
            .id(TestTenantConfig.adminId)
            .roles(List.of(Role.ADMIN))
            .build();
}
