package com.example.springbootproject.entities;

import java.util.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
public class User {

  @Id
  private String userId;

  private String username;

  
  private int score;

 
  private Set<String> badges = new HashSet<>();
}
