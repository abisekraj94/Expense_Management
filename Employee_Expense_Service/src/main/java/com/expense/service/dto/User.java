package com.expense.service.dto;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean active;
}