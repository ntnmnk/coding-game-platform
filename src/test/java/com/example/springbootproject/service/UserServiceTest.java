package com.example.springbootproject.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.springbootproject.entities.User;
import com.example.springbootproject.exception.UserAlreadyExistsException;
import com.example.springbootproject.exception.UserNotFoundException;
import com.example.springbootproject.reposiotries.UserRepository;
import com.example.springbootproject.services.UserServiceImpl;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                   .userId("1")
                   .username("user1")
                   .score(50)
                   .badges(Collections.emptySet())
                   .build();
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById("1");
        verify(userRepository).findByUsername("user1");
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUser_UserIdAlreadyExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository).findById("1");
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository).findById("1");
        verify(userRepository).findByUsername("user1");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("1"));
        verify(userRepository).findById("1");
    }
}
