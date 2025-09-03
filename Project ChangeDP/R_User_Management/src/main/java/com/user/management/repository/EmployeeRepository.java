package com.user.management.repository;

import com.user.management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Employee entity
 * Provides database operations for employee management
 * 
 * @author User Management Team
 * @version 1.0
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find employee by email ID
     * 
     * @param emailId the email ID to search for
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmailId(String emailId);

    /**
     * Check if employee exists by email ID
     * 
     * @param emailId the email ID to check
     * @return true if employee exists, false otherwise
     */
    boolean existsByEmailId(String emailId);

    /**
     * Find employee with roles by email ID
     * 
     * @param emailId the email ID to search for
     * @return Optional containing the employee with roles if found
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.employeeRoleMappings erm LEFT JOIN FETCH erm.role WHERE e.emailId = :emailId")
    Optional<Employee> findByEmailIdWithRoles(@Param("emailId") String emailId);
}