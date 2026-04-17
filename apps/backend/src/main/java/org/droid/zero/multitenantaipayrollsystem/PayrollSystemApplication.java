package org.droid.zero.multitenantaipayrollsystem;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayrollSystemApplication {

    public static void main(String[] args) {

        String profile = System.getenv("SPRING_PROFILES_ACTIVE");

        if (profile == null) {
            profile = "dev"; // fallback
        }

        if (!profile.equals("prod")) {
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env." + profile)
                    .load();

            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );

            System.out.println("Loaded .env for profile: " + profile);
        }

        SpringApplication.run(PayrollSystemApplication.class, args);
    }
}