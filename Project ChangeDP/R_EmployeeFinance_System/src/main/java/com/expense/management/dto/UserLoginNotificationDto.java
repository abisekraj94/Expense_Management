package com.expense.management.dto;


import java.time.LocalDateTime;

public class UserLoginNotificationDto {
    private Long userId;
    private String email;
    private String fullname;
    private String role;
    private LocalDateTime loginTime;

    public UserLoginNotificationDto(Long userId, String email,String fullname, String role, LocalDateTime loginTime) {
        this.userId = userId;
        this.email = email;
        this.fullname=fullname;
        this.role = role;
        this.loginTime = loginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}