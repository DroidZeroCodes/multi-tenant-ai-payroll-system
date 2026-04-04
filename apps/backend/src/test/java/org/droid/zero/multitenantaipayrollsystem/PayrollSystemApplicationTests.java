package org.droid.zero.multitenantaipayrollsystem;

import org.droid.zero.multitenantaipayrollsystem.test.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles(value = "test")
@SpringBootTest
class PayrollSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
