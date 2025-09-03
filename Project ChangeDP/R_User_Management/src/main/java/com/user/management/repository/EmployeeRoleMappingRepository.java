package com.user.management.repository;

import com.user.management.entity.EmployeeRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for EmployeeRoleMapping entity
 * Provides database operations for employee-role mapping management
 * 
 * @author User Management Team
 * @version 1.0
 */
@Repository
public interface EmployeeRoleMappingRepository extends JpaRepository<EmployeeRoleMapping, Long> {
}