package com.user.management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginNotificationRequest {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String loginTime;
}