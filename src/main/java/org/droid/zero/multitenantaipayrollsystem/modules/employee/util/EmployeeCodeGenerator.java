package org.droid.zero.multitenantaipayrollsystem.modules.employee.util;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Date;

@RequiredArgsConstructor
public class EmployeeCodeGenerator {

    public static String generate(Date joiningDate, EmployeeRepository employeeRepository) throws HibernateException {

        long count = employeeRepository.countByJoiningDate(joiningDate);
        LocalDate jDate = joiningDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        return "EMP-" + jDate.getYear() + String.format("%04d", count + 1) + jDate.getDayOfMonth() + jDate.getMonthValue();
    }
}
