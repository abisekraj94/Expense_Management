package com.finance.admin.repository;

import com.finance.admin.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity
 * Provides database operations for employee management
 * 
 * @author Finance Team
 * @version 1.0.0
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Find employee by email address
     * 
     * @param email employee email
     * @return optional employee
     */
    Optional<Employee> findByEmailAndIsDeletedFalse(String email);

    /**
     * Find employees by department
     * 
     * @param department department name
     * @return list of employees
     */
    List<Employee> findByDepartmentAndIsDeletedFalse(String department);

    /**
     * Find employees with approved expenses
     * 
     * @return list of employees with approved expenses
     */
    @Query("SELECT DISTINCT e FROM Employee e " +
           "JOIN e.expenses exp " +
           "WHERE exp.status = 'APPROVED' " +
           "AND e.isDeleted = false " +
           "ORDER BY e.employeeName")
    List<Employee> findEmployeesWithApprovedExpenses();

    /**
     * Find employees by IDs with limit for reports
     * 
     * @param employeeIds list of employee IDs
     * @param limit       maximum number of employees
     * @return list of employees
     */
    @Query("SELECT e FROM Employee e " +
           "WHERE e.employeeId IN :employeeIds " +
           "AND e.isDeleted = false " +
           "ORDER BY e.employeeName")
    List<Employee> findByEmployeeIdInWithLimit(@Param("employeeIds") List<Long> employeeIds);
}