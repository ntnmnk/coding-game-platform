package com.example.springbootproject.controller;

import com.example.springbootproject.entities.User;
import com.example.springbootproject.exception.UserAlreadyExistsException;
import com.example.springbootproject.exception.UserNotFoundException;
import com.example.springbootproject.services.UserService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private static final Logger logger = LogManager.getLogger(
    UserController.class
  );

  @Autowired
  private UserService userService;

  @GetMapping("/all")
  public ResponseEntity<List<User>> getAllUsers() {
    try {
      logger.info("Fetching all users");

      List<User> users = userService.getAllUsers();

      if (users != null) {
        logger.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
      } else {
        logger.warn("No users found");
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      logger.error("An unexpected error occurred while fetching all users", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{userId}")
  public ResponseEntity<User> getUserById(@PathVariable String userId) {
    try {
      logger.info("Fetching user with userId: {}", userId);
      User user = userService.getUserById(userId);

      logger.info("Fetched user: {}", user);

      return ResponseEntity.ok(user);
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<Object> registerUser(@RequestBody User user)
    throws UserAlreadyExistsException {
    try {
      logger.info("Registering new user: {}", user);

      if (user.getUsername().isEmpty() || user.getUserId().isEmpty()) {
        String errorMessage = "User ID and Username must not be empty";
        logger.error(errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
      }

      // Check if user already exists
      if (userService.exists(user.getUserId())) {
        String errorMessage =
          "User with ID " + user.getUserId() + " already exists";
        logger.error(errorMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
      }

      // Register the user
      User registeredUser = userService.registerUser(user);
      logger.info("User registered successfully: {}", registeredUser);
      return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      logger.error("An unexpected error occurred while registering user", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/{userId}")
  public ResponseEntity<User> updateScore(
    @PathVariable String userId,
    @RequestParam int score
  ) {
    try {
     

      logger.info("Updating score for user with userId: {}", userId);
      User updatedUser = userService.updateScore(userId, score);
      logger.info("Score updated for user: {}", updatedUser);

      return ResponseEntity.ok(updatedUser);
    } 
    catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
    catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deregisterUser(@PathVariable String userId) {
    try {
      logger.info("Deregistering user with userId: {}", userId);

      userService.deregisterUser(userId);
      logger.info("User deregistered successfully");

      return ResponseEntity.noContent().build();
    } catch (UserNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
