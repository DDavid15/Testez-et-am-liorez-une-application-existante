package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.User;
import com.openclassrooms.etudiant.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
    private static final String ENCODED_PASSWORD = "ENCODED_PASSWORD";
    private static final String JWT_TOKEN = "JWT_TOKEN";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerNullUserShouldThrowIllegalArgumentException() {
        // GIVEN : aucun utilisateur n'est fourni

        // WHEN / THEN : l'inscription est refusée
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(null)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerExistingUserShouldThrowIllegalArgumentException() {
        // GIVEN : un utilisateur possède déjà ce login
        User user = createUser(PASSWORD);

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.of(user));

        // WHEN / THEN : l'inscription est refusée
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(user)
        );

        verify(userRepository).findByLogin(LOGIN);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldEncodePasswordAndSaveUser() {
        // GIVEN : le login n'est pas encore utilisé
        User user = createUser(PASSWORD);

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        // WHEN : l'utilisateur est inscrit
        userService.register(user);

        // THEN : le mot de passe est encodé avant la sauvegarde
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).findByLogin(LOGIN);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser).isEqualTo(user);
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
    }

    @Test
    void loginShouldReturnJwtTokenWhenCredentialsAreValid() {
        // GIVEN : un utilisateur enregistré et un mot de passe valide
        User user = createUser(ENCODED_PASSWORD);

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD))
                .thenReturn(true);

        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn(JWT_TOKEN);

        // WHEN : l'utilisateur se connecte
        String result = userService.login(LOGIN, PASSWORD);

        // THEN : le token JWT est retourné
        assertEquals(JWT_TOKEN, result);

        verify(userRepository).findByLogin(LOGIN);
        verify(passwordEncoder).matches(
                PASSWORD,
                ENCODED_PASSWORD
        );

        ArgumentCaptor<UserDetails> userDetailsCaptor =
                ArgumentCaptor.forClass(UserDetails.class);

        verify(jwtService).generateToken(userDetailsCaptor.capture());

        assertEquals(
                LOGIN,
                userDetailsCaptor.getValue().getUsername()
        );
    }

    private User createUser(String password) {
        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setLogin(LOGIN);
        user.setPassword(password);
        return user;
    }
}