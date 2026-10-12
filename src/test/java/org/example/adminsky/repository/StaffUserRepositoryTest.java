package org.example.adminsky.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;
import org.example.adminsky.entity.StaffUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
class StaffUserRepositoryTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired StaffUserRepository users;
    @Autowired StaffRoleRepository roles;

    @Test
    void loadsRolesWithUsernameLookup() {
        var admin = roles.findByCode("ADMIN").orElseThrow();
        users.saveAndFlush(StaffUser.builder().username("test.admin").passwordHash("hash")
                .fullName("Test Admin").roles(Set.of(admin)).build());

        var loaded = users.findWithRolesByUsername("test.admin").orElseThrow();
        assertThat(loaded.getRoles()).extracting("code").containsExactly("ADMIN");
    }
}
