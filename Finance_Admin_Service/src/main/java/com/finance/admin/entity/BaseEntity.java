package com.finance.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity class containing common audit fields
 * All entities should extend this class to inherit audit functionality
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Timestamp when the entity was created
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * User who created the entity
     */
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    /**
     * User who last updated the entity
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Soft delete flag
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * Pre-persist callback to set audit fields before saving
     */
    @PrePersist
    protected void onCreate() {
        if (createdBy == null) {
            createdBy = com.finance.admin.util.Constants.SYSTEM_USER;
        }
        if (updatedBy == null) {
            updatedBy = com.finance.admin.util.Constants.SYSTEM_USER;
        }
    }

    /**
     * Pre-update callback to set audit fields before updating
     */
    @PreUpdate
    protected void onUpdate() {
        if (updatedBy == null) {
            updatedBy = com.finance.admin.util.Constants.SYSTEM_USER;
        }
    }
}