package org.droid.zero.multitenantaipayrollsystem.modules.employee.util;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.hibernate.HibernateException;

import java.time.LocalDate;

@RequiredArgsConstructor
public class EmployeeCodeGenerator {

    public static String generate(LocalDate joiningDate, EmployeeRepository employeeRepository) throws HibernateException {

        long count = employeeRepository.countByJoiningDate(joiningDate);

        return "EMP-" + joiningDate.getYear() + String.format("%04d", count + 1) + joiningDate.getDayOfMonth() + joiningDate.getMonthValue();
    }
}
