package org.droid.zero.multitenantaipayrollsystem;

import org.springframework.boot.SpringApplication;

public class TestAiPoweredPayrollSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(PayrollSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
