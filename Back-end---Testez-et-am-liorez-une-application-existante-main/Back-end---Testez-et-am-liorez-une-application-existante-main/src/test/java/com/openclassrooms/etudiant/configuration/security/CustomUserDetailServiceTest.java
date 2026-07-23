package com.openclassrooms.etudiant.configuration.security;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    private static final String LOGIN = "agent";
    private static final String PASSWORD = "encoded-password";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsernameShouldReturnExistingUser() {
        // GIVEN : un utilisateur correspondant au login demandé
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setLogin(LOGIN);
        user.setPassword(PASSWORD);

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.of(user));

        // WHEN : Spring Security recherche l'utilisateur
        UserDetails result =
                customUserDetailService.loadUserByUsername(LOGIN);

        // THEN : l'utilisateur trouvé est retourné
        assertSame(user, result);
        assertEquals(LOGIN, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());

        verify(userRepository).findByLogin(LOGIN);
    }
}