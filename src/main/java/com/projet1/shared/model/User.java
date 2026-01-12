package com.projet1.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Example model class using Lombok annotations.
 * 
 * @Data generates getters, setters, toString, equals, hashCode
 * @Builder enables the builder pattern
 * @NoArgsConstructor generates a no-args constructor
 * @AllArgsConstructor generates a constructor with all fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private boolean connected;

    public static User fromBinary(String rawUser) {
        String[] parts = rawUser.split(",", 3);
        if (parts.length < 2) return null;

        User user = new User();
            user.setId(Long.parseLong(parts[0]));
            user.setUsername(parts[1]);
        if (parts.length >= 3)
            user.setConnected(Integer.parseInt(parts[2]) != 0);
        else
            user.setConnected(false);
        return user;
    } 
}
