package org.example.adminsky.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.example.adminsky.entity.StaffRole;
import org.example.adminsky.entity.StaffUser;
import org.example.adminsky.repository.StaffRoleRepository;
import org.example.adminsky.repository.StaffUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AdminInitializerTest {
    @Test
    void createsAdminWithBCryptOnlyWhenDatabaseIsEmpty() {
        var users = mock(StaffUserRepository.class);
        var roles = mock(StaffRoleRepository.class);
        var encoder = new BCryptPasswordEncoder();
        when(users.count()).thenReturn(0L);
        when(roles.findByCode("ADMIN")).thenReturn(Optional.of(StaffRole.builder().code("ADMIN").build()));
        var initializer = new AdminInitializer(users, roles, encoder);
        org.springframework.test.util.ReflectionTestUtils.setField(initializer, "username", " Admin ");
        org.springframework.test.util.ReflectionTestUtils.setField(initializer, "password", "StrongPass123");

        initializer.run(new DefaultApplicationArguments());

        var saved = org.mockito.ArgumentCaptor.forClass(StaffUser.class);
        verify(users).save(saved.capture());
        assertThat(saved.getValue().getUsername()).isEqualTo("admin");
        assertThat(encoder.matches("StrongPass123", saved.getValue().getPasswordHash())).isTrue();
    }
}
