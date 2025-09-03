package com.expense.management.repository;

import com.expense.management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Employee entity
 * Provides database operations for employee management
 * 
 * @author System
 * @version 1.0.0
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds employee by email ID
     * 
     * @param emailId employee email ID
     * @return Optional containing employee if found
     */
    Optional<Employee> findByEmailId(String emailId);

}