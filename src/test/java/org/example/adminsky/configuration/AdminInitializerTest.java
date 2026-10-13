package org.example.adminsky.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private final DefaultApplicationArguments args = new DefaultApplicationArguments();

    @Test
    void createsFirstAdminWithBCryptHash() {
        var users = mock(StaffUserRepository.class);
        var roles = mock(StaffRoleRepository.class);
        var encoder = new BCryptPasswordEncoder();
        when(users.count()).thenReturn(0L);
        when(roles.findByCode("ADMIN")).thenReturn(Optional.of(StaffRole.builder().code("ADMIN").build()));
        var initializer = new AdminInitializer(users, roles, encoder, " Admin ", "StrongPass123");

        initializer.run(args);

        var saved = org.mockito.ArgumentCaptor.forClass(StaffUser.class);
        verify(users).save(saved.capture());
        assertThat(saved.getValue().getUsername()).isEqualTo("admin");
        assertThat(encoder.matches("StrongPass123", saved.getValue().getPasswordHash())).isTrue();
    }

    @Test
    void rejectsMissingOrWeakPasswordWhenDatabaseIsEmpty() {
        var users = mock(StaffUserRepository.class);
        when(users.count()).thenReturn(0L);
        var initializer = new AdminInitializer(users, mock(StaffRoleRepository.class),
                new BCryptPasswordEncoder(), "admin", "weak");

        assertThatThrownBy(() -> initializer.run(args)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("password policy");
        verify(users, never()).save(any());
    }
}
