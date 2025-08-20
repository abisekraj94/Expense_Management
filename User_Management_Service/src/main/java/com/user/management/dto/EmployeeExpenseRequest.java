package com.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeExpenseRequest {
    private Long userId;
    private String userEmail;
    private String userName;
    private String department;
}