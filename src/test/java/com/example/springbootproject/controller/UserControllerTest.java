package com.example.springbootproject.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.springbootproject.entities.User;
import com.example.springbootproject.exception.UserAlreadyExistsException;
import com.example.springbootproject.exception.UserNotFoundException;
import com.example.springbootproject.services.UserService;





@SpringBootTest(classes = {SpringBootApplication.class})
@AutoConfigureMockMvc
// @WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build(); 
        MockitoAnnotations.openMocks(this);
        user1 = User.builder()
                    .userId("1")
                    .username("user1")
                    .score(50)
                    .badges(Set.of("badge1", "badge2"))
                    .build();
        user2 = User.builder()
                    .userId("2")
                    .username("user2")
                    .score(75)
                    .badges(Set.of("badge3"))
                    .build();
    }
    @Test
    void testGetAllUsers_mvc() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userService.getAllUsers()).thenReturn(users);
        String uri = "/users/all";

        
        
        mockMvc.perform(get(uri.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].userId").value("1"))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[1].userId").value("2"))
            .andExpect(jsonPath("$[1].username").value("user2"));
    }
    

    


    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> responseEntity = userController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(users, responseEntity.getBody());
    }

    @Test
    void testGetUserById_UserFound() throws UserNotFoundException {
        when(userService.getUserById("1")).thenReturn(user1);

        ResponseEntity<User> responseEntity = userController.getUserById("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user1, responseEntity.getBody());
    }

    @Test
    void testGetUserById_UserNotFound() throws UserNotFoundException {
        when(userService.getUserById(anyString())).thenThrow(new UserNotFoundException(""));

        ResponseEntity<User> responseEntity = userController.getUserById("1");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testRegisterUser_Success() throws UserAlreadyExistsException {
        when(userService.registerUser(any())).thenReturn(user1);

        ResponseEntity<Object> responseEntity = userController.registerUser(user1);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(user1, responseEntity.getBody());
    }
}
