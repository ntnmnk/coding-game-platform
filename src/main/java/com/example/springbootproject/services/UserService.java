package com.example.springbootproject.services;

import com.example.springbootproject.entities.User;
import java.util.List;

public interface UserService {
  List<User> getAllUsers();
  User getUserById(String userId);
  User registerUser(User user);
  User updateScore(String userId, int score);
  void deregisterUser(String userId);

  public boolean exists(String userId);
}
