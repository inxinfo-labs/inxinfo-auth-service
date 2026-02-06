package com.satishlabs.auth.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Seeds puja types and pandits on application startup when DB is empty.
 * Runs only when not in test profile.
 * NOTE: This seeder is disabled to avoid circular dependencies.
 * Move to a separate module or enable when modules are properly structured.
 */
@Component
@Order(1)
@Slf4j
@Profile("!test")
public class DataSeeder implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("DataSeeder disabled - moved to avoid circular dependencies");
        // Data seeding moved to avoid circular dependencies between modules
        // To enable: Move this to a separate seeding module or enable after module restructuring
    }
}
