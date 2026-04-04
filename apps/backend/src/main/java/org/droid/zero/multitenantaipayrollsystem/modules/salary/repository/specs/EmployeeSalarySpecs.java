package org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.specs;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeSalarySpecs {

    /**
     * Returns a Specification that checks if the provided salaryId is equal to the EmployeeSalary's salaryId.
     *
     * @param providedSalaryId the salaryId to check against
     * @return a Specification that checks if the provided salaryId is equal to the EmployeeSalary's salaryId
     */
    public static Specification<EmployeeSalary> hasSalaryId(UUID providedSalaryId){
        // Root is the FROM clause in SQL
        // Query is for Ordering, Distinct, and GroupingBy
        // Builder is to conduct search criteria (WHERE clause in SQL)
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salaryId"), providedSalaryId);
    }

    /**
     * Returns a Specification that checks if the provided employeeId is equal to the EmployeeSalary's employeeId.
     *
     * @param providedEmployeeId the employeeId to check against
     * @return a Specification that checks if the provided employeeId is equal to the EmployeeSalary's employeeId
     */
    public static Specification<EmployeeSalary> hasEmployeeId(UUID providedEmployeeId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("employeeId"), providedEmployeeId);
    }

    /**
     * Returns a Specification that checks if the provided date is greater than or equal to the EmployeeSalary's
     * effectiveDate. Additionally, it orders the result by effectiveDate in descending order and distincts the result.
     *
     * @param providedDate the date to check against
     * @return a Specification that checks if the provided date is greater than or equal to the EmployeeSalary's
     *         effectiveDate
     */
    public static Specification<EmployeeSalary> effectiveOn(LocalDate providedDate){
        return (root, query, criteriaBuilder) ->
        {
            // Order the result by effectiveDate in descending order
            if (query != null) {
                query.distinct(true).orderBy(criteriaBuilder.desc(root.get("effectiveDate")));
            }
            // Check if the provided date is greater than or equal to the EmployeeSalary's effectiveDate
            return criteriaBuilder.greaterThanOrEqualTo(root.get("effectiveDate"), providedDate);
        };
    }
}
