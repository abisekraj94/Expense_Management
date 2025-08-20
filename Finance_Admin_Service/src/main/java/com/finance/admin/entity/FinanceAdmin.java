package com.finance.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Finance Admin entity representing finance administrators
 * Contains admin credentials and profile information
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Entity
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceAdmin extends BaseEntity {

    /**
     * Primary key for finance admin
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    /**
     * Admin's username for login
     */
    @NotBlank(message = "${validation.username.required}")
    @Size(min = 3, max = 50, message = "${validation.username.min.length}")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Admin's encrypted password
     */
    @NotBlank(message = "${validation.password.required}")
    @Size(min = 8, message = "${validation.password.min.length}")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Admin's full name
     */
    @NotBlank(message = "${validation.name.required}")
    @Size(max = 100, message = "${validation.name.max.length}")
    @Column(name = "full_name", nullable = false, length = 100)
    private String adminName;

    /**
     * Admin's email address
     */
    @NotBlank(message = "${validation.email.required}")
    @Email(message = "${validation.email.invalid}")
    @Size(max = 150, message = "${validation.email.max.length}")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Admin's role in the system
     */
    @Size(max = 50, message = "${validation.role.max.length}")
    @Column(name = "role", length = 50)
    @Builder.Default
    private String role = com.finance.admin.util.Constants.ROLE_FINANCE_ADMIN;

    /**
     * Whether the admin account is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}