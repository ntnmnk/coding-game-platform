package com.example.springbootproject.services;

import com.example.springbootproject.entities.User;
import com.example.springbootproject.exception.UserAlreadyExistsException;
import com.example.springbootproject.exception.UserNotFoundException;
import com.example.springbootproject.reposiotries.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAllByOrderByScoreDesc();
  }

  @Override
  public User getUserById(String userId) {
    return userRepository
      .findById(userId)
      .orElseThrow(() ->
        new UserNotFoundException("User not found with id: " + userId)
      );
  }

  @Override
  public User registerUser(User user) {
    userRepository
      .findById(user.getUserId())
      .ifPresent(existingUser -> {
        throw new UserAlreadyExistsException(
          "User with userId '" + user.getUserId() + "' already exists."
        );
      });

    userRepository
      .findByUsername(user.getUsername())
      .ifPresent(existingUser -> {
        throw new UserAlreadyExistsException(
          "User with username '" + user.getUsername() + "' already exists."
        );
      });

    user.setBadges(Collections.EMPTY_SET);
    return userRepository.save(user);
  }

  @Override
  public User updateScore(String userId, int score) {
    if(score<0&&score>100){
      throw new IllegalArgumentException("Score must be between 0 and 100");
    }
    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new UserNotFoundException("User not found with id: " + userId)
      );

    user.setScore(score);
    awardBadges(user);
    return userRepository.save(user);
  }

  @Override
  public void deregisterUser(String userId) {
    userRepository
      .findById(userId)
      .ifPresentOrElse(
        user -> {
          userRepository.deleteById(userId);
        },
        () -> {
          throw new UserNotFoundException(
            "User with userId '" + userId + "' not found."
          );
        }
      );
  }

  private void awardBadges(User user) {
    if (user.getScore() >= 1 && user.getScore() <= 30) {
      user.getBadges().add("Code Ninja");
    } else if (user.getScore() > 30 && user.getScore() <= 60) {
      user.getBadges().add("Code Champ");
    } else if (user.getScore() > 60 && user.getScore() <= 100) {
      user.getBadges().add("Code Master");
    }
  }

  @Override
  public boolean exists(String userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    return userOptional.isPresent();
  }
}
