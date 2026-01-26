package org.droid.zero.multitenantaipayrollsystem.modules.employee.util;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.hibernate.HibernateException;

import java.time.LocalDate;

@RequiredArgsConstructor
public class EmployeeCodeGenerator {

    public static String generate(LocalDate joiningDate, EmployeeRepository employeeRepository) {
        long attemptCount = 0;
        long count = employeeRepository.countByJoiningDate(joiningDate);
        String employeeCode = generateEmployeeCode(joiningDate, count);

        while (employeeRepository.existsByEmployeeCode(employeeCode)){
            attemptCount++;

            if (attemptCount % 3 == 0){
                count = employeeRepository.countByJoiningDate(joiningDate);
            } else if (attemptCount > 15){
                throw new HibernateException("Failed to generate unique employee code after 15 attempts.");
            } else {
                count++;
            }

            employeeCode = generateEmployeeCode(joiningDate, count);
        }

        return employeeCode;
    }

    private static String generateEmployeeCode(LocalDate joiningDate, long count) {
        return "EMP-" + joiningDate.getYear() + String.format("%04d", count + 1) + joiningDate.getDayOfMonth() + joiningDate.getMonthValue();
    }
}

